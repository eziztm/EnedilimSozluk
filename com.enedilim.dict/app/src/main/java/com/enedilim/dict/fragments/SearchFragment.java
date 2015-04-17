package com.enedilim.dict.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.enedilim.dict.R;
import com.enedilim.dict.adapters.WordSuggestionsCursorAdapter;
import com.enedilim.dict.adapters.WordsAdapter;
import com.enedilim.dict.fragments.support.WordFetchResult;
import com.enedilim.dict.fragments.support.WordFetchTask;
import com.enedilim.dict.utils.DatabaseHelper;

/**
 * Fragment retrieves and display words and their definitions.
 *
 * @author Nazar
 * @version 1.0
 */
public class SearchFragment extends Fragment implements WordFetchTask.WordFetchListener {
    private static final String TAG = SearchFragment.class.getSimpleName();
    private ListView definitionView;
    private ProgressBar progressBar;
    private AutoCompleteTextView searchField;
    private TextView errorMessage;
    private String displayWord;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        definitionView = (ListView) view.findViewById(R.id.listViewWordDefinitions);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        searchField = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteWord);
        errorMessage = (TextView) view.findViewById(R.id.textViewErrorMessage);


        if (!searchField.getText().toString().equals(displayWord)) {
            searchField.setText("");
        }
        if (displayWord != null) {
            displayDefinitions();
        }

        setUpAutoCompleteSearchField();
        return view;
    }

    private void setUpAutoCompleteSearchField() {
        // Configure the auto-completer of dictionary words
        CursorAdapter cursorAdapter = new WordSuggestionsCursorAdapter(getActivity(), DatabaseHelper.getInstance(getActivity()));
        searchField.setAdapter(cursorAdapter);

        // On item click listener. Fetch given word from server
        searchField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getItemAtPosition(position);
                String selectedWord = c.getString(c.getColumnIndexOrThrow("word"));
                searchField.dismissDropDown();
                setDisplayWord(selectedWord);
                displayDefinitions();
            }
        });

        searchField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(FragmentActivity.INPUT_METHOD_SERVICE);
                if (hasFocus) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
                } else {
                    imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                }
            }
        });

        // Reset the auto-complete field on long click
        searchField.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                searchField.setText("");
                return false;
            }
        });
    }

    void displayDefinitions() {
        definitionView.setVisibility(View.GONE);
        errorMessage.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        WordFetchTask wordFetchTask = new WordFetchTask(getActivity(), this);
        wordFetchTask.execute(displayWord);
    }

    public void setDisplayWord(String displayWord) {
        Log.d(TAG, "Word is " + displayWord);
        this.displayWord = displayWord;
    }

    @Override
    public void doneFetching(WordFetchResult result) {
        progressBar.setVisibility(View.GONE);
        if (result.isError()) {
            errorMessage.setVisibility(View.VISIBLE);
            errorMessage.requestFocus();
            if (result.getError().isOnline() && result.getError().isHostAvailable()) {
                errorMessage.setText(getResources().getString(R.string.noWordMatch, result.getWord()));
            } else if (result.getError().isOnline()) {
                errorMessage.setText(getResources().getString(R.string.enedilimUnavailable));
            } else {
                errorMessage.setText(getResources().getString(R.string.connectionOfflineError));
            }
        } else if (result.getWords().isEmpty()) {
            errorMessage.setText(getResources().getString(R.string.noWordMatch, result.getWord()));
            errorMessage.setVisibility(View.VISIBLE);
            errorMessage.requestFocus();
        } else {
            WordsAdapter listAdapter = new WordsAdapter(getActivity(), result.getWords(), searchField);
            definitionView.setAdapter(listAdapter);
            definitionView.setVisibility(View.VISIBLE);
            definitionView.requestFocus();
            searchField.setText("");
        }
    }
}