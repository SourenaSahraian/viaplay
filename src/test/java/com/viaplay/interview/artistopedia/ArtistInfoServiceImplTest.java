package com.viaplay.interview.artistopedia;

import com.viaplay.interview.artistopedia.common.ClientRequestHandler;
import com.viaplay.interview.artistopedia.model.*;
import com.viaplay.interview.artistopedia.service.ArtistInfoServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;


public class ArtistInfoServiceImplTest {

    private static final String EXPECTED_RESPONSE_ALBUM_IMAGE_URI = "SOME_IMAGE_URI";
    private static final String EXPECTED_RESPONSE_DESCIPTION = "Some Description";
    private static final String mbid = "5b11f4ce-a62d-471e-81fc-a69a8278c7da";
    private static final String MUSIC_COVERS_BASE_URI = "http://coverartarchive.org/release-group/";
    private static final String MUSIC_BASE_URI = "http://musicbrainz.org/ws/2/artist/";
    private static final String MUSIC_BASE_DISCOGZ_URI = "https://api.discogs.com/artists";
    private static final String MUSIC_BRAINS_TEST_REQUEST_URI = "http://musicbrainz.org/ws/2/artist/5b11f4ce-a62d-471e-81fc-a69a8278c7da?fmt=json&inc=url-rels+release-groups";
    private static final String COVERS_TEST_REUEST_URI = "http://coverartarchive.org/release-group/123";
    private static final String DISCOGZ_TEST_REQUEST_URI = "https://api.discogs.com/artists/SOME_DISCOGZ_RESOURCE";

    @InjectMocks
    private ArtistInfoServiceImpl serviceImpl;

    @Mock
    private ApplicationConfig config;

    @Mock
    private ClientRequestHandler clientRequestHandlerMock;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        List<ReleaseGroups> releaseGroupsList = new ArrayList<>();
        final ReleaseGroups releaseGroup1 = ReleaseGroups.builder().id("123").title("title1").build();
        releaseGroupsList.add(releaseGroup1);

        List<Relations> relations = new ArrayList<>();
        Relations discogs1 = Relations.builder().type("discogs").url(Url.builder().resource("SOME_DISCOGZ_RESOURCE").build()).build();
        relations.add(discogs1);

        Mono<MusicBrainsResponse> musicResponseMono = Mono.just(MusicBrainsResponse.builder()
                .relations(relations).releaseGroups(releaseGroupsList).build());

        List<CoverImageTypes> coverImageTypes = new ArrayList<>();

        CoverImageTypes coverImageType1 = CoverImageTypes.builder().image("SOME_IMAGE_URI").id("SOME_ID").build();
        coverImageTypes.add(coverImageType1);

        Mono<CoverResponse> coverResponseMono = Mono.just(CoverResponse.builder().images(coverImageTypes).build());
        Mono<DiscogsResponse> discogsResponseMono = Mono.just(DiscogsResponse.builder().profile("Some Description").build());

        when(config.getBaseMusicBrainzUrl()).thenReturn(MUSIC_BASE_URI);
        when(config.getCovers().getBaseUrl()).thenReturn(MUSIC_COVERS_BASE_URI);
        when(config.getDiscogs().getBaseUrl()).thenReturn(MUSIC_BASE_DISCOGZ_URI);

        when((Mono<MusicBrainsResponse>) clientRequestHandlerMock.executeRequestMono(MUSIC_BRAINS_TEST_REQUEST_URI, MusicBrainsResponse.class))
                .thenReturn(musicResponseMono);

        when((Mono<CoverResponse>) clientRequestHandlerMock.executeRequestMono(COVERS_TEST_REUEST_URI, CoverResponse.class))
                .thenReturn(coverResponseMono);

        when((Mono<DiscogsResponse>) clientRequestHandlerMock.executeRequestMono(DISCOGZ_TEST_REQUEST_URI, DiscogsResponse.class))
                .thenReturn(discogsResponseMono);

    }

    @Test
    public void testGetArtistAsync_ShouldSucceed() {
        ArtistInfo artistInfoResponse = serviceImpl.getArtistAsync(mbid).block();

        assertNotNull(artistInfoResponse);
        assertFalse(artistInfoResponse.getAlbums().isEmpty());
        assertEquals(EXPECTED_RESPONSE_DESCIPTION, artistInfoResponse.getDescription());
        assertEquals(EXPECTED_RESPONSE_ALBUM_IMAGE_URI, artistInfoResponse.getAlbums().get(0).getImageUri().toString());

    }

}
