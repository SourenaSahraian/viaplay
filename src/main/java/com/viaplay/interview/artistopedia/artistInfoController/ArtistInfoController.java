package com.viaplay.interview.artistopedia.artistInfoController;

import com.viaplay.interview.artistopedia.model.ArtistInfo;
import com.viaplay.interview.artistopedia.service.ArtistInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/artistopedia")
public class ArtistInfoController {


    private final ArtistInfoService artistInfoService;

    @Autowired
    public ArtistInfoController(ArtistInfoService artistInfoService) {
        this.artistInfoService = artistInfoService;
    }

    /**
     * Note: mbid is not the best choice of name , but I assume
     * here it has a business connotation.
     *
     * @param mbid
     * @return
     */
    @GetMapping("/artistInfo/{mbid}")
    public Mono<ArtistInfo> getArtistInfo(@PathVariable String mbid) {

        return artistInfoService.getArtistInfo(mbid);

    }

}
