package com.bayraktar.scrum.ui.project.detail.main;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bayraktar.scrum.R;
import com.bayraktar.scrum.adapter.ProjectTaskAdapter;
import com.bayraktar.scrum.model.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static com.bayraktar.scrum.App.firebaseService;

public class ProjectDetailMainFragment extends Fragment implements ProjectTaskAdapter.OnProjectTaskListener {

    private static final String TASK_STATUS = "taskStatus";
    private static final String PROJECT_ID = "projectID";
    private int taskStatus;
    private String projectID;

    TextView tvNoTask;
    LottieAnimationView av_splash_animation;
    RecyclerView rvTaskList;
    ProjectTaskAdapter projectTaskAdapter;
    List<String> currentTaskList;

    public ProjectDetailMainFragment() {
    }

    private ProjectDetailMainViewModel mViewModel;

    public static ProjectDetailMainFragment newInstance(int param1, String projectID) {

        ProjectDetailMainFragment fragment = new ProjectDetailMainFragment();
        Bundle args = new Bundle();
        args.putInt(TASK_STATUS, param1);
        args.putString(PROJECT_ID, projectID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            taskStatus = getArguments().getInt(TASK_STATUS);
            projectID = getArguments().getString(PROJECT_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(ProjectDetailMainViewModel.class);
        View view = inflater.inflate(R.layout.fragment_project_detail_main, container, false);
        tvNoTask = view.findViewById(R.id.tvNoTask);
        av_splash_animation = view.findViewById(R.id.av_splash_animation);
        rvTaskList = view.findViewById(R.id.rvTaskList);

        projectTaskAdapter = new ProjectTaskAdapter(getContext(), this);
        rvTaskList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTaskList.setAdapter(projectTaskAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel.getTasks(taskStatus, projectID).observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> taskListModels) {
                setProjectTasks(taskListModels);
            }
        });
    }


    void setProjectTasks(List<String> taskList) {
        isLoadSuccess(taskList != null && taskList.size() > 0);
        if (taskList != null) {
            currentTaskList = taskList;
            projectTaskAdapter.setProjectTaskList(currentTaskList);
        }
    }

    void isLoadSuccess(boolean isSuccess) {
        av_splash_animation.setVisibility(View.GONE);
        if (isSuccess) {
            rvTaskList.setVisibility(View.VISIBLE);
            tvNoTask.setVisibility(View.GONE);
        } else {
            rvTaskList.setVisibility(View.GONE);
            tvNoTask.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onProjectTaskClick(final View v, int position) {
        String taskID = currentTaskList.get(position);
        final Bundle bundle = new Bundle();
        bundle.putString("taskID", taskID);
        bundle.putString("projectID", projectID);

        firebaseService.getTask(taskID, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Task task = snapshot.getValue(Task.class);
                    if (task != null) {
                        if (task.getTaskName() != null && !task.getTaskName().equals("")) {
                            bundle.putString("title", task.getTaskName());
                            Navigation.findNavController(v).navigate(R.id.action_nav_project_detail_to_nav_task_detail, bundle);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("HATA")
                                    .setMessage("Görev adı olmadığından detaylara gidemezsiniz")
                                    .setNegativeButton("TAMAM", null)
                                    .show().create();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}