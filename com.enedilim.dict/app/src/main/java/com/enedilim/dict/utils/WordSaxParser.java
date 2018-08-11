package com.enedilim.dict.utils;

import com.enedilim.dict.entity.Word;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class WordSaxParser {
    private SAXParserFactory spf;
    private static WordSaxParser instance;

    private WordSaxParser() {
        spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
    }

    public static WordSaxParser getInstance() {
        if (instance == null) {
            instance = new WordSaxParser();
        }
        return instance;
    }

    public List<Word> parseXml(String s) throws SAXException {
        try {
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();

            WordHandler handler = new WordHandler();
            xr.setContentHandler(handler);
            xr.parse(new InputSource(new StringReader(s)));

            return handler.getWords();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException("Exception while parsing string", e);
        }
    }
}
