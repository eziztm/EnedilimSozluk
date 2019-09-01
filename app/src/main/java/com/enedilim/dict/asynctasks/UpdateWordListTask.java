package com.enedilim.dict.asynctasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.enedilim.dict.connectors.EnedilimConnector;
import com.enedilim.dict.exceptions.ConnectionException;
import com.enedilim.dict.utils.DatabaseHelper;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class UpdateWordListTask extends AsyncTask<DatabaseHelper, Integer, Boolean> {
    private static final int UPDATE_CHECK_INTERVAL = 7;

    private static final String TAG = UpdateWordListTask.class.getSimpleName();
    private final Context context;

    public UpdateWordListTask(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(DatabaseHelper... dbHelpers) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int wordListVersion = preferences.getInt("WORDLIST_VERSION", 0);
        long lastChecked = preferences.getLong("WORDLIST_VERSION_CHECK", 0);
        boolean shouldCheck = lastChecked == 0 || TimeUnit.MILLISECONDS.toDays(Math.abs(new Date().getTime() - lastChecked)) > UPDATE_CHECK_INTERVAL;
        if (shouldCheck) {
            Log.i(TAG, "Attempting to update wordlist remotely ");
            try {
                int onlineWordListVersion = EnedilimConnector.getInstance().getWordListVersion();
                Log.d(TAG, "Online wordlist version " + onlineWordListVersion + ", stored version " + wordListVersion);
                if (wordListVersion < onlineWordListVersion) {
                    Set<String> onlineWordList = EnedilimConnector.getInstance().getWordList();
                    if (syncWordList(dbHelpers[0], onlineWordList)) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putLong("WORDLIST_VERSION_CHECK", new Date().getTime());
                        editor.putInt("WORDLIST_VERSION", onlineWordListVersion);
                        editor.apply();
                        return true;
                    }
                } else {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putLong("WORDLIST_VERSION_CHECK", new Date().getTime());
                    editor.apply();
                }
            } catch (ConnectionException e) {
                Log.e(TAG, "Failed to retrieve wordlist remotely", e);
            }
        }

        return false;
    }

    private boolean syncWordList(DatabaseHelper dbHelper, Set<String> onlineWordList) {
        if (dbHelper == null || onlineWordList == null || onlineWordList.isEmpty()) {
            return false;
        }

        dbHelper.updateWordList(onlineWordList);
        return true;
    }
}
