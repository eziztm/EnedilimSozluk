package com.enedilim.dict.utils;

import android.util.Log;

import com.enedilim.dict.connectors.EnedilimConnector;
import com.enedilim.dict.entity.Word;
import com.enedilim.dict.exceptions.ConnectionException;
import com.enedilim.dict.exceptions.SaxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Cache manager for downloaded XML files.
 * Singleton.
 *
 * @author Eziz Annagurban
 */
public class CacheManager {
    public static final String FILE_PREFIX = "cached_";
    public static final int CACHE_SIZE = 50;
    private static final String TAG = CacheManager.class.getSimpleName();
    private final WordSaxParser parser = new WordSaxParser();
    private static CacheManager INSTANCE;
    private File cacheDir;

    private CacheManager() {

    }

    public static CacheManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CacheManager();
        }
        return INSTANCE;
    }

    public void setCacheDir(File cacheDir) {
        Log.d(TAG, "Cache dir is:" + cacheDir.getName());
        File newCacheDir = new File(cacheDir, "/xml");
        newCacheDir.mkdir();
        this.cacheDir = newCacheDir;
    }

    /**
     * Gets filename from cache if it exists, otherwise retrieves online and puts it in cache.
     *
     * @param word
     * @param isOnline
     * @return
     * @throws ConnectionException
     */
    public List<Word> get(String word, boolean isOnline) throws ConnectionException {
        try {
            String searchWord = URLEncoder.encode(word, "UTF-8");
            String filename = "cached_" + searchWord + ".xml";
            File cachedFile = new File(cacheDir, filename);

            if (cachedFile.exists()) {
                Log.d(TAG, "Retrieving from cache: " + filename);
                cachedFile.setLastModified(new Date().getTime());
                return parseFromCache(cachedFile);
            }

            if (isOnline) {
                Log.d(TAG, "Retrieving from web: " + filename);
                String response = EnedilimConnector.getInstance().getWord(word);
                saveCacheFile(filename, response);
                return parseFromString(response);
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        throw new ConnectionException(false, false);
    }

    public List<String> getCachedWords() {
        List<String> words = new ArrayList<>();

        File[] files = cacheDir.listFiles(
                new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        return filename.startsWith(FILE_PREFIX);
                    }
                });

        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                return (int) (rhs.lastModified() - lhs.lastModified());
            }
        });

        try {
            for (File file : files) {
                String name = file.getName();
                name = name.substring(FILE_PREFIX.length(), name.length() - 4);
                words.add(URLDecoder.decode(name, "UTF-8"));
            }
        } catch (UnsupportedEncodingException ex) {
            Log.e(TAG, "Shouldn't happen.", ex);
        }

        return words;
    }

    /**
     * Deletes all files in the cache directory
     */
    public void clearCacheDirectory() {
        File[] files = cacheDir.listFiles();
        for (File file : files) {
            if (file.isFile() && file.getName().startsWith(FILE_PREFIX)) {
                file.delete();
            }
        }
    }

    /**
     * Deletes all cache files older than a month
     */
    public void cleanCache() {
        File[] files = cacheDir.listFiles();

        if (files.length < CACHE_SIZE) {
            return;
        }

        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                return (int) (lhs.lastModified() - rhs.lastModified());
            }
        });

        int numberOfFilesToDelete = files.length - CACHE_SIZE / 2;
        for (int i = 0; i < numberOfFilesToDelete; i++) {
            files[i].delete();
        }
    }

    private void saveCacheFile(String filename, String content) {
        File output = new File(cacheDir, filename);
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(output);
            os.write(content.getBytes());
        } catch (IOException e) {
            Log.e(TAG, "Failed to write to cache", e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                    Log.e(TAG, "Failed to close stream", e);
                }
            }
        }
    }

    private List<Word> parseFromCache(File xmlFile) {
        try {
            return parser.parseXml(new FileInputStream(xmlFile));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Cache file not found. " + e.getMessage());
        } catch (SaxException se) {
            Log.e(TAG, "Error while parsing from cache. " + se.getMessage());
        }
        return Collections.emptyList();
    }

    private List<Word> parseFromString(String s) {
        try {
            return parser.parseXml(s);
        } catch (SaxException se) {
            Log.e(TAG, "Error while parsing from cache. " + se.getMessage());
        }
        return Collections.emptyList();
    }
}
