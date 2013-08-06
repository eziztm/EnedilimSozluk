package com.enedilim.dict.entity;

import java.util.ArrayList;
import java.util.List;

public class Definition {

	private String definition;
	private List<Example> examples;
	private String category;
	private String see;
	private int seeHomonym;
	private String seeDefinition;
	private String seePhrase;

	public Definition() {
		examples = new ArrayList<Example>();
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public List<Example> getExamples() {
		return examples;
	}

	public void setExamples(List<Example> examples) {
		this.examples = examples;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSee() {
		return see;
	}

	public void setSee(String see) {
		this.see = see;
	}

	public int getSeeHomonym() {
		return seeHomonym;
	}

	public void setSeeHomonym(int seeHomonym) {
		this.seeHomonym = seeHomonym;
	}

	public String getSeeDefinition() {
		return seeDefinition;
	}

	public void setSeeDefinition(String seeDefinition) {
		this.seeDefinition = seeDefinition;
	}
	
	public String getSeePhrase() {
		return seePhrase;
	}

	public void setSeePhrase(String seePhrase) {
		this.seePhrase = seePhrase;
	}

	@Override
	public String toString() {
		return "Definition{" + "definition=" + definition + "examples="
				+ examples + "category=" + category + "see=" + see
				+ "seeHymonym=" + seeHomonym + "seeDefinition=" + seeDefinition
				+ '}';
	}
	
}
