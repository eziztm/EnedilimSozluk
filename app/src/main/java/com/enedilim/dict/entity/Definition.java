package com.enedilim.dict.entity;

import java.util.ArrayList;
import java.util.List;

public class Definition {
    private String definition;
    private final List<Example> examples;
    private final String category;
    private final String see;
    private final int seeHomonym;
    private final String seeDefinition;
    private final String seePhrase;

    public Definition(String category, String see, String seeHomonym, String seeDefinition, String seePhrase) {
        this.category = category;
        this.see = see;
        this.seeHomonym = seeHomonym == null ? 0 : Integer.parseInt(seeHomonym);
        this.seeDefinition = seeDefinition;
        this.seePhrase = seePhrase;
        examples = new ArrayList<>();
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getDefinition() {
        return definition;
    }

    public List<Example> getExamples() {
        return examples;
    }

    public String getCategory() {
        return category;
    }

    public String getSee() {
        return see;
    }

    public int getSeeHomonym() {
        return seeHomonym;
    }

    public String getSeeDefinition() {
        return seeDefinition;
    }

    public String getSeePhrase() {
        return seePhrase;
    }

    @Override
    public String toString() {
        return "Definition{" + "definition=" + definition + "examples="
                + examples + "category=" + category + "see=" + see
                + "seeHymonym=" + seeHomonym + "seeDefinition=" + seeDefinition
                + '}';
    }

}
