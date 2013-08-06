package com.enedilim.dict.entity;

import java.util.ArrayList;
import java.util.List;

public class Phrase {
	
    private int id;
    private String phrase;
    private List<Definition> definitions;
    
    public Phrase(){
    	definitions = new ArrayList<Definition>();
    }
    
    public Phrase(String phrase){
    	this.phrase = phrase;
    	definitions = new ArrayList<Definition>();
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
