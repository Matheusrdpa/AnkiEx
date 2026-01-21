package com.ankiEx.project.services;

import com.ankiEx.project.entities.Dictionary.Data;
import com.ankiEx.project.entities.Dictionary.Jishoresponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class DictionaryService {
    private RestClient restClient;
    public DictionaryService(@Qualifier("jishoClient")RestClient restClient) {
        this.restClient = restClient;
    }

    public Jishoresponse getWordData(String kanji){
       return restClient.get()
               .uri(uriBuilder -> uriBuilder.queryParam("keyword", kanji)
               .build())
               .retrieve()
               .body(Jishoresponse.class);
    }

}

