package com.enedilim.dict.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.enedilim.dict.R;
import com.enedilim.dict.utils.DatabaseHelper;

/**
 * Initializes the word list dictionary.
 *
 * Should be called the first time the application is run.
 *
 * @author Nazar Annagurban
 */
public class WordListInitializerTask extends AsyncTask<DatabaseHelper, Integer, Boolean> {

    private static final String TAG = WordListInitializerTask.class.getSimpleName();
    private ProgressDialog loadDialog;
    private Context context;

    public WordListInitializerTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        loadDialog = ProgressDialog.show(context, "", context.getString(R.string.buildingDatabase), true);
    }

    @Override
    protected void onPostExecute(Boolean successful) {
        loadDialog.dismiss();
        if (!successful) {
            String toastDbMsg = context.getResources().getString(R.string.errorDb);
            Toast.makeText(context, toastDbMsg, Toast.LENGTH_LONG).show();
            Log.e(TAG, "Exception while creating database.");
        }
    }

    @Override
    protected Boolean doInBackground(DatabaseHelper... dbHelpers) {
        if (dbHelpers[0] == null) {
            return false;
        }

        SQLiteDatabase db = dbHelpers[0].getWritableDatabase();
        boolean result = db.isOpen() && !db.isReadOnly();
        db.close();
        return result;
    }
}
