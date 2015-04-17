package com.enedilim.dict.utils;

import com.enedilim.dict.entity.Word;
import com.enedilim.dict.exceptions.SaxException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class WordSaxParser {
    private SAXParserFactory spf;

    public WordSaxParser() {
        spf = SAXParserFactory.newInstance();
    }

    /**
     * Parse Word Xml file
     *
     * @param is InputStream - URL or file path
     * @return null or List<Word>
     */
    public List<Word> parseXml(InputStream is) throws SaxException {
        // Initiate SAX parser
        try {

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
