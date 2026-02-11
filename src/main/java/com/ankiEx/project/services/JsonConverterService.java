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
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("./tools/" + videoId + ".ja.json3");

        if (!file.exists()) {
            log.warn("Subtitles file not found");
            return null;
        }

        try {
            YtSubtitles s = mapper.readValue(file, YtSubtitles.class);
            if (s.getEvents() == null){
                log.warn("Subtitles not found");
                return null;
            }

            Events bestMatch = null;
            long smallestDifference = Long.MAX_VALUE;

            for (Events e : s.getEvents()) {
                if (e.getSegs() != null) {
                    StringBuilder tempContent = new StringBuilder();
                    for (Segs se : e.getSegs()) {
                        if (se.getUtf8() != null && !se.getUtf8().equals("\n")) {
                            tempContent.append(se.getUtf8());
                        }
                    }
                    if (tempContent.toString().trim().isEmpty()) {
                        continue;
                    }

                    long diff = Math.abs(e.gettStartMs() - targetMs);
                    if (diff < smallestDifference) {
                        smallestDifference = diff;
                        bestMatch = e;
                    }
                }
            }

            if (bestMatch != null && smallestDifference < 5000L) {
                StringBuilder sb = new StringBuilder();
                for (Segs segs : bestMatch.getSegs()) {
                    String text = segs.getUtf8();
                    if (text != null && !text.isBlank() && !text.equals("\n")) {
                        sb.append(text);
                    }
                }
                String finalSentence = sb.toString().trim();
                log.info("Best match found: {} (Diff: {}ms)", finalSentence, smallestDifference);
                return finalSentence;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
