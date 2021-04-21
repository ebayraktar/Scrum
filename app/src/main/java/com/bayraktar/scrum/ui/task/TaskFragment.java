package com.bayraktar.scrum.ui.task;

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
import com.bayraktar.scrum.adapter.TaskAdapter;
import com.bayraktar.scrum.model.Task;

import java.util.List;

import static com.bayraktar.scrum.App.currentUser;

public class TaskFragment extends Fragment implements TaskAdapter.OnTaskListener {

    private TaskViewModel mViewModel;
    TextView tvNoTask;
    RecyclerView rvTaskList;
    TaskAdapter taskAdapter;
    List<Task> currentTaskList;
    LottieAnimationView av_splash_animation;


    public static TaskFragment newInstance() {
        return new TaskFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        mViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        tvNoTask = view.findViewById(R.id.tvNoTask);
        rvTaskList = view.findViewById(R.id.rvTaskList);
        av_splash_animation = view.findViewById(R.id.av_splash_animation);

        taskAdapter = new TaskAdapter(getContext(), this);
        rvTaskList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTaskList.setAdapter(taskAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (currentUser != null) {
            mViewModel.getTaskList(currentUser.getUserID()).observe(getViewLifecycleOwner(), new Observer<List<Task>>() {
                @Override
                public void onChanged(List<Task> taskListModels) {
                    setTaskList(taskListModels);
                }
            });
        }
    }

    void setTaskList(List<Task> taskList) {
        isLoadSuccess(taskList != null && taskList.size() > 0);
        if (taskList != null) {
            currentTaskList = taskList;
            taskAdapter.setTaskList(currentTaskList);
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
    public void onTaskClick(View v, int position) {
        Task task = currentTaskList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("taskID", task.getTaskID());
        bundle.putString("title", task.getTaskName());
        bundle.putString("projectID", task.getProjectID());
        Navigation.findNavController(v).navigate(R.id.action_nav_tasks_to_nav_task_detail, bundle);
    }
}