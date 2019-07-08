package com.viaplay.interview.artistopedia.service;


import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.viaplay.interview.artistopedia.ApplicationConfig;
import com.viaplay.interview.artistopedia.common.ClientRequestHandler;
import com.viaplay.interview.artistopedia.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
public class ArtistInfoServiceImpl implements ArtistInfoService {

    public static final String DISCOGS = "discogs";

    @Autowired
    private ClientRequestHandler clientRequestHandler;

    @Autowired
    private ApplicationConfig config;

    AsyncLoadingCache<String, ArtistInfo> cache = null;

    private static boolean hasDiscogsRelation(Relations relation) {
        return relation.getType() != null && relation.getType().equalsIgnoreCase(DISCOGS);
    }

    @PostConstruct
    public void setup() {
        cache = Caffeine.newBuilder()
                .expireAfterWrite(config.getCacheExpirationTime(), TimeUnit.MINUTES)
                .maximumSize(config.getCacheMaxSizeElements())
                // Build with a asynchronous computation that returns a future
                .buildAsync((key, executor) -> getArtistAsync(key).toFuture());


    }

    @Override
    public Mono<ArtistInfo> getArtistInfo(String key) {
        // Lookup and asynchronously compute an entry if absent
        return Mono.fromFuture(cache.get(key));

    }

    // @Cacheable(value = "artistCache", key = "#mbid")
    public Mono<ArtistInfo> getArtistAsync(String mbid) {

        Mono<MusicBrainsResponse> musicBrainsResponse =
                (Mono<MusicBrainsResponse>) clientRequestHandler.executeRequestMono(getMusicBrainsRequestUri(mbid), MusicBrainsResponse.class);

        Albums.AlbumsBuilder albumBuilder = Albums.builder();
        final ArtistInfo.ArtistInfoBuilder artistInfoBuilder = ArtistInfo.builder();

        ArtistInfo artInfo = artistInfoBuilder.build();


        return musicBrainsResponse.flatMap(musicBrainsResp ->
        {

            //list all albums for one request - still need the associated images for each album
            List<Albums> albums = musicBrainsResp.getReleaseGroups().stream()
                    .map(releaseGroup -> albumBuilder.title(releaseGroup.getTitle()).
                            id(releaseGroup.getId()).build()).collect(Collectors.toList());

            artInfo.setMbid(mbid);
            artInfo.setAlbums(albums);

            //this call is independent of covers - fire away
            Mono<DiscogsResponse> discogsResponseMono = (Mono<DiscogsResponse>)
                    clientRequestHandler.executeRequestMono(getDiscogzRequestUri(musicBrainsResp).get(), DiscogsResponse.class);

            Mono<ArtistInfo> discoMono = discogsResponseMono
                    .map(discogsResponse -> buildArtistInfo(discogsResponse.getProfile(), artInfo))
                    .onErrorResume(e -> fallBackOnUnsuccessDescription(artInfo));


            Mono<ArtistInfo> coverImagesMono = Flux.merge(albums.stream().map(album -> {
                //GET covers - fire away
                Mono<CoverResponse> coverResponseMono = (Mono<CoverResponse>)
                        clientRequestHandler.executeRequestMono(config.getCovers().getBaseUrl() + album.getId(), CoverResponse.class);

                //2.call and handler for each image
                //shortcut- if multiple images, pick the first one
                return coverResponseMono
                        .onErrorResume(e -> Mono.just(CoverResponse.builder().
                                images(initNoCoverFound()).build())).retry(1)
                        .map(coverResponse -> extractCoverImages(coverResponse.getImages().get(0).getImage(), album,artInfo));

            }).collect(Collectors.toList())).then(Mono.just(artInfo));


            return Mono.zip(coverImagesMono, discoMono, (covers, profileInfo) -> {
                //both futures are resolved at this point- could return either one
                return covers;
            });

        });

    }

    private Mono<ArtistInfo> fallBackOnUnsuccessDescription(ArtistInfo artistInfo) {
        artistInfo.setDescription(config.getDiscogs().getFallbackErrorMessage());
        return Mono.just(artistInfo);

    }

    private List<CoverImageTypes> initNoCoverFound() {

        List<CoverImageTypes> coverImageTypes = new ArrayList<>();
        coverImageTypes.add(CoverImageTypes.builder().image(config.getCovers().getFallbackErrorMessage()).build());
        return coverImageTypes;
    }

    private String extractIdFromUri(String str) {
        String[] urlArray = str.split("/");
        return urlArray[urlArray.length - 1];
    }

    private ArtistInfo buildArtistInfo(String description, ArtistInfo artistInfo) {

        artistInfo.setDescription(description);
        return artistInfo;
    }

    private Albums extractCoverImages(String image, Albums album, ArtistInfo artistInfo) {

        album.setImageUri(image);
        artistInfo.getAlbums().add(album);
        return album;
    }

    private Optional<String> getDiscogzRequestUri(MusicBrainsResponse musicBrainsResponse) {

        List<Relations> discogs = musicBrainsResponse.getRelations().stream().
                filter(ArtistInfoServiceImpl::hasDiscogsRelation).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(discogs)) {
            return Optional.empty();
        }
        //I assume any of the potential multiple discogz objects can provide the profile info

        return Optional.ofNullable(config.getDiscogs().getBaseUrl() + "/" + extractIdFromUri(discogs.get(0).getUrl().getResource()));
    }

    private String getMusicBrainsRequestUri(String mbid) {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.put("fmt", Arrays.asList("json"));
        params.put("inc", Arrays.asList("url-rels+release-groups"));
        String base = config.getBaseMusicBrainzUrl() + mbid;
        String createdUrl = getEncodedUrl(base, params);
        return createdUrl;

    }

    private String getEncodedUrl(String url, MultiValueMap<String, String> params) {

        return UriComponentsBuilder.fromHttpUrl(url)
                .queryParams(params)
                .build().toUriString();
    }

}
