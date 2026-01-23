package com.ankiEx.project.services;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class YtDlpService {
    public String executeCommands(String url, String videoId){
        File videoFile = new File("./tools/" + videoId);
        boolean alreadyExists = videoFile.exists();

        if(!alreadyExists){
            try {
                List<String> commands = new ArrayList<>();
                commands.add("./tools/yt-dlp.exe");
                commands.add(url);
                commands.add("--write-auto-sub");
                commands.add("--skip-download");
                commands.add("--sub-langs");
                commands.add("ja");
                commands.add("--sub-format");
                commands.add("json3");
                commands.add("-o");
                commands.add("./tools/" + videoId);

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
        return "File found";
    }
}
