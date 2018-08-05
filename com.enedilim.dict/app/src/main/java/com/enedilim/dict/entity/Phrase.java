package com.enedilim.dict.entity;

import java.util.ArrayList;
import java.util.List;

public class Phrase {

    private final String phrase;
    private final List<Definition> definitions;
    
    public Phrase(String phrase){
    	this.phrase = phrase;
    	definitions = new ArrayList<>();
    }

	public String getPhrase() {
		return phrase;
	}

	public List<Definition> getDefinitions() {
		return definitions;
	}
    @Override
    public String toString() {
        return "Phrase{" + "phrase=" + phrase + "definitions=" + definitions + '}';
    }
}
