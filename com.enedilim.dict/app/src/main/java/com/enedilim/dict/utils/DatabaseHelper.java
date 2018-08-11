package com.enedilim.dict.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.enedilim.dict.entity.WordContent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();
    public static final String DB_NAME = "dict.sqlite";
    private static final String CREATE_TABLE_SQL = "create table words (_id integer primary key autoincrement, word text not null, content text, updated integer, viewed integer);";
    private static final String DROP_TABLE = "drop table if exists words;";
    public static final int DATABASE_VERSION = 5;
    public static final int INCLUDED_WORDLIST_VERSION = 4;
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
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
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
        wordPrefix = wordPrefix.replaceAll("ñ", "ň");
        return getReadableDatabase().query("words", null, "word like ?", new String[]{wordPrefix + "%"}, null, null, "word", "50");
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
            Log.i(TAG, "Retrieving included word list...");
            Set<String> wordList = getCompleteWordList();
            Log.i(TAG, "Populating words database...");
            db.beginTransaction();

            SQLiteStatement insertStmt = db.compileStatement("insert into words (word) values (?);");

            for (String word: wordList) {
                insertStmt.bindString(1, word);
                insertStmt.executeInsert();
            }
            db.setTransactionSuccessful();
        } catch (IOException ex) {
            Log.e(TAG, ex.getMessage());
            throw new RuntimeException("Error populating the database");
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
    private Set<String> getCompleteWordList() throws IOException {
        Set<String> words = new HashSet<>(21500);
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

    public void updateWordList(Set<String> onlineWordList) {
        Log.i(TAG, "Updating word list from remote source");

        Cursor cursor = getReadableDatabase().query("words", new String[]{"word"}, null, null, null, null, null, null);
        Set<String> storedWordList = new HashSet<>();
        if (cursor.moveToFirst()) {
            do {
                storedWordList.add(cursor.getString(cursor.getColumnIndex("word")));
            } while (cursor.moveToNext());
        }
        cursor.close();

        Set<String> toAdd = new HashSet<>(onlineWordList);
        toAdd.removeAll(storedWordList);

        Set<String> toRemove = new HashSet<>(storedWordList);
        toRemove.removeAll(onlineWordList);

        if (!toAdd.isEmpty() || !toRemove.isEmpty()) {
            updateWordRows(toAdd, toRemove);
        }
    }

    private void updateWordRows(Set<String> toAdd, Set<String> toRemove) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            if (!toAdd.isEmpty()) {
                SQLiteStatement insertStmt = db.compileStatement("insert into words (word) values (?);");
                for (String word: toAdd) {
                    insertStmt.bindString(1, word);
                    insertStmt.executeInsert();
                }
                Log.i(TAG, "Added " + toAdd.size() + " rows");
            }
            if (!toRemove.isEmpty()) {
                SQLiteStatement deleteStmt = db.compileStatement("delete from words where word = ?;");
                for (String word: toRemove) {
                    deleteStmt.bindString(1, word);
                    deleteStmt.executeUpdateDelete();
                }
                Log.i(TAG, "Removed " + toRemove.size() + " rows");
            }
            db.setTransactionSuccessful();
        }  finally {
            db.endTransaction();
        }
    }

    public WordContent getWordContent(String word) {
        Cursor cursor = getReadableDatabase().query("words", new String[]{"content", "updated"}, "word = ? and content is not null", new String[]{word}, null, null, null, null);
        WordContent result = null;
        if (cursor.moveToFirst()) {
            result = new WordContent(word, cursor.getString(cursor.getColumnIndex("content")), cursor.getLong(cursor.getColumnIndex("updated")));
        }
        cursor.close();

        if (result != null) {
            ContentValues values = new ContentValues();
            values.put("viewed", new Date().getTime());
            getWritableDatabase().update("words", values, "word = ?", new String[]{word});
        }
        return result;
    }

    public void storeWordContent(String word, String content) {
        ContentValues values = new ContentValues();
        long now = new Date().getTime();
        values.put("content", content);
        values.put("viewed", now);
        values.put("updated", now);

        int result =  getWritableDatabase().update("words", values, "word = ?", new String[]{word});
        Log.i(TAG, "Updated rows: " + result);
    }

    public List<String> getRecentlyViewed() {
        Cursor cursor = getReadableDatabase().query("words",  new String[] {"word"}, "viewed is not null", null, null, null, "viewed desc", "50");
        List<String> recentlyViewed = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                recentlyViewed.add(cursor.getString(cursor.getColumnIndex("word")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return recentlyViewed;
    }

    public void getClearRecentlyViewed() {
        ContentValues values = new ContentValues();
        values.putNull("viewed");

        int result =  getWritableDatabase().update("words", values, "viewed is not null", null);
        Log.i(TAG, "Updated rows: " + result);
    }
}
