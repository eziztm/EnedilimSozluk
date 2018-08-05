package com.enedilim.dict.connectors;

import com.enedilim.dict.exceptions.ConnectionException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EnedilimConnector {
    private static final String TAG = EnedilimConnector.class.getSimpleName();
    private static final String BASE_URL = "enedilim.com";
    private static final String API_HEADER = "application/vnd.enedilim.v3+xml";
    private final OkHttpClient client = new OkHttpClient();

    public String getWord(String word) throws ConnectionException {
        return doRequest(BASE_URL + "/sozluk/soz/" + word);
    }

    public List<String> getWordList() throws ConnectionException {
        String response = doRequest(BASE_URL + "/sozluk/meta/wordlist");
        return Arrays.asList(response.split("\n"));
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
            Response response;
            try {
                Request request = new Request.Builder().url("https://" + url).addHeader("Accept", API_HEADER).build();
                response = client.newCall(request).execute();
            } catch (IOException e) {
                Request request = new Request.Builder().url("http://" + url).addHeader("Accept", API_HEADER).build();
                response = client.newCall(request).execute();
            }
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                throw new ConnectionException(true, true);
            }
        } catch (IOException e) {
            throw new ConnectionException(true, false);
        }
    }
}
