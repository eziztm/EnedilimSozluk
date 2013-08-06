package com.enedilim.dict.adapters;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.enedilim.dict.R;
import com.enedilim.dict.entity.Example;

/**
 * Custom adapter to show Example items (from Definition or Phrase) in a
 * ListView
 * 
 * @author Eziz Annagurban
 * 
 */
public class ExamplesAdapter extends ArrayAdapter<Example> {

	// static to save the reference to the outer class and to avoid access to
	// any members of the containing class
	static class ViewHolder {
		public TextView viewExample;
		public TextView viewSource;
	}

	private List<Example> items;

	public ExamplesAdapter(Context context, int textViewResourceId,
			List<Example> items) {
		super(context, textViewResourceId, items);
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_item_example, null);

			holder = new ViewHolder();
			holder.viewExample = (TextView) v.findViewById(R.id.textViewExample);
			holder.viewSource = (TextView) v.findViewById(R.id.textViewExampleSource);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}

		Example example = items.get(position);
		if (example != null) {
			holder.viewExample.setText(example.getExample());

			if (example.getSource() != null && !example.getSource().equals("")) {
				holder.viewSource.setText(Html.fromHtml("<small>("
						+ example.getSource() + ")</small>"));
			}
			else{
				holder.viewSource.setVisibility(View.GONE);
			}
		}

		return v;
	}

}
