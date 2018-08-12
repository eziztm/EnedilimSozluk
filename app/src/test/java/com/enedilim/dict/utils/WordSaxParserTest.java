package com.enedilim.dict.utils;

import com.enedilim.dict.entity.Definition;
import com.enedilim.dict.entity.Example;
import com.enedilim.dict.entity.Phrase;
import com.enedilim.dict.entity.Word;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class WordSaxParserTest {

    private WordSaxParser parser = WordSaxParser.getInstance();

    @Test
    public void shouldSuccessfullyParseFromString() throws Exception {
        String xmlString = "<wordList>\n" +
                "\t<wordItem word=\"maksat\" class=\"at\" suffixRoot=\"maksad\">\n" +
                "\t\t<attributes opensConsonant=\"true\" twoSyllables=\"true\" endsWithBackVowelSyllable=\"true\"/>\n" +
                "\t\t<defItem cat=\"Test\">\n" +
                "\t\t\t<def>Bir işi amala aşyrmaklyga bolan meýil, niýet, matlap, maksat-myrat, arzuw.</def>\n" +
                "\t\t\t<example source=\"«Sowet edebiýaty» žurnaly\">Sowet Döwlet býujeti hem gös-göni şu maksatlara gönükdürilen parahatçylyk hem döredijilik býujetidir.</example>\n" +
                "\t\t\t<example source=\"«Mydam Taýýar» gazeti\">Amyderýany Murgaba tapyşdyrmak maksady bilen joşýarlar.</example>\n" +
                "\t\t</defItem>\n" +
                "\t\t<defItem see=\"Some word\">\n" +
                "\t\t\t<example source=\"S. N. Winogradow, A. F. Kuzmin, Logika\">Adam özüniň işine konkret maksatlar goýýar.</example>\n" +
                "\t\t</defItem>\n" +
                "\t\t<phraseItem phrase=\"nazardan sypdyrmak\">\n" +
                "\t\t\t<defItem>\n" +
                "\t\t\t\t<def>Ünsden düşürmek, gözden salmak.</def>\n" +
                "\t\t\t\t<example source=\"A. Gowşudow, Köpetdagyň eteginde\">Alasarmyk garaňkynyň içinde ýigitler her bir gymyldyny nazarlaryndan saldyrmajak bolşup jan edýärdiler.</example>\n" +
                "\t\t\t</defItem>\n" +
                "\t\t</phraseItem>\n" +
                "\t\t<phraseItem phrase=\"nazary düşmek\">\n" +
                "\t\t\t<defItem>\n" +
                "\t\t\t\t<def>Birini ýa-da bir zady görmek, birine, bir zada gözi düşmek.</def>\n" +
                "\t\t\t\t<example source=\"N. Amanow, A. Kowusow, Balykçylar\">Heniz gyzyşmandyr deňiz bazary, Goňşy gämilere düşdi nazary.</example>\n" +
                "\t\t\t</defItem>\n" +
                "\t\t</phraseItem>\n" +
                "\t\t<rule>\n" +
                "\t\t\t<explanation>Soňuna çekimli ses bilen başlanýan goşulmalar ýa-da *-rak* goşulmasy goşulanda, sözüň soňundaky dymyk çekimsiz degişli açyk çekimsizine öwrülýär.</explanation>\n" +
                "\t\t\t<examples>maksat - maksady.</examples>\n" +
                "\t\t</rule>\n" +
                "\t\t<relatedWords>meýil,arzuw,niýet,maksat-myrat,matlap</relatedWords>\n" +
                "\t</wordItem>\n" +
                "</wordList>";

        List<Word> words = parser.parseXml(xmlString);
        assertFalse("Should not be empty", words.isEmpty());
        assertThat(words.size(), is(1));
        assertWordAttributes(words.get(0));
    }

    private void assertWordAttributes(Word word) {
        assertThat(word.getWord(), equalTo("maksat"));
        assertThat(word.getWordType(), equalTo("at"));
        assertThat(word.getHomonym(), is(0));

        assertThat(word.getDefinitions().size(), is(2));
        assertWordDefinition(word.getDefinitions().get(0));

        assertThat(word.getPhrases().size(), is(2));
        assertWordPhrase(word.getPhrases().get(0));
    }

    private void assertWordPhrase(Phrase phrase) {
        assertNotNull(phrase.getPhrase());
        assertThat(phrase.getDefinitions().size(), is(1));
    }

    private void assertWordDefinition(Definition definition) {
        assertThat(definition.getExamples().size(), is(2));
        assertDefinitionExample(definition.getExamples().get(0));

        assertNotNull(definition.getDefinition());
    }

    private void assertDefinitionExample(Example example) {
        assertNotNull(example.getExample());
        assertNotNull(example.getSource());
    }
}