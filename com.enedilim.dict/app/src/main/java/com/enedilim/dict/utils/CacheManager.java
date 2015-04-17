package com.enedilim.dict.utils;

import android.util.Log;

import com.enedilim.dict.exceptions.ConnectionException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
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
     * @param baseUrl
     * @param fullUrl
     * @param filename
     * @param isOnline
     * @return
     * @throws ConnectionException
     */
    public File get(String baseUrl, String fullUrl, String filename, boolean isOnline) throws ConnectionException {
        File cachedFile = new File(cacheDir, filename);

        if (cachedFile.exists()) {
            Log.d(TAG, "Retrieving from cache: " + filename);
            cachedFile.setLastModified(new Date().getTime());
            return cachedFile;
        }

        if (isOnline) {
            Log.d(TAG, "Retrieving from web: " + filename);
            try {
                return saveCacheFile(fullUrl, filename);
            } catch (IOException e) {
                throw new ConnectionException(true, isHostAvailable(baseUrl));
            }
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
        for (File file: files) {
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

        int numberOfFilesToDelete = files.length - CACHE_SIZE/2;
        for (int i = 0; i < numberOfFilesToDelete; i++) {
            files[i].delete();
        }
    }

    private boolean isHostAvailable(String url) {
        HttpURLConnection connection = null;
        try {
            URL baseUrl = new URL(url);
            connection = (HttpURLConnection) baseUrl.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setRequestProperty("Accept-Encoding", "");
            int code = connection.getResponseCode();
            return code == 200;
        } catch (IOException e) {
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Saves the XML from URL to cache directory.
     *
     * @param url
     * @param filename
     * @throws IOException
     */
    File saveCacheFile(String url, String filename) throws IOException {
        URL sourceUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) sourceUrl.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();

        if (conn.getResponseCode() == 200) {
            InputStream is = conn.getInputStream();
            File output = new File(cacheDir, filename);
            OutputStream os = new FileOutputStream(output);
            saveCacheFile(is, os);
            return output;
        }
        throw new IOException("Cannot retrieve " + url);
    }

    /**
     * Saves the file from InputStream to OutputStream
     *
     * @param is
     * @param os
     * @throws IOException
     */
    void saveCacheFile(InputStream is, OutputStream os) throws IOException {
        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length =is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }

        // Close the streams
        os.flush();
        os.close();
        is.close();
    }
}
