package com.bayraktar.scrum.ui.help;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bayraktar.scrum.R;

public class HelpFragment extends Fragment implements View.OnClickListener {

    public static HelpFragment newInstance() {
        return new HelpFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        view.findViewById(R.id.cvReportProblem).setOnClickListener(this);
        view.findViewById(R.id.cvHelpCenter).setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cvReportProblem:
                Navigation.findNavController(v).navigate(R.id.nav_report_problem);
                break;
            case R.id.cvHelpCenter:
                Navigation.findNavController(v).navigate(R.id.nav_help_center);
                break;
        }
    }
}