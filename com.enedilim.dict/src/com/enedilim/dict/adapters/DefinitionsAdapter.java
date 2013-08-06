package com.enedilim.dict.adapters;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.enedilim.dict.R;
import com.enedilim.dict.entity.Definition;

public class DefinitionsAdapter extends ArrayAdapter<Definition> {

	// static to save the reference to the outer class and to avoid access to
	// any members of the containing class
	static class ViewHolder {
		public TextView viewDefinition;
		public TextView viewDefinitionCount;
		public LinearLayout layoutExamples;
		public TextView viewSee;
	}

	// Two variables to alter the AutoCompleteTextView on "see word" click
	private AutoCompleteTextView autocomplete;
	private String lookupWord;

	public DefinitionsAdapter(Context context, int textViewResourceId,
			List<Definition> items, AutoCompleteTextView autocomplete) {
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
			v = vi.inflate(R.layout.list_item_def, null);

			holder = new ViewHolder();
			holder.viewDefinition = (TextView) v.findViewById(R.id.textViewDef);
			holder.viewDefinitionCount = (TextView) v
					.findViewById(R.id.textViewDefCount);
			holder.viewSee = (TextView) v.findViewById(R.id.textViewSee);
			holder.layoutExamples = (LinearLayout) v
					.findViewById(R.id.layoutExamples);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		
		// Clicking "see word" would change the value of auto-completer
		holder.viewSee.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				autocomplete.setText(lookupWord);
			}
		});

		Definition d = getItem(position);
		if (d != null) {
			holder.viewDefinitionCount.setText((position + 1) + ". ");

			if (d.getDefinition() != null) {
				String definition = d.getDefinition();
				
				if (d.getCategory() != null) {
					definition = "<font color=\"#000099\"><i>[" 
						+ d.getCategory() + "]</i></font>  " + definition;
				}
				
				holder.viewDefinition.setText(Html.fromHtml(definition));
			} 
			else if (d.getSeePhrase() != null){
				String s = "<font color=\"#990000\"><i>seret</i></font> ";
				holder.viewDefinition.setText(Html.fromHtml(s + d.getSeePhrase()));
			}
			else if (d.getSee() != null) {
				holder.viewSee.setVisibility(View.VISIBLE);
				lookupWord = d.getSee();
				
				String seret = "<font color=\"#990000\"><i>seret</i></font> ";
				if(d.getCategory() != null){
					seret = "<font color=\"#000099\"><i>[" 
						+ d.getCategory() + "]</i></font>  " + seret;
				}			
				holder.viewDefinition.setText(Html.fromHtml(seret));
				
				String see = d.getSee();				
				if(d.getSeeHomonym() > 0){
					see += "<sup><small>" + d.getSeeHomonym()+ "</small></sup>";
				}
				if(d.getSeeDefinition() != null){
					see += " (" + d.getSeeDefinition() + ")";
				}
				
				holder.viewSee.setText(Html.fromHtml(see));
			}

			// Load Examples
			if (holder.layoutExamples.getChildCount() == 0) {
				ExamplesAdapter adapter = new ExamplesAdapter(
						this.getContext(), R.layout.list_item_example,
						d.getExamples());

				for (int i = 0; i < d.getExamples().size(); i++) {
					View view = adapter.getView(i, null, holder.layoutExamples);
					holder.layoutExamples.addView(view);
				}
			}
		}
		return v;
	}
}
