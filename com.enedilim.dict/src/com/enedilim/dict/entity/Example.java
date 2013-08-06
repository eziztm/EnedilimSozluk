package com.enedilim.dict.entity;

public class Example {
	
    private String example;
    private String source;

    public Example() {
    }
    
    public Example(String ex, String src) {
    	example = ex;
    	source = src;
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

	public void setSource(String source) {
		this.source = source;
	}
    
    @Override
    public String toString() {
        return "Example{" + "example=" + example + "source=" + source + '}';
    }
}
