package com.ankiEx.project.services;

import com.ankiEx.project.entities.Dictionary.Jishoresponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnkiService {
    private Logger logger = LoggerFactory.getLogger(AnkiService.class);
    private RestClient restClient;
    private final ObjectMapper mapper = new ObjectMapper();
    private final DictionaryService dictionaryService;

    public AnkiService(@Qualifier("ankiClient") RestClient restClient, DictionaryService dictionaryService) {
        this.restClient = restClient;
        this.dictionaryService = dictionaryService;
    }

    public void addNote(String word, String sentence){

       Jishoresponse jishoresponse = dictionaryService.getWordData(word);

       String vocabularyFurigana = jishoresponse.getData().getFirst().getJapanese().getFirst().getReading();

       String vocabularyKanji = jishoresponse.getData().getFirst().getJapanese().getFirst().getWord();
       if (vocabularyKanji == null || vocabularyKanji.equals("")){
           vocabularyKanji = vocabularyFurigana;
       }

       List<String> vocabularyEnglish = jishoresponse.getData().getFirst().getSenses().getFirst().getEnglishDefinitions();

       String english = String.join("/", vocabularyEnglish);

       List<String> partOfSpeech = jishoresponse.getData().getFirst().getSenses().getFirst().getPartsOfSpeech();

       String pos = String.join("/", partOfSpeech);

       Map<String,Object> payload = new HashMap<>();
       payload.put("action", "addNote");
       payload.put("version", 6);

       Map<String,Object> params = new HashMap<>();
       Map<String,Object> note = new HashMap<>();
       note.put("deckName", "New one for testing");
       note.put("modelName", "Mining");

       Map<String,Object> fields = new HashMap<>();
       fields.put("Vocabulary-Kanji", vocabularyKanji);
       fields.put("Vocabulary-Furigana", vocabularyKanji + "[" + vocabularyFurigana + "]");
       fields.put("Vocabulary-Kana", vocabularyFurigana);
       fields.put("Vocabulary-English", english);
       fields.put("Expression", sentence);
       fields.put("Vocabulary-Pos", pos);
       fields.put("Sentence-English", "Not Yet defined"); // translation has to be added



       note.put("fields", fields);
       params.put("note", note);
       payload.put("params", params);

        if (noteAlreadyExists()){
            logger.warn("Card already exists");
            return;
        }

       String json = mapper.writeValueAsString(payload);

        try {
             String res = restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(json)
                    .retrieve()
                    .body(String.class);

            logger.warn("Anki Response: " + res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean noteAlreadyExists(){
        Map<String,Object> payload = new HashMap<>();
        payload.put("action", "findNotes");
        payload.put("version", 6);
        Map<String,Object> params = new HashMap<>();
        params.put("query", "\"deck:New one for testing\" \"Vocabulary-Kanji:Tested\"");
        payload.put("params", params);

        String json = mapper.writeValueAsString(payload);

        try {
            Map res = restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(json)
                    .retrieve()
                    .body(Map.class);
            List<String> list = (List<String>) res.get("result");
            if (list.isEmpty()){
                return false;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}


