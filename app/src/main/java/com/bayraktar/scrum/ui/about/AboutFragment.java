package com.bayraktar.scrum.ui.about;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bayraktar.scrum.R;

public class AboutFragment extends Fragment implements View.OnClickListener {


    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        view.findViewById(R.id.cvDataPolicy).setOnClickListener(this);
        view.findViewById(R.id.cvTermsOfUse).setOnClickListener(this);
        view.findViewById(R.id.cvOpenSourceLibraries).setOnClickListener(this);
        view.findViewById(R.id.cvScrum).setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cvDataPolicy:
                Navigation.findNavController(v).navigate(R.id.nav_data_policy);
                break;
            case R.id.cvTermsOfUse:
                Navigation.findNavController(v).navigate(R.id.nav_terms_of_use);
                break;
            case R.id.cvOpenSourceLibraries:
                Navigation.findNavController(v).navigate(R.id.nav_open_source_libraries);
                break;case R.id.cvScrum:
                Navigation.findNavController(v).navigate(R.id.nav_open_about_app);
                break;
        }
    }
}