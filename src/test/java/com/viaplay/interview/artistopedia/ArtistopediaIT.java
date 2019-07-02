package com.viaplay.interview.artistopedia;

import com.viaplay.interview.artistopedia.model.ArtistInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ArtistopediaIT {


    private static final String BASE_CONTEXT_URL = "/artistopedia/artistInfo/";


    @Autowired
    private WebTestClient webTestClient;


    @Test
    public void simpleSearch_ShouldSucceed(){

        webTestClient.get().uri(BASE_CONTEXT_URL+"5b11f4ce-a62d-471e-81fc-a69a8278c7da")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk().expectBody(ArtistInfo.class);
    }


}
