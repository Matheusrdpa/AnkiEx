package com.ankiEx.project.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;


@Service
public class AiService {
    private final ChatClient chatClient;
    public AiService(ChatClient.Builder builder){
        this.chatClient = builder.build();
    }

    public String process(String text){
        String res =  chatClient.prompt().system("""
                    Você é um professor de idiomas especializado em japonês e português. responda APENAS com um json valido, não escreva nada fora do json e nao use blocos de markdown
                    e não adicione texto antes ou depois do json
                    Sua tarefa é:
                    - Consertar legendas fragmentadas ou erradas que vão vir do youtube
                    - Verificar se a frase enviada pelo usuário possui erros gramaticais. (Se a frase estiver cortada ou incompleta, ignore a parte incompleta e adicione no errors)
                    - Se houver erros, corrija-os.
                    - Forneça a tradução correta para o português brasileiro.
                    - Forneça a resposta em Json com o seguinte formato:{"sentence": "...", "translation": "...", "furigana": "...","romaji": "...", "morphemes": "...","grammar_note": "..."}
                    - Onde sentence recebe uma string com a frase original (se for romaji, passe para kanji/kana) corrigida, Translation tem a traducao da frase correta, furigana vai ter a frase original (em kanji) mas ao lado dos kanjis,
                    onde for preciso ter furigana, você vai adicionar o furigana entre [] morphemes recebe uma lista de morphemas da linguagem na frase, por mais que so tenha um item,o json precisa ser formatado como uma lista e o grammar note possui a explicaçao do erro (caso exista)
                    e o romaji recebe a frase em romaji.
                    - O campo morphemes tem a seguinte estrutura: {"surface","reading","meaning","pos"}
                    """)
                .user(text)
                .call()
                .content();

        return res.replaceAll("```json","").replaceAll("```","").trim();
    }
}
