package com.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Words {
    private List<Map<String, String>> wordsWithTranslate = new ArrayList<>();
    public List<Map<String, String>> getWordsWithTranslate() {
        return wordsWithTranslate;
    }

    public void setWordsWithTranslate(List<Map<String, String>> wordsWithTranslate) {
        this.wordsWithTranslate = wordsWithTranslate;
    }
}
