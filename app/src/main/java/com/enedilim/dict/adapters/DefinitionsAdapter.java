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
import android.widget.TextView;

import com.enedilim.dict.R;
import com.enedilim.dict.entity.Definition;
import com.enedilim.dict.entity.Example;

class DefinitionsAdapter extends ArrayAdapter<Definition> {
    private static final String DEFINITION_WITH_CATEGORY = "<font color=\"#0C3675\"><i>[%s]</i></font> %s";
    public static final String DEFINITION_CATEGORY_SEE = "<font color=\"#000099\"><i>[%s]</i></font> %s";

    // static to save the reference to the outer class and to avoid access to
    // any members of the containing class
    static class ViewHolder {

        public TextView viewDefinition;
        public TextView viewDefinitionCount;
        public TextView viewSee;
        public TextView viewExamples;
    }
    // Two variables to alter the AutoCompleteTextView on "see word" click
    private final AutoCompleteTextView autocomplete;
    private String lookupWord;

    public DefinitionsAdapter(Context context, List<Definition> items, AutoCompleteTextView autocomplete) {
        super(context, R.layout.list_item_def, items);
        this.autocomplete = autocomplete;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item_def, parent, false);

            holder = new ViewHolder();
            holder.viewDefinition = (TextView) v.findViewById(R.id.textViewDef);
            holder.viewDefinitionCount = (TextView) v.findViewById(R.id.textViewDefCount);
            holder.viewSee = (TextView) v.findViewById(R.id.textViewSee);
            holder.viewExamples = (TextView) v.findViewById(R.id.textViewExamples);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        // Clicking "see word" would change the value of auto-completer
        holder.viewSee.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                autocomplete.setText(lookupWord);
                autocomplete.requestFocus();
                autocomplete.setSelection(lookupWord.length());
            }
        });

        Definition d = getItem(position);
        if (d != null) {
            holder.viewDefinitionCount.setText((position + 1) + ". ");

            if (d.getDefinition() != null) {
                String definition = d.getDefinition();

                if (d.getCategory() != null) {
                    definition = String.format(DEFINITION_WITH_CATEGORY, d.getCategory(), definition);
                }

                holder.viewDefinition.setText(Html.fromHtml(definition));
            } else if (d.getSeePhrase() != null) {
                String s = "<i>Seret</i> ";
                holder.viewDefinition.setText(Html.fromHtml(s + d.getSeePhrase()));
            } else if (d.getSee() != null) {
                holder.viewSee.setVisibility(View.VISIBLE);
                lookupWord = d.getSee();

                String seePretext = "<i>Seret</i> ";
                if (d.getCategory() != null) {
                    seePretext = String.format(DEFINITION_CATEGORY_SEE, d.getCategory(), seePretext);
                }
                holder.viewDefinition.setText(Html.fromHtml(seePretext));

                StringBuilder see = new StringBuilder(d.getSee());
                if (d.getSeeHomonym() > 0) {
                    see.append("<sup><small>").append(d.getSeeHomonym()).append("</small></sup>");
                }
                if (d.getSeeDefinition() != null) {
                    see.append(" (").append(d.getSeeDefinition()).append(")");
                }

                holder.viewSee.setText(Html.fromHtml(see.toString()));
            }
            if (!d.getExamples().isEmpty()) {
                holder.viewExamples.setText(Html.fromHtml(Example.allOf(d.getExamples())));
            } else {
                holder.viewExamples.setVisibility(View.GONE);
            }
        }
        return v;
    }
}
