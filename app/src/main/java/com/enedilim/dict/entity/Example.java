package com.enedilim.dict.entity;

import java.util.Iterator;
import java.util.List;

public class Example {
	
    private String example;
    private final String source;

    public Example(String source) {
        this.source = source;
    }

	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
	}

	public String getSource() {
		return source;
	}
    
    @Override
    public String toString() {
        return "Example{" + "example=" + example + "source=" + source + '}';
    }

    public static String allOf(List<Example> examples) {
        StringBuilder sb = new StringBuilder();

        for(Iterator<Example> iterator = examples.iterator(); iterator.hasNext();) {
            Example example = iterator.next();

            sb.append("• ").append(example.getExample());
            if (example.getSource() != null) {
                sb.append(" <small>(").append(example.getSource()).append(")</small>");
            }

            if (iterator.hasNext()) {
                sb.append("<br><br>");
            }
        }

        return sb.toString();
    }
}
