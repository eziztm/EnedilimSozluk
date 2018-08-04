package com.enedilim.dict.fragments.support;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.enedilim.dict.R;
import com.enedilim.dict.entity.Word;
import com.enedilim.dict.exceptions.ConnectionException;
import com.enedilim.dict.exceptions.SaxException;
import com.enedilim.dict.utils.CacheManager;
import com.enedilim.dict.utils.WordSaxParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
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
    private static final WordSaxParser parser = new WordSaxParser();
    private static String baseUrl;
    private static String wordPath;
    private static String apiHeader;
    private Context context;
    private WordFetchListener callback;

    public WordFetchTask(Context context, WordFetchListener callback) {
        this.context = context;
        this.callback = callback;

        // load the xml url once
        if (baseUrl == null) {
            baseUrl = context.getResources().getString(R.string.baseUrl);
            wordPath = context.getResources().getString(R.string.wordPath);
            apiHeader = context.getResources().getString(R.string.apiHeader);
        }
    }

    /**
     * Retrieve the word in the background.
     *
     * Use cache if it exists, else populate cache from server.
     */
    @Override
    public WordFetchResult doInBackground(String... word) {

        try {
            List<Word> words = parseFromCache(CacheManager.getInstance().get(word[0].trim(), isOnline()));
            if (words == null) {
                words = Collections.emptyList();
            }
            return new WordFetchResult(word[0], words);
        } catch (ConnectionException e) {
            return new WordFetchResult(word[0], e);
        }
    }

    private List<Word> parseFromCache(File xmlFile) {
        try {
            InputStream is = new FileInputStream(xmlFile);
            return parser.parseXml(is);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Cache file not found. " + e.getMessage());
        } catch (SaxException se) {
            Log.e(TAG, "Error while parsing from cache. " + se.getMessage());
        }
        return Collections.emptyList();
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

