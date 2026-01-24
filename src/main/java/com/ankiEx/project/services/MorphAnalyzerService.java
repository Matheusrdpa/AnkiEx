package com.ankiEx.project.services;

import com.ankiEx.project.entities.morph.AnalyzedSentence;
import com.ankiEx.project.entities.morph.WordOption;
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


    public AnalyzedSentence analyzeSentence(String sentence){
        List<Morpheme> tokens = tokenizer.tokenize(Tokenizer.SplitMode.C,sentence);
        List<WordOption> wordOptions = new ArrayList<>();
        StringBuilder fullSentence = new StringBuilder();
        StringBuilder fullSentenceRaw = new StringBuilder();


        for (int i = 0; i < tokens.size(); i++) {
            Morpheme currentToken = tokens.get(i);
            String currentRomaji = kanaConverterService.toRomaji(currentToken.readingForm());
            String currentKanji = currentToken.surface();
            String currentType = currentToken.partOfSpeech().get(0);
            fullSentenceRaw.append(currentKanji);

            if (i + 1 < tokens.size()) {
                Morpheme nextToken = tokens.get(i + 1);
                String nextRomaji = kanaConverterService.toRomaji(nextToken.readingForm());
                String fusionAttempt = currentRomaji + nextRomaji;

                if (whiteListExceptions.contains(fusionAttempt)) {
                    String fusedKanji = currentKanji + nextToken.surface();
                    wordOptions.add(new WordOption(fusedKanji,fusionAttempt, "Expressão"));
                    fullSentence.append(fusionAttempt).append(" ");
                    i++;
                    continue;
                }
            }
            wordOptions.add(new WordOption(currentKanji,currentRomaji, currentType));
            fullSentence.append(currentRomaji).append(" ");
        }

        logger.info("Romaji: " + fullSentence + "Kanji: " + fullSentenceRaw);
        return new AnalyzedSentence(fullSentence.toString().trim(),wordOptions);
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
