package com.bayraktar.scrum.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bayraktar.scrum.R;
import com.bayraktar.scrum.adapter.NotificationAdapter;
import com.bayraktar.scrum.model.NotificationPermission;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private NotificationsViewModel notificationsViewModel;
    SwitchCompat swCloseAll, swTasks, swProjects;
    RecyclerView rvTaskNotifications, rvProjectNotifications;
    CardView cvCloseAll, cvTasks, cvProjects;

    boolean prevTasks, prevProjects;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        swCloseAll = view.findViewById(R.id.swCloseAll);
        swTasks = view.findViewById(R.id.swTasks);
        swProjects = view.findViewById(R.id.swProjects);

        cvCloseAll = view.findViewById(R.id.cvCloseAll);
        cvTasks = view.findViewById(R.id.cvTasks);
        cvProjects = view.findViewById(R.id.cvProjects);

        rvTaskNotifications = view.findViewById(R.id.rvTaskNotifications);
        rvProjectNotifications = view.findViewById(R.id.rvProjectNotifications);

        cvCloseAll.setOnClickListener(this);
        cvTasks.setOnClickListener(this);
        cvProjects.setOnClickListener(this);

        swCloseAll.setOnCheckedChangeListener(this);
        swTasks.setOnCheckedChangeListener(this);
        swProjects.setOnCheckedChangeListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);

        List<NotificationPermission> taskPermissionList = new ArrayList<>();
        NotificationPermission taskPermission =
                new NotificationPermission(0, "Görev bildirimi", true);

        taskPermissionList.add(taskPermission);
        taskPermissionList.add(taskPermission);
        taskPermissionList.add(taskPermission);
        taskPermissionList.add(taskPermission);
        taskPermissionList.add(taskPermission);

        List<NotificationPermission> projectPermissionList = new ArrayList<>();
        NotificationPermission projectPermission =
                new NotificationPermission(0, "Proje bildirimi", true);

        projectPermissionList.add(projectPermission);
        projectPermissionList.add(projectPermission);
        projectPermissionList.add(projectPermission);

        NotificationAdapter tasksAdapter = new NotificationAdapter(tasksListener);
        tasksAdapter.setNotificationPermissionList(taskPermissionList);
        NotificationAdapter projectsAdapter = new NotificationAdapter(projectsListener);
        projectsAdapter.setNotificationPermissionList(projectPermissionList);


        rvTaskNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTaskNotifications.setAdapter(tasksAdapter);

        rvProjectNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        rvProjectNotifications.setAdapter(projectsAdapter);

    }

    void closeAll() {
        boolean isAllCloseChecked = swCloseAll.isChecked();
        if (isAllCloseChecked) {
            closeTasks();
            closeProjects();
        } else {
            setDefault();
        }
    }

    void setDefault() {
        swTasks.setEnabled(true);
        swProjects.setEnabled(true);

        swTasks.setChecked(prevTasks);
        swProjects.setChecked(prevProjects);
    }

    void closeTasks() {
        prevTasks = swTasks.isChecked();
        swTasks.setChecked(false);
        swTasks.setEnabled(false);
        rvTaskNotifications.setVisibility(View.GONE);
    }

    void closeProjects() {
        prevProjects = swProjects.isChecked();
        swProjects.setChecked(false);
        swProjects.setEnabled(false);
        rvProjectNotifications.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cvCloseAll:
                swCloseAll.setChecked(!swCloseAll.isChecked());
                break;
            case R.id.cvTasks:
                swTasks.setChecked(!swTasks.isChecked());
                break;
            case R.id.cvProjects:
                swProjects.setChecked(!swProjects.isChecked());
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.swCloseAll:
                closeAll();
                break;
            case R.id.swTasks:
                if (!isChecked) {
                    rvTaskNotifications.setVisibility(View.GONE);
                } else {
                    rvTaskNotifications.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.swProjects:
                if (!isChecked) {
                    rvProjectNotifications.setVisibility(View.GONE);
                } else {
                    rvProjectNotifications.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    NotificationAdapter.OnNotificationListener tasksListener = new NotificationAdapter.OnNotificationListener() {
        @Override
        public void onNotificationClick(int position, boolean isChecked) {
            Log.d("TAG", "onNotificationClick: tasksListener " + position + " " + isChecked);
        }
    };

    NotificationAdapter.OnNotificationListener projectsListener = new NotificationAdapter.OnNotificationListener() {
        @Override
        public void onNotificationClick(int position, boolean isChecked) {
            Log.d("TAG", "onNotificationClick: projectsListener " + position + " " + isChecked);
        }
    };
}