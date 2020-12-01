package com.bayraktar.scrum.ui.about.termsOfUse;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.airbnb.lottie.LottieAnimationView;
import com.bayraktar.scrum.R;

public class TermsOfUseFragment extends Fragment {

    WebView wvTermsOfUse;

    public static TermsOfUseFragment newInstance() {
        return new TermsOfUseFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terms_of_use, container, false);
        wvTermsOfUse = view.findViewById(R.id.wvTermsOfUse);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        wvTermsOfUse.loadUrl(getContext().getString(R.string.url_terms_of_use));

        // Enable Javascript
        WebSettings webSettings = wvTermsOfUse.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        wvTermsOfUse.setWebViewClient(new WebViewClient());
    }

}