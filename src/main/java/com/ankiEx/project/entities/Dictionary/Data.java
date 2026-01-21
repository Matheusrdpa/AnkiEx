package com.ankiEx.project.entities.Dictionary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Data {
    @JsonProperty("japanese")
    private List<Japanese> japanese;
    @JsonProperty("senses")
    private List<Senses> senses;

    public List<Japanese> getJapanese() {
        return japanese;
    }

    public void setJapanese(List<Japanese> japanese) {
        this.japanese = japanese;
    }

    public List<Senses> getSenses() {
        return senses;
    }

    public void setSenses(List<Senses> senses) {
        this.senses = senses;
    }
}
