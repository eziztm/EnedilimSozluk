package com.enedilim.dict.utils;

import com.enedilim.dict.entity.Word;

import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class WordSaxParserTest {

    private WordSaxParser parser = new WordSaxParser();

    @Test
    public void shouldSuccessfullyParseFromString() throws Exception {
        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<wordList>\n" +
                "<wordItem><word wordType=\"at\">maksat</word>" +
                "<defs><def><d>Bir işi amala aşyrmaklyga bolan meýil, niýet, matlap, maksat-myrat, arzuw.</d>" +
                "<examples><ex src=\"«Sowet edebiýaty» žurnaly\">Sowet Döwlet býujeti hem gös-göni şu maksatlara gönükdürilen parahatçylyk hem döredijilik býujetidir.</ex>" +
                "<ex src=\"«Mydam Taýýar» gazeti\">Amyderýany Murgaba tapyşdyrmak maksady bilen joşýarlar.</ex>" +
                "<ex src=\"S. N. Winogradow, A. F. Kuzmin, Logika\">Adam özüniň işine konkret maksatlar goýýar.</ex></examples></def></defs>" +
                "<notes><rule>Soňuna çekimli ses bilen başlanýan goşulmalar ýa-da *-rak* goşulmasy goşulanda, sözüň soňundaky dymyk çekimsiz degişli açyk çekimsizine öwrülýär.</rule>" +
                "<ruleExample>maksat - maksady.</ruleExample></notes></wordItem></wordList>\n";

        List<Word> words = parser.parseXml(xmlString);
        assertFalse("Should not be empty", words.isEmpty());
        assertThat(words.get(0).getWord(), equalTo("maksat"));
    }
}