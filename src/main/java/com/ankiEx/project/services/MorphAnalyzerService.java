package com.ankiEx.project.services;

import com.worksap.nlp.sudachi.Dictionary;
import com.worksap.nlp.sudachi.DictionaryFactory;
import com.worksap.nlp.sudachi.Morpheme;
import com.worksap.nlp.sudachi.Tokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MorphAnalyzerService {
    private Logger logger = LoggerFactory.getLogger(MorphAnalyzerService.class);
    private final Tokenizer tokenizer;
    private final KanaConverterService kanaConverterService;
    private  Set<String> whiteListExceptions;

    private final Path whitelistPath = Path.of("./tools/whitelist.txt");

    public MorphAnalyzerService(KanaConverterService kanaConverterService) throws IOException {
        String fileName = "system_core.dic";
        String config = String.format("{\"systemDict\": \"%s\", \"path\": \".\"}", fileName);

        Dictionary dict = new DictionaryFactory().create(null,config,true);
        this.tokenizer = dict.create();
        this.kanaConverterService = kanaConverterService;
        loadWhiteList();
    }


    public String analyzeSentence(String sentence){
        List<Morpheme> tokens = tokenizer.tokenize(Tokenizer.SplitMode.C,sentence);
        StringBuilder fullSentence = new StringBuilder();

        List<String> romajiTokens = new ArrayList<>();
        for (Morpheme morpheme : tokens) {
            String reading = kanaConverterService.toRomaji(morpheme.readingForm());
            romajiTokens.add(reading);
        }

        for (int i = 0; i < romajiTokens.size(); i++) {
            String current = romajiTokens.get(i);

            if (i + 1 < romajiTokens.size()) {

                String next = romajiTokens.get(i + 1);
                String fusionAttempt = current + next;

                if (whiteListExceptions.contains(fusionAttempt)) {
                    fullSentence.append(fusionAttempt).append(" ");
                    i++;
                    continue;
                }
            }
            fullSentence.append(current).append(" ");
        }

        logger.info("Romaji: " + fullSentence);
        return fullSentence.toString().trim();
    }

    public void loadWhiteList() {
        try(Stream<String> lines = Files.lines(whitelistPath, StandardCharsets.UTF_8)) {
            this.whiteListExceptions = lines.map(String::trim).filter(s -> !s.isEmpty() && !s.startsWith("#")).collect(Collectors.toSet());
            logger.info("full whitelist: " + whiteListExceptions);
        }catch (IOException e) {
            logger.warn("Error reading whitelist: " + e.getMessage());
            this.whiteListExceptions = new HashSet<>();
        }
    }
}
