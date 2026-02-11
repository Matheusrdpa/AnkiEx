package com.ankiEx.project.entities.ai;

import java.util.List;

public record AiResponse(   String sentence,
         String translation,
         String furigana,
         String romaji,
         List<MorphemeDto> morphemes, String grammarNote) {
}
