package com.enedilim.dict.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.enedilim.dict.R;

/**
 * About section fragment.
 */
public class AboutFragment  extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_about, container, false);
        WebView htmlContent = (WebView) v.findViewById(R.id.htmlContent);
        htmlContent.loadUrl("file:///android_asset/about.html");
        return v;

    }

}