package com.bayraktar.scrum.ui.task.detail;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bayraktar.scrum.model.Task;
import com.bayraktar.scrum.model.TaskComment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.bayraktar.scrum.App.firebaseService;

public class TaskDetailViewModel extends ViewModel {
    private MutableLiveData<Task> mutableLiveData;

    public TaskDetailViewModel() {
        mutableLiveData = new MutableLiveData<>();
    }

    public LiveData<Task> getTask(String projectID, String taskID) {

        firebaseService.getTask(taskID, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Task task = snapshot.getValue(Task.class);
                    mutableLiveData.setValue(task);
                } else {
                    mutableLiveData.setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mutableLiveData.setValue(null);
            }
        });
        return mutableLiveData;
    }
}