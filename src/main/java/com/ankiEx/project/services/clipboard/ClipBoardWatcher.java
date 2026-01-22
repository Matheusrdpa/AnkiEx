package com.ankiEx.project.services.clipboard;

import com.ankiEx.project.services.JsonConverterService;
import com.ankiEx.project.services.YtDlpService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.*;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ClipBoardWatcher {

    private YtDlpService ytDlpService;
    private JsonConverterService jsonConverterService;

    public ClipBoardWatcher(YtDlpService ytDlpService, JsonConverterService jsonConverterService) {
        this.ytDlpService = ytDlpService;
        this.jsonConverterService = jsonConverterService;
    }

    private String previousText = "";

    @EventListener(ApplicationReadyEvent.class)
    public void startWatching(){
        Thread newThread = new Thread(() -> {
            while (true) {
                try {
                    verifyClipBoard();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        newThread.setDaemon(true);
        newThread.start();
    }


    public void verifyClipBoard(){
        try {
            Transferable content = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

            if (content != null && content.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                String currentText = (String) content.getTransferData(DataFlavor.stringFlavor);
                boolean isNew = !currentText.equals(previousText);
                boolean isYoutube = currentText.contains("youtube.com/watch") || currentText.contains("youtu.be");
                boolean hasTime = currentText.contains("?t=") || currentText.contains("&t=");
                if (isNew && isYoutube && hasTime) {
                    previousText = currentText;
                    executeFlow(currentText);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void executeFlow(String url){

        String regexVideoId = "(?:v=|youtu\\.be/)([^&?]+)";
        Pattern pattern1 = Pattern.compile(regexVideoId);
        Matcher matcher1 = pattern1.matcher(url);
        String videoId = "";

        if (matcher1.find()) {
            videoId = matcher1.group(1);
        }

        ytDlpService.executeCommands(url,videoId);


        Pattern pattern = Pattern.compile("[?&]t=(\\d+)");
        Matcher matcher = pattern.matcher(url);

        int time = 0;

        if (matcher.find()) {
            time = Integer.parseInt(matcher.group(1));
        }

        jsonConverterService.getSentenceAtTimeStamp(time,videoId);
    }
}

