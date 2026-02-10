package com.ankiEx.project.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiController {
    private final ChatClient chatClient;
    public AiController(ChatClient.Builder builder){
        this.chatClient = builder.build();
    }

    @GetMapping("/process")
    public String process(@RequestParam String text){
        String res =  chatClient.prompt().system("""
                    Você é um professor de idiomas especializado em japonês e português. responda APENAS com um json valido, não escreva nada fora do json e nao use blocos de markdown
                    e não adicione texto antes ou depois do json
                    Sua tarefa é:
                    1. Verificar se a frase enviada pelo usuário possui erros gramaticais. (Se a frase estiver cortada ou incompleta, ignore a parte incompleta e adicione no errors)
                    2. Se houver erros, corrija-os.
                    3. Forneça a tradução correta para o português brasileiro.
                    4. Forneça a resposta em Json com o seguinte formato:{"Errors": "...", "Sentence": "...", "Translation": "..."}
                    5. Onde errors recebe uma string de explicaçao do erro, "sentence" tem a frase original corrigida, e Translation tem a traducao da frase correta
                    """)
                .user(text)
                .call()
                .content();

        return res.replaceAll("```json","").replaceAll("```","").trim();
    }
}
