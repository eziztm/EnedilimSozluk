package com.enedilim.dict.connectors;

import com.enedilim.dict.exceptions.ConnectionException;
import com.enedilim.dict.exceptions.NotFoundException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EnedilimConnector {
    private static final String TAG = EnedilimConnector.class.getSimpleName();
    private static final String BASE_URL = "enedilim.com";
    private static final String API_HEADER = "application/vnd.enedilim.v3+xml";
    private static EnedilimConnector connector;
    private final OkHttpClient client;

    private EnedilimConnector() {
        client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();
    }

    public static EnedilimConnector getInstance() {
        if (connector == null) {
            connector = new EnedilimConnector();
        }
        return connector;
    }

    public String getWord(String word) throws ConnectionException {
        return doRequest(BASE_URL + "/sozluk/soz/" + word);
    }

    public Set<String> getWordList() throws ConnectionException {
        String response = doRequest(BASE_URL + "/sozluk/meta/wordlist");
        return new HashSet<>(Arrays.asList(response.split("\n")));
    }

    public int getWordListVersion() throws ConnectionException {
        String version = doRequest(BASE_URL + "/sozluk/meta/version");
        if (version.contains(".")) {
            return Integer.parseInt(version.split("\\.")[1]);
        }
        return Integer.parseInt(version);
    }

    private String doRequest(String url) throws ConnectionException {
        try {
            try {
                // Prefer https
                Request httpsRequest = new Request.Builder().url("https://" + url).addHeader("Accept", API_HEADER).build();
                return doRequest(httpsRequest);
            } catch (IOException e) {
                // Attempt http request instead
                Request httpRequest = new Request.Builder().url("http://" + url).addHeader("Accept", API_HEADER).build();
                return doRequest(httpRequest);
            }
        } catch (IOException e) {
            throw new ConnectionException("Exception during enedilim request", e);
        }
    }

    private String doRequest(Request request) throws IOException, ConnectionException {
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            }
            throw new ConnectionException("Failed to retrieve word: " + response.body().string());
        }
    }
}
