package com.bayraktar.scrum.ui.project.applications;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bayraktar.scrum.model.Project;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.bayraktar.scrum.App.firebaseService;

public class ProjectApplicationsViewModel extends ViewModel {

    private MutableLiveData<Project> mutableProject;

    public ProjectApplicationsViewModel() {
        mutableProject = new MutableLiveData<>();
    }

    public LiveData<Project> getProject(String projectID) {
        firebaseService.getProject(projectID, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Project project = snapshot.getValue(Project.class);
                    if (project != null) {
                        mutableProject.setValue(project);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mutableProject.setValue(null);
            }
        });
        return mutableProject;
    }
}
