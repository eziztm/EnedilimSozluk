package com.enedilim.dict;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.enedilim.dict.adapters.WordCursorAdapter;
import com.enedilim.dict.adapters.WordsAdapter;
import com.enedilim.dict.entity.Word;
import com.enedilim.dict.utils.CacheManager;
import com.enedilim.dict.utils.DatabaseHelper;
import com.enedilim.dict.utils.WordAsyncTask;

/**
 * Main activity. 
 * Looks up and fetches the given word from server and presents it. 
 * 
 * @author Eziz Annagurban
 * @version 1.0
 */
public class Lookup extends Activity {
	
    //Logger tag and final Dialog messages
	private static final String TAG = "com.enedilim.dict.Lookup"; 
    
	// Helpers
	private DatabaseHelper myDbHelper;	
	
	// View objects
	private static AutoCompleteTextView textView;
	private static ListView listView;
	private static TextView emptyView;
	
	// Adapters
	private WordCursorAdapter cursorAdapter;
	private WordsAdapter listAdapter;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Create (if first time) or open the database
		myDbHelper = new DatabaseHelper(this);	
		try {
			String msg = getResources().getString(R.string.msgDbConnection);
			ProgressDialog dialog = ProgressDialog.show(Lookup.this, "",  msg, true);
			
			myDbHelper.createDatabase();
			myDbHelper.openDatabase();
			
			dialog.dismiss();			
		} catch (IOException ioe) {
			String toastDbMsg = getResources().getString(R.string.errorDb);
			Toast.makeText(Lookup.this, toastDbMsg, Toast.LENGTH_LONG).show();
			Log.e(TAG, "Unable to create database: " + ioe.getMessage());	
			throw new Error("Unable to create database");
		} catch (SQLException sqle) {
			String toastDbMsg = getResources().getString(R.string.errorDb);
			Toast.makeText(Lookup.this, toastDbMsg, Toast.LENGTH_LONG).show();
			Log.e(TAG, "SQLException while opening the db: " + sqle.getMessage());			
		}
		
		// Instantiate CacheManager and clean cache directory
		CacheManager cacheManager = CacheManager.getInstance();
		cacheManager.setCacheDir(getCacheDir());
		cacheManager.cleanCache();

		// Get the View element references
		textView = (AutoCompleteTextView) findViewById(R.id.autoCompleteWord);
		listView = (ListView) findViewById(R.id.listViewResult);
		emptyView = (TextView) findViewById(R.id.copyright);
				
		// Copyright message (Show when List is Empty)
		String footer = this.getString(R.string.msgHtmlFooter);
		footer = String.format(footer, Calendar.getInstance().get(Calendar.YEAR));
		emptyView.setMovementMethod(LinkMovementMethod.getInstance());
		emptyView.setText(Html.fromHtml(footer));
		
	    // Configure the auto-completer of dictionary words
	    cursorAdapter = new WordCursorAdapter(this, null, myDbHelper);
	    textView.setAdapter(cursorAdapter);
	    listView.setEmptyView(emptyView);
	        
	    // On item click listener. Fetch given word from server
	    textView.setOnItemClickListener(new OnItemClickListener() {
	    	public void onItemClick(AdapterView<?> parent, View view, int position, long id){   		
	    		// Retrieve the selected item as string
	    		Cursor c = (Cursor) parent.getItemAtPosition(position);	    		
            	String selected = c.getString(c.getColumnIndexOrThrow("word"));

            	// Get XML, parse it and present it in ListView
            	try{
                	WordAsyncTask parser = new WordAsyncTask(Lookup.this);
                	parser.execute(selected);               	             	
                	List<Word> words = parser.get();
                	
                	if(!words.isEmpty()){
                    	listAdapter = new WordsAdapter(Lookup.this, R.layout.list_item_word, words, textView);
                        listView.setAdapter(listAdapter);                        
                	}
                	else {
            			String toastMsg = getResources().getString(R.string.errorXml);
            			Toast.makeText(Lookup.this, toastMsg, Toast.LENGTH_LONG).show();
                	}          	
            	} catch (InterruptedException e) {
        			String errorMsg = getResources().getString(R.string.errorCon);
        			Toast.makeText(Lookup.this, errorMsg, Toast.LENGTH_LONG).show();
        			Log.e(TAG, "Task interrupted " + e.getMessage());			
				} catch (ExecutionException e) {
        			String errorMsg = getResources().getString(R.string.errorCon);
        			Toast.makeText(Lookup.this, errorMsg, Toast.LENGTH_LONG).show();
        			Log.e(TAG, "Task execution exception " + e.getMessage());		
				}          
	    	}
		});
	    
	    // Reset the auto-complete field on long click
	    textView.setOnLongClickListener(new OnLongClickListener() {		
			@Override
			public boolean onLongClick(View v) {
				textView.setText("");
				return false;
			}
		});
	  
    }
      
	@Override
	public void onDestroy() {
		myDbHelper.close();	
		super.onDestroy();
	}
	
	/**
	 * "About" Menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.aboutMenu:
			Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.about_dialog);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);
			TextView year = (TextView) dialog.findViewById(R.id.copyrightYear);
			String yr = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
			year.setText(yr);
			
			Button button = (Button) dialog.findViewById(R.id.linkButton);
			
			// Open the browser with the site
			button.setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {
					String url = getString(R.string.url);
					Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse(url));
					startActivity( browse );
				}
			});
			dialog.show();
		    break;
		}
			
		return true;
	}
}