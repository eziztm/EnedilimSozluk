package com.enedilim.dict.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();
    public static final String DB_NAME = "dict.sqlite";
    private static final String CREATE_TABLE_SQL = "create table words (_id integer primary key, word text not null);";
    private static final String DROP_TABLE = "drop table if exists words;";
    public static final int DATABASE_VERSION = 3;
    private static final String WORDS_ASSET = "words.txt";
    private static DatabaseHelper instance;
    private final Context myContext;

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "Database info: " + DB_NAME + DATABASE_VERSION);
        this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Creating words database: " + db.getPath());
        db.execSQL(CREATE_TABLE_SQL);
        populate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL(DROP_TABLE);
        onCreate(db);
        Log.i(TAG, "Upgrade of database complete.");
    }

    /**
     * Retrieves words for auto-completion
     *
     * @param wordPrefix Beginning of a word for auto-completer
     * @return Cursor instance with all query data
     */
    public Cursor retrieveSuggestions(String wordPrefix) {
        return getReadableDatabase().query("words", null, "word like ?", new String[]{wordPrefix + "%"}, null, null, "_id", "50");
    }

    /**
     * Populates the database with a list of words.
     *
     * @param db
     * @throws Error
     * @throws SQLException
     */
    private void populate(SQLiteDatabase db) throws Error, SQLException {
        try {
            Log.i(TAG, "Retrieving word list...");
            List<String> wordList = getCompleteWordList();
            Log.i(TAG, "Populating words database...");
            db.beginTransaction();

            SQLiteStatement insertStmt = db.compileStatement("insert into words (_id, word) values (?, ?);");
            long rowCount = 0;
            for (int i = 0; i < wordList.size(); i++) {
                insertStmt.bindLong(1, i);
                insertStmt.bindString(2, wordList.get(i));
                rowCount = insertStmt.executeInsert();
            }
            Log.i(TAG, (rowCount + 1) + " rows were inserted.");
            db.setTransactionSuccessful();
        } catch (IOException ex) {
            Log.e(TAG, ex.getMessage());
            throw new Error("Error in populating the database.");
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Retrieves the list of words from a text file.
     *
     * @return List of words
     * @throws IOException
     */
    private List<String> getCompleteWordList() throws IOException {
        List<String> words = new ArrayList<String>(21500);
        BufferedReader bis = null;
        try {
            bis = new BufferedReader(new InputStreamReader(myContext.getAssets().open(WORDS_ASSET, AssetManager.ACCESS_STREAMING), "utf-8"));
            String currentWord = "";
            while (currentWord != null) {
                words.add(currentWord);
                currentWord = bis.readLine();
            }
        } finally {
            if (bis != null) {
                bis.close();
            }
        }
        return words;
    }
}
