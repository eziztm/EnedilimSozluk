package com.enedilim.dict.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.enedilim.dict.R;
import com.enedilim.dict.entity.Phrase;

/**
 * Custom adapter to show Example items (from Definition or Phrase) in a
 * ListView
 * 
 * @author Eziz Annagurban
 * 
 */
class PhrasesAdapter extends ArrayAdapter<Phrase> {

	static class ViewHolder {
		public TextView viewPhrase;
		public LinearLayout layoutPhraseDef;
	}

	private final AutoCompleteTextView autocomplete;

	public PhrasesAdapter(Context context, List<Phrase> items, AutoCompleteTextView autocomplete) {
		super(context, R.layout.list_item_phrase, items);
		this.autocomplete = autocomplete;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_item_phrase, parent, false);

			holder = new ViewHolder();
			holder.viewPhrase = (TextView) v.findViewById(R.id.textViewPhrase);
			holder.layoutPhraseDef = (LinearLayout) v.findViewById(R.id.layoutPhraseDefinitions);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}

		Phrase phrase = getItem(position);
		if (phrase != null) {
			holder.viewPhrase.setText(phrase.getPhrase());

			// Remove visibility of ListView if Example list is empty
			if (phrase.getDefinitions().isEmpty()) {
				holder.layoutPhraseDef.setVisibility(View.GONE);
			} else {
				holder.layoutPhraseDef.setVisibility(View.VISIBLE);
				
				if (holder.layoutPhraseDef.getChildCount() == 0) {
					DefinitionsAdapter adapter = new DefinitionsAdapter(this.getContext(), phrase.getDefinitions(), autocomplete);

					for (int i = 0; i < phrase.getDefinitions().size(); i++) {
						View view = adapter.getView(i, null, holder.layoutPhraseDef);
						holder.layoutPhraseDef.addView(view);
					}
				}
			}
		}

		return v;
	}

}
