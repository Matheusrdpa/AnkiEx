package com.ankiEx.project.services;

import com.ankiEx.project.entities.ai.MorphemeDto;
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

    public AnkiService(@Qualifier("ankiClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public void addNote(MorphemeDto morpheme, String sentence,String sentenceFurigana, String deckName, String sentenceTranslation){

       String vocabularyFurigana = morpheme.reading();

       String vocabularyKanji = morpheme.surface();

       String vocabularyEnglish = morpheme.meaning();

       String pos = morpheme.pos();

       Map<String,Object> payload = new HashMap<>();
       payload.put("action", "addNote");
       payload.put("version", 6);

       Map<String,Object> params = new HashMap<>();
       Map<String,Object> note = new HashMap<>();
       note.put("deckName", deckName);
       note.put("modelName", "Mining");

       Map<String,Object> fields = new HashMap<>();
       fields.put("Vocabulary-Kanji", vocabularyKanji);
       fields.put("Vocabulary-Furigana", " " + sentenceFurigana);
       fields.put("Vocabulary-Kana", vocabularyFurigana);
       fields.put("Vocabulary-English", vocabularyEnglish);
       fields.put("Expression", sentence);
       fields.put("Vocabulary-Pos", pos);
       fields.put("Sentence-English", sentenceTranslation);

       note.put("fields", fields);
       params.put("note", note);
       payload.put("params", params);

        if (noteAlreadyExists(deckName,vocabularyKanji)){
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

    public boolean noteAlreadyExists(String deckName,String word){
        Map<String,Object> payload = new HashMap<>();
        payload.put("action", "findNotes");
        payload.put("version", 6);
        Map<String,Object> params = new HashMap<>();
        String query = "deck:\"" + deckName + "\" \"Vocabulary-Kanji:" + word + "\"";
        params.put("query", query);
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


