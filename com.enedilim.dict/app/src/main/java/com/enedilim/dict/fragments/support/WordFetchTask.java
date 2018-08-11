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

/**
 * Task for asynchronously fetching word definitions from cache or web.
 */
public class WordFetchTask extends AsyncTask<String, Integer, WordFetchResult> {

    // Container Activity must implement this interface
    public interface WordFetchListener {
        void doneFetching(WordFetchResult result);
    }

    private static final String TAG = WordFetchTask.class.getSimpleName();
    private Context context;
    private WordFetchListener callback;

    public WordFetchTask(Context context, WordFetchListener callback) {
        this.context = context;
        this.callback = callback;
    }

    /**
     * Retrieve the word in the background.
     * <p>
     * Use cache if it exists, else populate cache from server.
     */
    @Override
    public WordFetchResult doInBackground(String... word) {

        try {
            WordContent wordContent = DatabaseHelper.getInstance(context).getWordContent(word[0].trim());
            boolean staleContentExists = false;
            if (wordContent != null) {
                if (wordContent.isStale()) {
                    staleContentExists = true;
                } else {
                    Log.i(TAG, "Word displayed from cache");
                    return new WordFetchResult(word[0], WordSaxParser.getInstance().parseXml(wordContent.getContent()));
                }
            }

            if (!isOnline()) {
                if (staleContentExists) {
                    Log.i(TAG, "Showing stale content");
                    return new WordFetchResult(word[0], WordSaxParser.getInstance().parseXml(wordContent.getContent()));
                } else {
                    return new WordFetchResult(word[0], WordFetchResult.Error.NO_NETWORK);
                }
            }

            String content = EnedilimConnector.getInstance().getWord(word[0]);
            if (content == null || content.isEmpty()) {
                if (staleContentExists) {
                    return new WordFetchResult(word[0], WordSaxParser.getInstance().parseXml(wordContent.getContent()));
                } else {
                    return new WordFetchResult(word[0], WordFetchResult.Error.NOT_FOUND);
                }
            } else {
                Log.i(TAG, "Word retrieved online");
                List<Word> words = WordSaxParser.getInstance().parseXml(content);
                DatabaseHelper.getInstance(context).storeWordContent(word[0].trim(), content);
                return new WordFetchResult(word[0], words);
            }
        } catch (ConnectionException e) {
            return new WordFetchResult(word[0], WordFetchResult.Error.REMOTE_FAILED);
        } catch (SAXException e) {
            return new WordFetchResult(word[0], WordFetchResult.Error.NOT_FOUND);
        }
    }

    @Override
    public void onPostExecute(WordFetchResult result) {
        callback.doneFetching(result);
    }

    // Determine if there is internet access
    boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}

