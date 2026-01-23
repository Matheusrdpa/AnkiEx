package com.ankiEx.project.services;

import com.ibm.icu.text.Transliterator;
import org.springframework.stereotype.Service;

@Service
public class KanaConverterService {
    private final Transliterator converter = Transliterator.getInstance("Katakana-Latin");

    public String toRomaji(String katakanaText){
        if (katakanaText == null) return "null value in katakana text";

        String romaji = converter.transliterate(katakanaText);

        return romaji.replace("ū", "uu")
                .replace("ō", "ou")
                .toLowerCase();
    }
}
