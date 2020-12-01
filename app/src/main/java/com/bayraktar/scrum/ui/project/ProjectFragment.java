package com.bayraktar.scrum.ui.project;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bayraktar.scrum.R;
import com.bayraktar.scrum.adapter.ProjectAdapter;
import com.bayraktar.scrum.model.Project;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static com.bayraktar.scrum.App.currentUser;

public class ProjectFragment extends Fragment implements ProjectAdapter.OnProjectListener, View.OnClickListener {

    private ProjectViewModel mViewModel;
    private TextView tvNoProject;
    private FloatingActionButton fab;
    RecyclerView rvProjectList;
    ProjectAdapter projectAdapter;
    List<Project> projectList;
    LottieAnimationView av_splash_animation;

    public static ProjectFragment newInstance() {
        return new ProjectFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project, container, false);
        mViewModel = new ViewModelProvider(this).get(ProjectViewModel.class);

        av_splash_animation = view.findViewById(R.id.av_splash_animation);

        fab = view.findViewById(R.id.fab);
        tvNoProject = view.findViewById(R.id.tvNoProject);
        rvProjectList = view.findViewById(R.id.rvProjectList);
        projectAdapter = new ProjectAdapter(getContext(), this);
        rvProjectList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvProjectList.setAdapter(projectAdapter);

        fab.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (currentUser != null) {
            fab.setVisibility(View.VISIBLE);
            mViewModel.getMyProjects(currentUser.getUserID()).observe(getViewLifecycleOwner(), new Observer<List<Project>>() {
                @Override
                public void onChanged(List<Project> projects) {
                    setProject(projects);
                }
            });
        } else {
            isLoadSuccess(false);
        }
    }


    void setProject(List<Project> projects) {
        isLoadSuccess(projects != null && projects.size() > 0);
        if (projects != null) {
            projectList = projects;
            projectAdapter.setProjectList(projectList);
        }
    }

    void isLoadSuccess(boolean isSuccess) {
        av_splash_animation.setVisibility(View.GONE);
        if (isSuccess) {
            rvProjectList.setVisibility(View.VISIBLE);
            tvNoProject.setVisibility(View.GONE);
        } else {
            rvProjectList.setVisibility(View.GONE);
            tvNoProject.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onProjectClick(View v, int position) {
        Project taskListModel = projectList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("projectID", taskListModel.getProjectID());
        bundle.putString("title", taskListModel.getProjectName());
        Navigation.findNavController(v).navigate(R.id.action_nav_projects_to_nav_project_detail, bundle);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            Navigation.findNavController(v).navigate(R.id.action_nav_projects_to_nav_add_project);
        }
    }
}