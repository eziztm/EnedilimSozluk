package com.enedilim.dict.connectors;

import com.enedilim.dict.exceptions.ConnectionException;

import static org.hamcrest.CoreMatchers.containsString;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;

public class EnedilimConnectorTest {
    private EnedilimConnector connector = new EnedilimConnector();

    @Test
    public void shouldGetExistingWord() throws ConnectionException {
        assertThat(connector.getWord("maksat"), containsString("wordItem"));
    }

    @Test(expected = ConnectionException.class)
    public void shouldFailOnNonExistingWord() throws ConnectionException {
        connector.getWord("abc");
    }

    @Test
    public void shouldReturnIntegerVersion() throws ConnectionException {
        assertTrue("Should be larger than zero", connector.getWordListVersion() > 0 );
    }

    @Test
    public void shouldReturnWordList() throws ConnectionException {
        assertThat(connector.getWordList(), hasItem("maksat") );
    }

}