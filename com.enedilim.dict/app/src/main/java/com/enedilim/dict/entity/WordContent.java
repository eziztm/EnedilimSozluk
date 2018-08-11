package com.enedilim.dict.entity;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class WordContent {
    public static final int STALE_AFTER_DAYS = 30;
    private final String word;
    private final String content;
    private final Date updated;

    public WordContent(String word, String content, long updated) {
        this.word = word;
        this.content = content;
        this.updated = new Date(updated);
    }

    public String getWord() {
        return word;
    }

    public String getContent() {
        return content;
    }

    public boolean isStale() {
        return TimeUnit.MILLISECONDS.toDays(Math.abs(new Date().getTime() - updated.getTime())) > STALE_AFTER_DAYS;
    }
}
