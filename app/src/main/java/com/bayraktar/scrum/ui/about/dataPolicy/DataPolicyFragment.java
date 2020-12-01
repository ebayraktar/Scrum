package com.bayraktar.scrum.ui.about.dataPolicy;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bayraktar.scrum.R;

public class DataPolicyFragment extends Fragment {

    WebView wvDataPolicy;

    public static DataPolicyFragment newInstance() {
        return new DataPolicyFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data_policy, container, false);
        wvDataPolicy = view.findViewById(R.id.wvDataPolicy);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        wvDataPolicy.loadUrl(getContext().getString(R.string.url_data_policy));
        // Enable Javascript
        WebSettings webSettings = wvDataPolicy.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        wvDataPolicy.setWebViewClient(new WebViewClient());
    }
}