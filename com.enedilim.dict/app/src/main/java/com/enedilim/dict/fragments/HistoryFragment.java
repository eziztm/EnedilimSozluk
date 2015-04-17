package com.enedilim.dict.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.enedilim.dict.R;
import com.enedilim.dict.utils.CacheManager;

import java.util.List;

/**
 * Fragment for History.
 */
public class HistoryFragment extends Fragment {
    private OnWordSelectedListener mCallback;
    private List<String> words;

    // Container Activity must implement this interface
    public interface OnWordSelectedListener {
        public void onWordSelected(String word);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_history, container, false);
        ListView historyListView = (ListView) v.findViewById(R.id.listViewHistory);
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                   @Override
                                                   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                       mCallback.onWordSelected(words.get(position));
                                                   }
                                               }
        );

        words = CacheManager.getInstance().getCachedWords();

        final ArrayAdapter<String> searchedWords = new ArrayAdapter<String>(getActivity(), R.layout.list_item, words);
        historyListView.setAdapter(searchedWords);
        historyListView.requestFocus();

        ImageButton clearHistoryButton = (ImageButton) v.findViewById(R.id.clearHistoryButton);
        clearHistoryButton.setEnabled(!words.isEmpty());
        clearHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheManager.getInstance().clearCacheDirectory();
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