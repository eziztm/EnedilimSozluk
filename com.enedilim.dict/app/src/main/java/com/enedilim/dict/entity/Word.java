package com.enedilim.dict.entity;

import java.util.ArrayList;
import java.util.List;

public class Word {

    private final String word;
    private final int homonym;
    private final String pronun;
    private final String wordType;

    private final List<Definition> definitions;
    private final List<Phrase> phrases;
    private final List<Rule> rules;

    public Word(String word, String homonym, String pronun, String wordType) {
        this.word = word;
        this.homonym = homonym == null ? 0 : Integer.parseInt(homonym);
        this.pronun = pronun;
        this.wordType = wordType;

        definitions = new ArrayList<>();
        phrases = new ArrayList<>();
        rules = new ArrayList<>();
    }

    public String getWord() {
        return word;
    }

    public int getHomonym() {
        return homonym;
    }

    public String getPronun() {
        return pronun;
    }

    public String getWordType() {
        return wordType;
    }

    public List<Definition> getDefinitions() {
        return definitions;
    }

    public List<Phrase> getPhrases() {
        return phrases;
    }

    public List<Rule> getRules() {
        return rules;
    }

    @Override
    public String toString() {
        if (getHomonym() > 0) {
            return word + "-" + getHomonym();
        } else {
            return word;
        }
    }

}
