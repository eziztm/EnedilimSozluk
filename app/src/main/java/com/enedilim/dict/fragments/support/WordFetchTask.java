package com.enedilim.dict.fragments.support;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.enedilim.dict.connectors.EnedilimConnector;
import com.enedilim.dict.entity.Word;
import com.enedilim.dict.entity.WordContent;
import com.enedilim.dict.exceptions.ConnectionException;
import com.enedilim.dict.utils.DatabaseHelper;
import com.enedilim.dict.utils.WordSaxParser;

import org.xml.sax.SAXException;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Task for asynchronously fetching word definitions from cache or web.
 */
public class WordFetchTask implements Callable<WordFetchResult> {


    private static final String TAG = WordFetchTask.class.getSimpleName();
    private Context context;
    private String word;

    public WordFetchTask(Context context, String word) {
        this.context = context;
        this.word = word;
    }

    /**
     * Retrieve the word in the background.
     * <p>
     * Use cache if it exists, else populate cache from server.
     */
    @Override
    public WordFetchResult call() {

        try {
            WordContent wordContent = DatabaseHelper.getInstance(context).getWordContent(word.trim());
            boolean staleContentExists = false;
            if (wordContent != null) {
                if (wordContent.isStale()) {
                    staleContentExists = true;
                } else {
                    Log.i(TAG, "Word displayed from cache");
                    return new WordFetchResult(word, WordSaxParser.getInstance().parseXml(wordContent.getContent()));
                }
            }

            if (!isOnline()) {
                if (staleContentExists) {
                    Log.i(TAG, "Showing stale content");
                    return new WordFetchResult(word, WordSaxParser.getInstance().parseXml(wordContent.getContent()));
                } else {
                    return new WordFetchResult(word, WordFetchResult.Error.NO_NETWORK);
                }
            }

            String content = EnedilimConnector.getInstance().getWord(word);
            if (content == null || content.isEmpty()) {
                if (staleContentExists) {
                    return new WordFetchResult(word, WordSaxParser.getInstance().parseXml(wordContent.getContent()));
                } else {
                    return new WordFetchResult(word, WordFetchResult.Error.NOT_FOUND);
                }
            } else {
                Log.i(TAG, "Word retrieved online");
                List<Word> words = WordSaxParser.getInstance().parseXml(content);
                DatabaseHelper.getInstance(context).storeWordContent(word.trim(), content);
                return new WordFetchResult(word, words);
            }
        } catch (ConnectionException e) {
            return new WordFetchResult(word, WordFetchResult.Error.REMOTE_FAILED);
        } catch (SAXException e) {
            return new WordFetchResult(word, WordFetchResult.Error.NOT_FOUND);
        }
    }

    // Determine if there is internet access
    boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}

