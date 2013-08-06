package com.enedilim.dict.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.enedilim.dict.entity.Word;
import com.enedilim.dict.exceptions.SaxException;

public class WordSaxParser {
	

	public WordSaxParser(){
	}
	
	/**
	 * Parses xml from UML
	 * @param url
	 * @return 
	 * @throws SaxException
	 */
	public List<Word> parseFromUrl(String url) throws SaxException{
		try {
			URL sourceUrl = new URL(url);
			return parseXml(sourceUrl.openStream());
		} catch (MalformedURLException e) {
			throw new SaxException("URL error, " + e.getMessage(), e);
		} catch (IOException ioe) {
			throw new SaxException("IO error, " + ioe.getMessage(), ioe);
		}
	}
	
	/**
	 * Wrapped public version of the parseXml() method
	 * @param is
	 * @return
	 * @throws SaxException
	 */
	public List<Word> parseFromFile(InputStream is) throws SaxException {
		return parseXml(is);
	}
	
	/**
	 * Parse Word Xml file
	 * @param is InputStream - URL or file path
	 * @return null or List<Word>
	 */
	private List<Word> parseXml(InputStream is) throws SaxException{
		// Initiate SAX parser
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();

			WordHandler handler = new WordHandler();
			xr.setContentHandler(handler);
			xr.parse(new InputSource(is));

			return handler.getWords();

		} catch (ParserConfigurationException pce) {
			throw new SaxException("Parse error, " + pce.getMessage(), pce);
		} catch (SAXException se) {
			throw new SaxException("SAX error, " + se.getMessage(), se);
		} catch (IOException ioe) {
			throw new SaxException("IO error, " + ioe.getMessage(), ioe);
		} 	
	}
}
