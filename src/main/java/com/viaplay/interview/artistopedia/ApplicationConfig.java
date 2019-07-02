package com.viaplay.interview.artistopedia;

import com.viaplay.interview.artistopedia.common.ApiLogHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Component
@EnableCaching
@Data
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "artistopedia")
@Validated
public class ApplicationConfig {


    @NotBlank
    private String baseMusicBrainzUrl;

    @NotNull
    private Integer cacheExpirationTime;

    @NotNull
    private Long cacheMaxSizeElements;

    @NotNull
    private Discogs discogs;

    @NotNull
    private Covers covers;

    @Data
    public static class Covers{

        @NotBlank
        private String baseUrl;

        @NotBlank
        private String fallbackErrorMessage;
    }


    @Data
    public static class Discogs{

        @NotBlank
        private String baseUrl;

        @NotBlank
        private String fallbackErrorMessage;


    }

    /** @Bean
    public CacheManager cacheManager(){
        return new EhCacheCacheManager(cacheManagerFactory().getObject());
    }

    @Bean
    public EhCacheManagerFactoryBean cacheManagerFactory(){
        EhCacheManagerFactoryBean  cacheFactory = new EhCacheManagerFactoryBean();
        cacheFactory.setConfigLocation(new ClassPathResource("ehcache.xml"));
        cacheFactory.setShared(true);
        return cacheFactory;

    } **/

    @Bean
    public WebClient.Builder getWebClientBuilder(){
        return WebClient.builder()
                  .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .filter(ApiLogHandler.logRequest()).filter(ApiLogHandler.logResponse()).
          //pls refer to https://github.com/reactor/reactor-netty/issues/756
        clientConnector(new ReactorClientHttpConnector(HttpClient.newConnection().
                  compress(true).followRedirect(true)));

    }


}
