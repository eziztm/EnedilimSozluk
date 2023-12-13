package com.enedilim.dict.asynctasks;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import android.util.Log;

import com.enedilim.dict.connectors.EnedilimConnector;
import com.enedilim.dict.exceptions.ConnectionException;
import com.enedilim.dict.utils.DatabaseHelper;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class UpdateWordListTask implements Callable<Void> {
    private static final int UPDATE_CHECK_INTERVAL = 7;

    private static final String TAG = UpdateWordListTask.class.getSimpleName();
    private final Context context;
    private final DatabaseHelper dbHelper;

    public UpdateWordListTask(Context context, DatabaseHelper dbHelper) {
        this.context = context;
        this.dbHelper = dbHelper;
    }

    @Override
    public Void call() {
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
                    if (syncWordList(dbHelper, onlineWordList)) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putLong("WORDLIST_VERSION_CHECK", new Date().getTime());
                        editor.putInt("WORDLIST_VERSION", onlineWordListVersion);
                        editor.apply();
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
        return null;
    }

    private boolean syncWordList(DatabaseHelper dbHelper, Set<String> onlineWordList) {
        if (dbHelper == null || onlineWordList == null || onlineWordList.isEmpty()) {
            return false;
        }

        dbHelper.updateWordList(onlineWordList);
        return true;
    }
}
