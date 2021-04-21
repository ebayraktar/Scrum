package com.bayraktar.scrum.ui.about.scrum;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bayraktar.scrum.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScrumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScrumFragment extends Fragment {

    WebView wvScrum;

    public static ScrumFragment newInstance() {
        return new ScrumFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scrum, container, false);
        wvScrum = view.findViewById(R.id.wvScrum);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
        wvScrum.loadUrl(getContext().getString(R.string.url_report_problem));
        // Enable Javascript
        WebSettings webSettings = wvScrum.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        wvScrum.setWebViewClient(new WebViewClient());
    }
}