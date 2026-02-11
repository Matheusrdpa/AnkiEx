package com.ankiEx.project.services.clipboard;

import com.ankiEx.project.entities.ai.AiResponse;
import com.ankiEx.project.entities.ai.MorphemeDto;
import com.ankiEx.project.services.AiService;
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
import tools.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ClipBoardWatcher implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(ClipBoardWatcher.class);
    private YtDlpService ytDlpService;
    private JsonConverterService jsonConverterService;
    private MorphAnalyzerService morphAnalyzerService;
    private AnkiService ankiService;
    private String deckName;
    private AiService aiService;
    private ObjectMapper objectMapper;

    public ClipBoardWatcher(ObjectMapper objectMapper,YtDlpService ytDlpService, JsonConverterService jsonConverterService, MorphAnalyzerService morphAnalyzerService, AnkiService ankiService, AiService aiService) {
        this.ytDlpService = ytDlpService;
        this.jsonConverterService = jsonConverterService;
        this.morphAnalyzerService = morphAnalyzerService;
        this.ankiService = ankiService;
        this.aiService = aiService;
        this.objectMapper = objectMapper;
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
                logger.error("Error in monitoring loop");
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
                String text = aiService.process(sentence);
                AiResponse aiResponse = objectMapper.readValue(text, AiResponse.class);

                showSelectionWindow(aiResponse);
            }
        }
    }

    private void showSelectionWindow(AiResponse aiResponse) {
        StringBuilder menu = new StringBuilder();
        menu.append("Sentence: ").append(aiResponse.sentence()).append("\n");
        menu.append("Romaji: ").append(aiResponse.romaji()).append("\n");
        menu.append("-----------------------------------------------------\n");

        int id = 1;

        for (MorphemeDto word : aiResponse.morphemes()) {
            menu.append(String.format("[%d] %s (%s)\n", id, word.surface(), word.reading()));
            id++;
        }
        menu.append("-----------------------------------------------------\n");

        JTextField numbersField = new JTextField();

        JTextField deckNameBox = new JTextField(this.deckName);

        Object[] message = {
                new JTextArea(menu.toString()),
                "-----------------------------------------------------",
                "Type the word number or \na comma separated list of numbers (e.g.: 1,2,3,4)\n",
                numbersField,
                "Anki deck name:",
                deckNameBox
        };

        JDialog dialog = new JDialog();
        dialog.setAlwaysOnTop(true);

        int option = JOptionPane.showConfirmDialog(
                dialog,
                message,
                "Anki Miner",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            String deckTyped = deckNameBox.getText().trim();
            if (!deckTyped.isEmpty()) {
                this.deckName = deckTyped;
            }

            String input = numbersField.getText();
            if (input != null && !input.isEmpty()) {
                try {
                    List<Integer> inputList = Arrays.stream(input.split("[,\\s]+"))
                            .filter(x -> !x.isEmpty())
                            .map(Integer::parseInt)
                            .map(x -> x - 1)
                            .toList();

                    treatUserChoice(inputList, aiResponse);
                } catch (NumberFormatException e) {
                    logger.error("Type a number.");
                }
            } else {
                logger.info("No word selected.");
            }
        }

        dialog.dispose();
    }

    private void treatUserChoice(List<Integer> inputList, AiResponse aiResponse) {
        for (int i = 0; i < inputList.size(); i++) {
            int input = inputList.get(i);
                if (input >= 0 && input < aiResponse.morphemes().size()) {
                    MorphemeDto chosen = aiResponse.morphemes().get(input);
                    logger.info("Create card:  " + chosen.surface() + " (" + chosen.reading() + ")");
                    ankiService.addNote(chosen,aiResponse.sentence(),deckName,aiResponse.translation());
                } else {
                    logger.info("No sentence selected or invalid word/id");
                }
        }
    }
}

