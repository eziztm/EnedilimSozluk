package com.enedilim.dict.connectors;

import com.enedilim.dict.exceptions.ConnectionException;
import com.enedilim.dict.exceptions.SaxException;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class EnedilimConnectorTest {
    private EnedilimConnector connector = EnedilimConnector.getInstance();

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