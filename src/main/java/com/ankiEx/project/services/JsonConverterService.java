package com.ankiEx.project.services;

import com.ankiEx.project.entities.Subtitles.Events;
import com.ankiEx.project.entities.Subtitles.Segs;
import com.ankiEx.project.entities.Subtitles.YtSubtitles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.File;

@Service
public class JsonConverterService {

    private final Logger log = LoggerFactory.getLogger(JsonConverterService.class);
    public String getSentenceAtTimeStamp(int targetSeconds, String videoId) {

        long targetMs = targetSeconds * 1000L;
        long margin = 1000L;

        ObjectMapper mapper = new ObjectMapper();
        File file = new File("./tools/"+ videoId +".ja.json3");

        if (!file.exists()) {
            log.warn("Subtitles file not found");
            return null;
        }

        try{
            YtSubtitles s = mapper.readValue(file,YtSubtitles.class);
            if (s.getEvents() != null) {
                for (Events e : s.getEvents()) {
                    if (e.getSegs() != null) {
                        long start = e.gettStartMs() - margin;
                        Long durationObj = e.getDurationMs();
                        long duration = (durationObj != null) ? durationObj : 3000L;
                        long end = start + duration;

                        if (targetMs >= start && targetMs <= end) {
                            StringBuilder sb = new StringBuilder();

                            if (e.getSegs() != null) {
                                for (Segs segs : e.getSegs()) {
                                    String text = segs.getUtf8();
                                    if (text != null && !text.isBlank() && !text.equals("\n")) {
                                        sb.append(text);
                                    }
                                }
                            }
                            String finalSentence = sb.toString().trim();
                            log.info("Sentence found: {}start: {} duration: {} end: {} targetSeconds: {}", finalSentence, start, duration, end, targetMs);

                            return finalSentence;
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
