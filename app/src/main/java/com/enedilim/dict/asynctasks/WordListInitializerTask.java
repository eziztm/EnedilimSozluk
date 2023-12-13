package com.enedilim.dict.asynctasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import androidx.preference.PreferenceManager;

import com.enedilim.dict.utils.DatabaseHelper;

import java.util.concurrent.Callable;

/**
 * Initializes the word list dictionary.
 *
 * Should be called the first time the application is run.
 *
 * @author Nazar Annagurban
 */
public class WordListInitializerTask implements Callable<Boolean> {

    private static final String TAG = WordListInitializerTask.class.getSimpleName();

    private Context context;
    private final DatabaseHelper dbHelper;

    public WordListInitializerTask(Context context, DatabaseHelper dbHelper) {
        this.context = context;
        this.dbHelper = dbHelper;
    }

    @Override
    public Boolean call() {
        boolean isSuccessful = false;
        if (dbHelper != null) {

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            isSuccessful = db.isOpen() && !db.isReadOnly();

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("DB_VERSION", DatabaseHelper.DATABASE_VERSION);
            editor.putInt("WORDLIST_VERSION", DatabaseHelper.INCLUDED_WORDLIST_VERSION);
            editor.apply();
            db.close();
        }
        return isSuccessful;
    }
}
