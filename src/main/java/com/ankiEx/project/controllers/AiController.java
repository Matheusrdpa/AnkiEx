package com.ankiEx.project.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//Controller being used purely for data visualization

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
                    Você é um professor de idiomas especializado em japonês e ingles, então as explicações e traduçoes são sempre em ingles. responda APENAS com um json valido, não escreva nada fora do json e nao use blocos de markdown
                    e não adicione texto antes ou depois do json
                    Sua tarefa é:
                    - Consertar legendas fragmentadas ou erradas que vão vir do youtube
                    - Verificar se a frase enviada pelo usuário possui erros gramaticais. (Se a frase estiver cortada ou incompleta, ignore a parte incompleta e adicione no errors)
                    - Se houver erros, corrija-os.
                    - Forneça a tradução correta para o português brasileiro.
                    - Forneça a resposta em Json com o seguinte formato:{"sentence": "...", "translation": "...", "furigana": "...","romaji": "...", "morphemes": "...","grammar_note": "..."}
                    - Onde sentence recebe uma string com a frase original SEM FURIGANA (se for romaji, passe para kanji original) corrigida,o campo Translation tem a traducao da frase correta,o campo furigana vai ter a frase original em kanji 
                    e ao lado dos kanjis furigana, você vai adicionar o furigana entre [], exemplo:  猫[ねこ],o campo morphemes recebe uma lista de morphemas da linguagem na frase, por mais que so tenha um item,o json precisa ser formatado como uma lista e o grammar note possui a explicação do erro (caso exista)
                    e o romaji recebe a frase em romaji.     
                    - O campo morphemes tem a seguinte estrutura: {"surface","reading","meaning","pos"},onde o reading vem sempre com o hiragana.
                    """)
                .user(text)
                .call()
                .content();

        return res.replaceAll("```json","").replaceAll("```","").trim();
    }
}
