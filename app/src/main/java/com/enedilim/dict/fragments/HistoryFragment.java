package com.enedilim.dict.fragments;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.enedilim.dict.R;
import com.enedilim.dict.utils.DatabaseHelper;

import java.util.List;

/**
 * Fragment for History.
 */
public class HistoryFragment extends Fragment {
    private OnWordSelectedListener mCallback;
    private List<String> words;

    // Container Activity must implement this interface
    public interface OnWordSelectedListener {
        void onWordSelected(String word);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_history, container, false);
        ListView historyListView = v.findViewById(R.id.listViewHistory);
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                   @Override
                                                   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                       mCallback.onWordSelected(words.get(position));
                                                   }
                                               }
        );

        words = DatabaseHelper.getInstance(getContext()).getRecentlyViewed();

        final ArrayAdapter<String> searchedWords = new ArrayAdapter<>(getActivity(), R.layout.list_item, words);
        historyListView.setAdapter(searchedWords);
        historyListView.requestFocus();

        ImageButton clearHistoryButton = v.findViewById(R.id.clearHistoryButton);
        clearHistoryButton.setEnabled(!words.isEmpty());
        clearHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper.getInstance(getContext()).getClearRecentlyViewed();
                searchedWords.clear();
                v.setEnabled(false);
            }
        });
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (OnWordSelectedListener) activity;
    }
}