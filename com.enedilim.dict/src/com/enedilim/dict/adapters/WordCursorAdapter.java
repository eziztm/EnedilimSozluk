package com.enedilim.dict.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.enedilim.dict.R;
import com.enedilim.dict.utils.DatabaseHelper;

/**
 * Custom CursorAdapter for auto-completer in word search 
 * @author Eziz Annagurban
 *
 */
public class WordCursorAdapter extends CursorAdapter {
	
	private DatabaseHelper databaseHelper;
	private final String COLUMN_WORD = "word";

	public WordCursorAdapter(Context context, Cursor c, DatabaseHelper dbHelper) {
		super(context, c);
		databaseHelper = dbHelper;
	}
	
    /**
     * Called by the AutoCompleteTextView field to display the text for a
     * particular choice in the list.
     *
     * @param context
     *            The context (Activity) to which this form belongs;
     *            equivalent to {@code SelectState.this}.
     * @param cursor
     *            The cursor for the list of choices, positioned to a
     *            particular row.
     * @param parent
     *            The ListView that contains the list of choices.
     *
     * @return A new View (really, a TextView) to hold a particular choice.
     */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		return (TextView) inflater.inflate(R.layout.list_item, parent, false);
		//return (TextView) inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
	}
	
    /**
     * Called by the ListView for the AutoCompleteTextView field to display
     * the text for a particular choice in the list.
     *
     * @param view
     *            The TextView used by the ListView to display a particular
     *            choice.
     * @param context
     *            The context (Activity) to which this form belongs;
     *            equivalent to {@code SelectState.this}.
     * @param cursor
     *            The cursor for the list of choices, positioned to a
     *            particular row.
     */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		((TextView) view).setText(cursor.getString(cursor.getColumnIndex(COLUMN_WORD)));
	}

    /**
     * Called by the AutoCompleteTextView field to get the text that will be
     * entered in the field after a choice has been made.
     *
     * @param Cursor
     *            The cursor, positioned to a particular row in the list.
     * @return A String representing the row's text value. (Note that this
     *         specializes the base class return value for this method,
     *         which is {@link CharSequence}.)
     */
	@Override
	public String convertToString(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndex(COLUMN_WORD)); 
	}

    /**
     * Invoked by the AutoCompleteTextView field to get completions for the
     * current input.
     *
     * NOTE: If this method either throws an exception or returns null, the
     * Filter class that invokes it will log an error with the traceback,
     * but otherwise ignore the problem. No choice list will be displayed.
     * Watch those error logs!
     *
     * @param constraint
     *            The input entered thus far. The resulting query will
     *            search for states whose name begins with this string.
     * @return A Cursor that is positioned to the first row (if one exists)
     *         and managed by the activity.
     */
	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		if (getFilterQueryProvider() != null) {
			return getFilterQueryProvider().runQuery(constraint);
		}

		Cursor cur = null;
		if (constraint != null){
			cur = databaseHelper.retrieveSuggestions(constraint.toString());
		}	
		
		return cur;
	}
}
