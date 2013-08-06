package com.enedilim.dict.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Calendar;

/**
 * Cache manager for downloaded XML files.
 * Conforms the singleton pattern.
 * @author Eziz Annagurban
 *
 */
public class CacheManager{
	
	// Max. number of files cached
	private static final int CACHE_SIZE = 50;
	private static CacheManager INSTANCE;
	
	private File cacheDir;
	
	private CacheManager(){
	}

	/**
	 * The only way to instantiate CacheManager
	 * @return
	 */
	public static CacheManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new CacheManager(); 
		}
		
		return INSTANCE;   
	}

	public File getCacheDir() {
		return cacheDir;
	}

	public void setCacheDir(File cacheDir) {
		File newCacheDir = new File(cacheDir, "/xml");
		
		if(newCacheDir.exists()){
			newCacheDir.delete();
		}
		
		newCacheDir.mkdir();
		
		this.cacheDir = newCacheDir;
	}
	
	/**
	 * Retrieves cached file or null, if file doesn't exist
	 * @param filename
	 * @return File instance or null if file doesn't exist
	 */
	public File getCacheFile(String filename) {
		File cache = new File(cacheDir, filename);
		
		if(cache.exists())
			return cache;
		else 
		    return null;
	}
	
	/**
	 * Deletes all files in the cache directory
	 */
	public void clearCacheDirectory(){
		File[] files = cacheDir.listFiles();
		
		for(int i = 0; i < files.length; i++){
			if(files[i].isFile()){			
				files[i].delete();
			}
		}
	}
	
	/**
	 * Deletes all cache files older than a month
	 */
	public void cleanCache(){
		File[] files = cacheDir.listFiles();
		
		Calendar current = Calendar.getInstance();
		current.set(Calendar.MONTH, current.get(Calendar.MONTH)-1);
		
		if(files.length >= CACHE_SIZE){
			clearCacheDirectory();
		}
		else {
			for(int i = 0; i < files.length; i++){
				Calendar lastModified = Calendar.getInstance();
				lastModified.setTimeInMillis(files[i].lastModified());
				
				if(lastModified.before(current)){
					files[i].delete();
				}
			}
		}
	}
	
	/**
	 * Saves the XML from URL to cache directory.
	 * @param url
	 * @param filename
	 * @throws IOException
	 */
	public void saveCacheFile(String url, String filename) throws IOException {
		URL sourceUrl = new URL(url);
		InputStream is = sourceUrl.openStream();
		File output = new File(cacheDir, filename);
		OutputStream os = new FileOutputStream(output);

		saveCacheFile(is, os);
	}

	/**
	 * Saves the file from InputStream to OutputStream
	 * @param is
	 * @param os
	 * @throws IOException
	 */
	public void saveCacheFile(InputStream is, OutputStream os) throws IOException {
		// Open the input and output streams
		InputStream myInput = is;
		OutputStream myOutput = os;

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}
}


