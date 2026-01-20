package com.ankiEx.project.services;

import com.ankiEx.project.Subtitles.Events;
import com.ankiEx.project.Subtitles.Segs;
import com.ankiEx.project.Subtitles.YtSubtitles;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.File;

@Service
public class JsonConverterService {
    public void convertJson(String json) {
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
        System.out.println(s.getEvents().get(1).getSegs().get(0).getUtf8());
    }
}
