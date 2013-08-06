package com.enedilim.dict.entity;

import java.util.ArrayList;
import java.util.List;

public class Word {
	
	private int id;
	private String word;
	private int homonym;
	private String pronun;
	private List<Definition> definitions;
	private List<Phrase> phrases;
	
	public Word() {
		homonym = 0;
		definitions = new ArrayList<Definition>();
		phrases = new ArrayList<Phrase>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getHomonym() {
		return homonym;
	}

	public void setHomonym(int homonym) {
		this.homonym = homonym;
	}

	public String getPronun() {
		return pronun;
	}

	public void setPronun(String pronun) {
		if(!pronun.trim().equals(""))
		    this.pronun = pronun;
	}

	public List<Definition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<Definition> definitions) {
		this.definitions = definitions;
	}

	public List<Phrase> getPhrases() {
		return phrases;
	}

	public void setPhrases(List<Phrase> phrases) {
		this.phrases = phrases;
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
