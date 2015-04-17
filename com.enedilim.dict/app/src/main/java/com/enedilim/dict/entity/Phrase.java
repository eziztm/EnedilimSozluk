package com.enedilim.dict.entity;

import java.util.ArrayList;
import java.util.List;

public class Phrase {

    private String phrase;
    private List<Definition> definitions;
    
    public Phrase(){
    	definitions = new ArrayList<Definition>();
    }

	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}

	public List<Definition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<Definition> definitions) {
		this.definitions = definitions;
	}
	
    @Override
    public String toString() {
        return "Phrase{" + "phrase=" + phrase + "definitions=" + definitions + '}';
    }
}
