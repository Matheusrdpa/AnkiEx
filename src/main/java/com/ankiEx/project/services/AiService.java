package com.ankiEx.project.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Service
public class AiService {
    private final ChatClient chatClient;
    @Value("classpath:/system-prompt.txt")
    private Resource systemPromptResource;

    public AiService(ChatClient.Builder builder){
        this.chatClient = builder.build();
    }

    public String process(String text) throws IOException {
        String message = systemPromptResource.getContentAsString(StandardCharsets.UTF_8);
        String res =  chatClient.prompt().system(message)
                .user(text)
                .call()
                .content();

        return res.replaceAll("```json","").replaceAll("```","").trim();
    }
}
