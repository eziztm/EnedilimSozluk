package com.enedilim.dict.asynctasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.enedilim.dict.connectors.EnedilimConnector;
import com.enedilim.dict.exceptions.ConnectionException;
import com.enedilim.dict.utils.DatabaseHelper;

import java.util.Set;

public class UpdateWordListTask extends AsyncTask<DatabaseHelper, Integer, Boolean> {

    private static final String TAG = UpdateWordListTask.class.getSimpleName();
    private final Context context;

    public UpdateWordListTask(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(DatabaseHelper... dbHelpers) {
        Log.i(TAG, "Attempting to update wordlist remotely ");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int wordListVersion = preferences.getInt("WORDLIST_VERSION", 0);
        try {
            int onlineWordListVersion = EnedilimConnector.getInstance().getWordListVersion();
            Log.d(TAG, "Online wordlist version " + onlineWordListVersion + ", stored version " + wordListVersion);
            if (wordListVersion < onlineWordListVersion) {
                Set<String> onlineWordList = EnedilimConnector.getInstance().getWordList();
                if (syncWordList(dbHelpers[0], onlineWordList)) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("WORDLIST_VERSION", onlineWordListVersion);
                    return editor.commit();
                }
            }
        } catch (ConnectionException e) {
            Log.e(TAG, "Failed to retrieve wordlist remotely", e);
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
