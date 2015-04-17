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
    private static String baseUrl = null;
    private static String xmlApiUrl = null;
    private Context context;
    private WordFetchListener callback;

    public WordFetchTask(Context context, WordFetchListener callback) {
        this.context = context;
        this.callback = callback;

        // load the xml url once
        if (WordFetchTask.baseUrl == null) {
            WordFetchTask.baseUrl = context.getResources().getString(R.string.baseUrl);
            WordFetchTask.xmlApiUrl = WordFetchTask.baseUrl + context.getResources().getString(R.string.xmlApiPath);
        }
    }

    /**
     * Parse XML of given word Attempt to find it in cache if not retrieve from
     * server
     */
    @Override
    public WordFetchResult doInBackground(String... word) {

        try {
            String searchWord = URLEncoder.encode(word[0].trim(), "UTF-8");

            String cacheFilename = "cached_" + searchWord + ".xml";
            String xmlUrl = xmlApiUrl + searchWord;

            List<Word> words = parseFromCache(CacheManager.getInstance().get(baseUrl, xmlUrl, cacheFilename, isOnline()));
            if (words == null) {
                words = Collections.emptyList();
            }
            return new WordFetchResult(word[0], words);
        } catch (ConnectionException e) {
            return new WordFetchResult(word[0], e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
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

