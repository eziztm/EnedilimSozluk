package com.enedilim.dict.utils;

import com.enedilim.dict.entity.Definition;
import com.enedilim.dict.entity.Example;
import com.enedilim.dict.entity.Phrase;
import com.enedilim.dict.entity.Word;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class WordHandler extends DefaultHandler {

    private List<Word> words;
    private List<Definition> definitions;
    private List<Example> examples;
    private List<Phrase> phrases;
    private Word currentWord;
    private Definition currentDef;
    private Example currentExample;
    private Phrase currentPhrase;
    private String currentElement = null;
    private List<String> rules;

    public List<Word> getWords() {
        return words;
    }

    @Override
    public void startDocument() throws SAXException {
        words = new ArrayList<Word>();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {

        if (localName.equalsIgnoreCase("WordItem")) {
            currentWord = new Word();
        } else if (localName.equalsIgnoreCase("word")) {
            if (attributes.getValue("hom") != null) {
                int h = Integer.parseInt(attributes.getValue("hom"));
                currentWord.setHomonym(h);
            }
            currentWord.setWordType(attributes.getValue("wordType"));
        } else if (localName.equalsIgnoreCase("defs")) {
            definitions = new ArrayList<Definition>();
        } else if (localName.equalsIgnoreCase("def")) {
            currentDef = new Definition();
            if (attributes.getValue("cat") != null) {
                currentDef.setCategory(attributes.getValue("cat"));
            }
        } else if (localName.equalsIgnoreCase("seeWord")) {
            if (attributes.getValue("hom") != null) {
                currentDef.setSeeHomonym(Integer.parseInt(attributes.getValue("hom")));
            }

            if (attributes.getValue("def") != null) {
                currentDef.setSeeDefinition(attributes.getValue("def"));
            }
        } else if (localName.equalsIgnoreCase("examples")) {
            examples = new ArrayList<Example>();
        } else if (localName.equalsIgnoreCase("ex")) {
            currentExample = new Example();

            if (attributes.getValue("src") != null) {
                currentExample.setSource(attributes.getValue("src"));
            }
        } else if (localName.equalsIgnoreCase("phrases")) {
            phrases = new ArrayList<Phrase>();
        } else if (localName.equalsIgnoreCase("phraseItem")) {
            currentPhrase = new Phrase();
        } else if (localName.equalsIgnoreCase("notes")) {
            rules = new ArrayList<String>();
        }

        currentElement = localName;
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        if (localName.equalsIgnoreCase("WordItem")) {
            words.add(currentWord);
            currentWord = null;
        } else if (localName.equalsIgnoreCase("defs")) {
            if (currentPhrase == null) {
                currentWord.setDefinitions(definitions);
            } else {
                currentPhrase.setDefinitions(definitions);
            }
            definitions = null;
        } else if (localName.equalsIgnoreCase("def")) {
            definitions.add(currentDef);
            currentDef = null;
        } else if (localName.equalsIgnoreCase("examples")) {
            currentDef.setExamples(examples);
            examples = null;
        } else if (localName.equalsIgnoreCase("ex")) {
            examples.add(currentExample);
            currentExample = null;
        } else if (localName.equalsIgnoreCase("phrases")) {
            currentWord.setPhrases(phrases);
            phrases = null;
        } else if (localName.equalsIgnoreCase("phraseItem")) {
            phrases.add(currentPhrase);
            currentPhrase = null;
        } else if (localName.equalsIgnoreCase("notes")) {
            currentWord.setRules(rules);
            rules = null;
        }
    }

    @Override
    public void characters(char ch[], int start, int length)
            throws SAXException {
        String value = new String(ch, start, length);
        value = value.trim();
        if (!value.trim().equals("")) {
            if (currentElement.equalsIgnoreCase("word")) {
                currentWord.setWord(value);
            } else if (currentElement.equalsIgnoreCase("pronun")) {
                currentWord.setPronun(value);
            } else if (currentElement.equalsIgnoreCase("d")) {
                currentDef.setDefinition(value);
            } else if (currentElement.equalsIgnoreCase("ex")) {
                currentExample.setExample(value);
            } else if (currentElement.equalsIgnoreCase("seeWord")) {
                currentDef.setSee(value);
            } else if (currentElement.equalsIgnoreCase("seePhrase")) {
                currentDef.setSeePhrase(value);
            } else if (currentElement.equalsIgnoreCase("phrase")) {
                currentPhrase.setPhrase(value);
            } else if (currentElement.equalsIgnoreCase("rule")) {
                rules.add(value);
            } else if (currentElement.equalsIgnoreCase("ruleExample")) {
                currentWord.setRuleExample(value);
            }
        }
    }
}
