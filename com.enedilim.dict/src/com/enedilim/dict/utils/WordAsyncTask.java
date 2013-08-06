package com.enedilim.dict.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.enedilim.dict.R;
import com.enedilim.dict.entity.Word;
import com.enedilim.dict.exceptions.SaxException;

public class WordAsyncTask extends  AsyncTask<String, Integer, List<Word>>{
	
	private static final String TAG = "com.enedilim.dict.WordAsyncTask";  
	
	private Context context;
	private ProgressDialog dialog;
	private CacheManager cacheManager;
	
	private static String url = null;
	
    public  WordAsyncTask(Context context){
    	this.context =  context;
    	
    	// load the xml url once
    	if(WordAsyncTask.url == null){
    		WordAsyncTask.url = context.getResources().getString(R.string.xmlUrl);
    	}
    }
	
    /**
     * Parse XML of given word
     * Attempt to find it in cache if not retrieve from server
     */
    @Override
	public List<Word> doInBackground(String... word){	
		try {
			String w = URLEncoder.encode(word[0].trim(), "UTF-8");
			String cacheFilename = "cached_"+w+".xml";
			String xmlUrl = url + w;
			
			List<Word> words;
			cacheManager = CacheManager.getInstance();
			
			if(cacheManager.getCacheFile(cacheFilename) != null){
				words = parseFromCache(cacheManager.getCacheFile(cacheFilename));
				
				if(!words.isEmpty())
				    return words;
			}
			
			// Read xml from server
			words = parseFromUrl(xmlUrl);
			
			// Save cache
			cacheManager.saveCacheFile(xmlUrl, cacheFilename);
			
			return words;
			
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "Unsupported Encoding, " + e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, "Failed to write cache file. " + e.getMessage());
		}
		
		return Collections.<Word>emptyList();
	}
	

	private List<Word> parseFromCache(File xmlFile) {
		try{
			//Log.i(TAG, "Reading XML from cache: " + xmlFile.getName());
			
			InputStream is = new FileInputStream(xmlFile);
			WordSaxParser parser = new WordSaxParser();		
			return parser.parseFromFile(is);
			
		} catch (FileNotFoundException e){
			Log.e(TAG, "Cache file not found. " + e.getMessage());
		} catch (SaxException se){
			Log.e(TAG, "Error while parsing from cache. " + se.getMessage());
		}
		
		return Collections.<Word>emptyList();
	}
	
	private List<Word> parseFromUrl(String url) {		
		try {
			//Log.i(TAG, "Reading XML from url");
			
			WordSaxParser parser = new WordSaxParser();
			
			//If no connectivity return empty
			if(isOnline()){
				return parser.parseFromUrl(url);
			}
		} catch (SaxException se) {
			Log.e(TAG, "Error while parsing from server. " + se.getMessage());
		}
		
		return Collections.<Word>emptyList();
	}
	
	
	@Override
	public void onPostExecute(List<Word> result) {
		dialog.dismiss();
	}

	@Override
	public void onPreExecute() {
		dialog = ProgressDialog.show(context, "",  context.getString(R.string.msgLoadingWord), true);
	}
	
	// Determine if there is an internet access
	public boolean isOnline() {
	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
}