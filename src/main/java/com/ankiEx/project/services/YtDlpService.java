package com.ankiEx.project.services;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class YtDlpService {
    public String executeCommands(){
        try {
        List<String> commands = new ArrayList<>();
        String url = "https://www.youtube.com/watch?v=KTquJyBBE10"; // example url that will be removed later
        commands.add("./tools/yt-dlp.exe");
        commands.add(url);
        commands.add("--write-auto-sub");
        commands.add("--skip-download");
        commands.add("--sub-langs");
        commands.add("ja");
        commands.add("--sub-format");
        commands.add("json3");
        commands.add("-o");
        commands.add("subtitles");

        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        process.waitFor();
        return response.toString();

        }catch (Exception e){
            e.printStackTrace();
            return "error executing commands";
        }
    }
}
