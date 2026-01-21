package com.ankiEx.project.services;

import com.ankiEx.project.entities.Subtitles.Events;
import com.ankiEx.project.entities.Subtitles.Segs;
import com.ankiEx.project.entities.Subtitles.YtSubtitles;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.File;

@Service
public class JsonConverterService {
    public void convertJson() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("./tools/subtitles.ja.json3");
        YtSubtitles s = mapper.readValue(file, YtSubtitles.class);

        if (s.getEvents() != null) {
            for (Events e : s.getEvents()) {
                if (e.getSegs() != null) {

                    long time = e.gettStartMs();
                    StringBuilder sb = new StringBuilder();

                    for (Segs seg : e.getSegs()){
                        String text = seg.getUtf8();
                        if (text.isBlank()) {
                            continue;
                        }
                        sb.append(text);
                    }

                }
            }
        }
    }
}
