package com.ankiEx.project.entities.Subtitles;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Segs {
    @JsonProperty("utf8")
    private String utf8;

    public String getUtf8() {
        return utf8;
    }

    public void setUtf8(String utf8) {
        this.utf8 = utf8;
    }
}
