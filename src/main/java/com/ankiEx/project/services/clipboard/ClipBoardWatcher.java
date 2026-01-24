package com.ankiEx.project.services.clipboard;

import com.ankiEx.project.entities.morph.AnalyzedSentence;
import com.ankiEx.project.entities.morph.WordOption;
import com.ankiEx.project.services.AnkiService;
import com.ankiEx.project.services.JsonConverterService;
import com.ankiEx.project.services.MorphAnalyzerService;
import com.ankiEx.project.services.YtDlpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ClipBoardWatcher implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(ClipBoardWatcher.class);
    private YtDlpService ytDlpService;
    private JsonConverterService jsonConverterService;
    private MorphAnalyzerService morphAnalyzerService;
    private AnkiService ankiService;

    public ClipBoardWatcher(YtDlpService ytDlpService, JsonConverterService jsonConverterService, MorphAnalyzerService morphAnalyzerService, AnkiService ankiService) {
        this.ytDlpService = ytDlpService;
        this.jsonConverterService = jsonConverterService;
        this.morphAnalyzerService = morphAnalyzerService;
        this.ankiService = ankiService;
    }



    @Override
    public void run(String... args) throws Exception {
        logger.info("Started watching the clipboard...");
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String previousText = "";

        while (true) {
            try {
                if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                    String content = (String) clipboard.getData(DataFlavor.stringFlavor);

                    if (!content.equals(previousText)) {
                        previousText = content;
                        processContent(content);
                    }
                }
                Thread.sleep(1000);
            }catch (Exception e){
            }
        }
    }

    private void processContent(String url) {
        String regex = "^(?:https?://)?(?:www\\.)?(?:youtube\\.com/watch\\?v=|youtu\\.be/)([a-zA-Z0-9_-]{11}).*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {

            String videoIdRegex = "(?:v=|youtu\\.be/)([^&?]+)";
            Pattern idPattern = Pattern.compile(videoIdRegex);
            Matcher idMatcher = idPattern.matcher(url);

            Pattern timePattern = Pattern.compile("[?&]t=(\\d+)");
            Matcher timeMatcher = timePattern.matcher(url);

            int time = timeMatcher.find() ? Integer.parseInt(timeMatcher.group(1)) : 0;

            String videoId = idMatcher.find() ? idMatcher.group(1) : "";
            ytDlpService.executeCommands(url,videoId);

            String sentence = jsonConverterService.getSentenceAtTimeStamp(time,videoId);

            if (sentence != null && !sentence.isEmpty()) {
                AnalyzedSentence analyzedSentence = morphAnalyzerService.analyzeSentence(sentence);

                showSelectionWindow(sentence, analyzedSentence);
            }
        }
    }

    private void showSelectionWindow(String sentence, AnalyzedSentence analyzedSentence) {
        StringBuilder menu = new StringBuilder();
        menu.append("Frase: " + sentence).append("\n");
        menu.append("Romaji: " + analyzedSentence.fullRomaji()).append("\n");
        menu.append("-----------------------------------------------------\n");

        int id = 1;

        for (WordOption word: analyzedSentence.words()){
            menu.append(String.format("[%d] %s (%s)\n", id,word.kanji(),word.romaji()));
            id++;
        }

        menu.append("-----------------------------------------------------\n");
        menu.append("Type the word number or romaji: ");

        JDialog dialog = new JDialog();
        dialog.setAlwaysOnTop(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        String input = JOptionPane.showInputDialog(dialog,menu.toString(), "Anki Miner - New sentence", JOptionPane.QUESTION_MESSAGE);

        if (input != null && !input.isEmpty()) {
            treatUserChoice(input,analyzedSentence, sentence);
        }else {
            logger.info("No sentence selected");
        }
        dialog.dispose();
    }

    private void treatUserChoice(String input, AnalyzedSentence analyzedSentence, String sentence) {
        input = input.toLowerCase().trim();

        if (input.matches("\\d+")){
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < analyzedSentence.words().size()) {
                WordOption chosen = analyzedSentence.words().get(index);
                logger.info("Create card:  " + chosen.kanji() + " (" + chosen.romaji() + ")");
                ankiService.addNote(chosen.kanji(), sentence);
            }else {
                logger.info("No sentence selected or invalid word/id");
            }
        }else {
            logger.info("Search custom word");
        }
    }

}

