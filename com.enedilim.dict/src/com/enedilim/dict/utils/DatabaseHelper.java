package com.enedilim.dict.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    //Logger tag
    private static final String TAG = "com.enedilim.dict.DatabaseHelper";  
	
    //The Android's default system path of your application database.
    private static final String DB_PATH = "/data/data/com.enedilim.dict/databases/"; 
    private static final String DB_NAME = "dict.sqlite";
    private SQLiteDatabase myDatabase;  
    private final Context myContext;
 
    public DatabaseHelper(Context context) { 	 
    	super(context, DB_NAME, null, 1);
        this.myContext = context;
    }	
	
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
	
	/**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDatabase() throws IOException{
    	if(!databaseExists()){
    		Log.i(TAG, "Database doesn't exist. Creating new.");
    		
    		// Creates or opens an empty database in system path
    		// which will be overwritten by the included db
        	this.getReadableDatabase();
 
        	try {
    			copyDatabase();
    		} catch (IOException e) {
        		throw new Error("Error copying database");
        	} 	
    	} 
    }
    
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean databaseExists(){
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		SQLiteDatabase checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);	
    		checkDB.close();
    		
    		return true;
    	}catch(SQLiteException e){
    		//database does't exist yet.
    		return false;
    	}
    }
 
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDatabase() throws IOException{
 
    	//Open your local db as the input stream
    	InputStream myInput = myContext.getAssets().open(DB_NAME);
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
    	
    	Log.i(TAG, "Done copying data to database");
    }
 
    public void openDatabase() throws SQLException{
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
    	myDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }
 
    @Override
	public synchronized void close() { 	    
    	if(myDatabase != null)
    		myDatabase.close();
        
    	super.close();
	}
    
    /**
     * Retrieve suggestion words for auto-completer
     * @param Beginnign of a word for auto-completer 
     * @return Cursor instance with all query data
     */
	public Cursor retrieveSuggestions(String string) {
		String sql = "SELECT * FROM words w WHERE w.word LIKE ? GROUP BY w.word ORDER BY w.word";
		String[] selectionArgs = new String[] {string + "%"};
		return myDatabase.rawQuery(sql, selectionArgs);
	}
}
