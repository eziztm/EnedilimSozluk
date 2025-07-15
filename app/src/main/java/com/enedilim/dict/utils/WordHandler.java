package com.enedilim.dict.utils;

import com.enedilim.dict.entity.Definition;
import com.enedilim.dict.entity.Example;
import com.enedilim.dict.entity.Phrase;
import com.enedilim.dict.entity.Rule;
import com.enedilim.dict.entity.Word;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class WordHandler extends DefaultHandler {

    private List<Word> words;
    private Word currentWord;
    private Definition currentDef;
    private Example currentExample;
    private Phrase currentPhrase;
    private String currentElement = null;
    private Rule currentRule;

    public List<Word> getWords() {
        return words;
    }

    @Override
    public void startDocument() {
        words = new ArrayList<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attr) {

        if (localName.equalsIgnoreCase("wordItem")) {
            currentWord = new Word(attr.getValue("word"),
                    attr.getValue("hom"),
                    attr.getValue("pronun"),
                    attr.getValue("class"));
        } else if (localName.equalsIgnoreCase("defItem")) {
            currentDef = new Definition(attr.getValue("cat"),
                    attr.getValue("see"),
                    attr.getValue("seeHom"),
                    attr.getValue("seeDef"),
                    attr.getValue("seePhrase"));
        } else if (localName.equalsIgnoreCase("example")) {
            currentExample = new Example(attr.getValue("source"));
        } else if (localName.equalsIgnoreCase("phraseItem")) {
            currentPhrase = new Phrase(attr.getValue("phrase"));
        } else if (localName.equalsIgnoreCase("rule")) {
            currentRule = new Rule();
        }

        currentElement = localName;
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (localName.equalsIgnoreCase("wordItem")) {
            words.add(currentWord);
            currentWord = null;
        } else if (localName.equalsIgnoreCase("defItem")) {
            if (currentPhrase == null) {
                currentWord.getDefinitions().add(currentDef);
            } else {
                currentPhrase.getDefinitions().add(currentDef);
            }
            currentDef = null;
        } else if (localName.equalsIgnoreCase("example")) {
            currentDef.getExamples().add(currentExample);
            currentExample = null;
        } else if (localName.equalsIgnoreCase("phraseItem")) {
            currentWord.getPhrases().add(currentPhrase);
            currentPhrase = null;
        } else if (localName.equalsIgnoreCase("rule")) {
            currentWord.getRules().add(currentRule);
            currentRule = null;
        }
    }

    @Override
    public void characters(char ch[], int start, int length) {
        String value = new String(ch, start, length);
        value = value.trim();
        if (value.trim().isEmpty()) {
            return;
        }

        if (currentElement.equalsIgnoreCase("def")) {
            currentDef.setDefinition(value);
        } else if (currentElement.equalsIgnoreCase("example")) {
            currentExample.setExample(value);
        } else if (currentElement.equalsIgnoreCase("explanation")) {
            currentRule.setExplanation(value);
        } else if (currentElement.equalsIgnoreCase("examples")) {
            currentRule.setExamples(value);
        }
    }
}
