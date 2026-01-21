package com.ankiEx.project.entities.Subtitles;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Events {
    @JsonProperty("tStartMs")
    private Long tStartMs;
    @JsonProperty("dDurationMs")
    private Long durationMs;
    @JsonProperty("segs")
    private List<Segs> segs;

    public Long gettStartMs() {
        return tStartMs;
    }

    public void settStartMs(Long tStartMs) {
        this.tStartMs = tStartMs;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public List<Segs> getSegs() {
        return segs;
    }

    public void setSegs(List<Segs> segs) {
        this.segs = segs;
    }
}
