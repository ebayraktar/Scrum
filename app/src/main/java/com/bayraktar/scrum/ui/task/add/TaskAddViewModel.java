package com.bayraktar.scrum.ui.task.add;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bayraktar.scrum.model.Project;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static com.bayraktar.scrum.App.firebaseDatabase;

public class TaskAddViewModel extends ViewModel {
    private MutableLiveData<Project> mutableLiveData;
    private DatabaseReference projectsRef = firebaseDatabase.getReference("PROJECTS");

    public TaskAddViewModel() {
        mutableLiveData = new MutableLiveData<>();
    }

    public LiveData<Project> getProject(String projectID) {
        projectsRef.child(projectID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Project tempProject = snapshot.getValue(Project.class);
                    mutableLiveData.setValue(tempProject);
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
