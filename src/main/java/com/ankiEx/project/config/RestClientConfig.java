package com.ankiEx.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Bean("ankiClient")
    public RestClient getRestClient() {
        return RestClient.builder().baseUrl("http://localhost:8765").build();
    }

    @Bean("jishoClient")
    public RestClient jishoClient(){
        return RestClient.builder().baseUrl("https://jisho.org/api/v1/search/words").build();
    }
}
