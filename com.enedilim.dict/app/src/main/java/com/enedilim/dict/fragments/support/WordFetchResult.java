package com.enedilim.dict.fragments.support;

import com.enedilim.dict.entity.Word;
import com.enedilim.dict.exceptions.ConnectionException;

import java.util.List;

/**
 * Container for async task result and errors.
 */
public class WordFetchResult {
    private final String word;
    private List<Word> words;
    private ConnectionException error;

    public WordFetchResult(String word, List<Word> words) {
        this.words = words;
        this.word = word;
    }

    public WordFetchResult(String word, ConnectionException error) {
        this.error = error;
        this.word = word;
    }

    public boolean isError() {
        return error != null;
    }

    public List<Word> getWords() {
        return words;
    }

    public ConnectionException getError() {
        return error;
    }

    public String getWord() {
        return word;
    }
}
