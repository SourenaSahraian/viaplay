package com.viaplay.interview.artistopedia.service;

import com.viaplay.interview.artistopedia.model.ArtistInfo;

import reactor.core.publisher.Mono;

public interface ArtistInfoService {

      Mono<ArtistInfo> getArtistInfo(String mbid);

}
