package com.bayraktar.scrum.ui.task;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bayraktar.scrum.model.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.bayraktar.scrum.App.firebaseDatabase;

public class TaskViewModel extends ViewModel {
    private final MutableLiveData<List<Task>> mutableData;
    private final DatabaseReference tasksRef;

    public TaskViewModel() {
        this.mutableData = new MutableLiveData<>();
        tasksRef = firebaseDatabase.getReference("TASKS");
    }

    public LiveData<List<Task>> getTaskList(final String userID) {
        final List<Task> taskList = new ArrayList<>();
        tasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.exists()) {
                            Task tempTask = dataSnapshot.getValue(Task.class);
                            if (tempTask != null) {
                                if (tempTask.getConstituent().equals(userID)
                                        || (tempTask.getAttendants() != null && tempTask.getAttendants().containsKey(userID))
                                ) {
                                    taskList.add(tempTask);
                                }
                            }
                        }
                    }
                }
                mutableData.setValue(taskList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mutableData.setValue(null);
            }
        });
        return mutableData;
    }
}