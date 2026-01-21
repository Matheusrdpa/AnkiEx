package com.ankiEx.project.CLRTest;

import com.ankiEx.project.services.AnkiService;
import com.ankiEx.project.services.DictionaryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ClrTest implements CommandLineRunner {

    AnkiService ankiService;
    public ClrTest(AnkiService ankiService) {
        this.ankiService = ankiService;
    }

    @Override
    public void run(String... args) throws Exception {
        ankiService.addNote();
    }
}
