package com.ankiEx.project.entities.Dictionary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Senses {

    @JsonProperty("english_definitions")
    private List<String> englishDefinitions;
    @JsonProperty("parts_of_speech")
    private List<String> partsOfSpeech;

    public List<String> getEnglishDefinitions() {
        return englishDefinitions;
    }

    public void setEnglishDefinitions(List<String> englishDefinitions) {
        this.englishDefinitions = englishDefinitions;
    }

    public List<String> getPartsOfSpeech() {
        return partsOfSpeech;
    }

    public void setPartsOfSpeech(List<String> partsOfSpeech) {
        this.partsOfSpeech = partsOfSpeech;
    }
}
