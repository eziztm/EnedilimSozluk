package com.enedilim.dict.adapters;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.enedilim.dict.R;
import com.enedilim.dict.entity.Word;

/**
 * Custom words array adapter to display Word items in ListView
 * 
 * @author Eziz Annagurban
 * 
 */
public class WordsAdapter extends ArrayAdapter<Word> {

	// static to save the reference to the outer class and to avoid access to
	// any members of the containing class
	static class ViewHolder {
		public TextView wordView;
		public TextView pronunView;
		public LinearLayout layoutDefinitions;
		public LinearLayout layoutPhrases;
	}

	private AutoCompleteTextView autocomplete;

	public WordsAdapter(Context context, int textViewResourceId,
			List<Word> items, AutoCompleteTextView autocomplete) {
		super(context, textViewResourceId, items);
		this.autocomplete = autocomplete;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_item_word, null);

			holder = new ViewHolder();
			holder.wordView = (TextView) v.findViewById(R.id.textViewWord);
			holder.pronunView = (TextView) v.findViewById(R.id.textViewPronun);
			holder.layoutDefinitions = (LinearLayout) v
					.findViewById(R.id.layoutDefinitions);
			holder.layoutPhrases = (LinearLayout) v
					.findViewById(R.id.layoutPhrases);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}

		Word w = getItem(position);
		if (w != null) {
			// Create HTML-style formatted text
			String wordstring = "<big><b>" + w.getWord() + "</b></big>";
			if (w.getHomonym() > 0) {
				wordstring += "<sup><small>" + w.getHomonym() + "<small></sup>";
			}
			
			holder.wordView.setText(Html.fromHtml(wordstring));
			
			if (w.getPronun() != null) {
				holder.pronunView.setText(w.getPronun());
				holder.pronunView.setVisibility(View.VISIBLE);
			} 

			/**
			 * Load Definitions and Phrases
			 * 
			 * These following methods are not considered very efficient as they
			 * do not re-use heavy methods. Trying to nested ListViews resulted
			 * in wrong height for rows and some of the content wasn't visible.
			 * These methods just iterate through ArrayAdapter instances and add
			 * the resulting views to the view hierarchy. To be re-done in
			 * future when I get a better understanding of the mechanism.
			 */

			// Load Definitions
			if (holder.layoutDefinitions.getChildCount() == 0) {
				DefinitionsAdapter adapter = new DefinitionsAdapter(
						this.getContext(), R.layout.list_item_def,
						w.getDefinitions(), autocomplete);

				for (int i = 0; i < w.getDefinitions().size(); i++) {
					View view = adapter.getView(i, null,
							holder.layoutDefinitions);
					holder.layoutDefinitions.addView(view);
				}
			}

			// Load Phrases
			if (holder.layoutPhrases.getChildCount() == 0) {
				PhrasesAdapter adapter = new PhrasesAdapter(this.getContext(),
						R.layout.list_item_phrase, w.getPhrases(), autocomplete);

				for (int i = 0; i < w.getPhrases().size(); i++) {
					View view = adapter.getView(i, null, holder.layoutPhrases);
					holder.layoutPhrases.addView(view);
				}

			}
		}

		return v;
	}

}
