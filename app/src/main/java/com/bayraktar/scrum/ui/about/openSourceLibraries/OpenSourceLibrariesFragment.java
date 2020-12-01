package com.bayraktar.scrum.ui.about.openSourceLibraries;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bayraktar.scrum.R;

public class OpenSourceLibrariesFragment extends Fragment {

    WebView wvOpenSourceLibraries;

    public static OpenSourceLibrariesFragment newInstance() {
        return new OpenSourceLibrariesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_open_source_libraries, container, false);
        wvOpenSourceLibraries = view.findViewById(R.id.wvOpenSourceLibraries);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
        wvOpenSourceLibraries.loadUrl(getContext().getString(R.string.url_open_source_libraries));
        // Enable Javascript
        WebSettings webSettings = wvOpenSourceLibraries.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        wvOpenSourceLibraries.setWebViewClient(new WebViewClient());
    }

}