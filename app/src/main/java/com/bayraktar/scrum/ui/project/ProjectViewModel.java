package com.bayraktar.scrum.ui.project;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bayraktar.scrum.model.Invitation;
import com.bayraktar.scrum.model.Project;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.bayraktar.scrum.App.firebaseDatabase;

public class ProjectViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<List<Project>> mutableLiveData;
    private DatabaseReference projectsRef = firebaseDatabase.getReference("PROJECTS");


    public ProjectViewModel() {
        mutableLiveData = new MutableLiveData<>();
    }

    public LiveData<List<Project>> getPublicProjects() {
        final List<Project> projectList = new ArrayList<>();
        projectsRef.orderByChild("privacyMode").equalTo(false).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                projectList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.exists()) {
                            Project project = dataSnapshot.getValue(Project.class);
                            if (project != null) {
                                projectList.add(project);
                            }
                        }
                    }
                }
                mutableLiveData.setValue(projectList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mutableLiveData.setValue(null);
            }
        });
        return mutableLiveData;
    }

    public LiveData<List<Project>> getMyProjects(final String userID) {
        final List<Project> projectList = new ArrayList<>();
        if (!TextUtils.isEmpty(userID)) {
            projectsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    projectList.clear();
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.exists()) {
                                Project project = dataSnapshot.getValue(Project.class);
                                if (project != null) {

                                    if ((project.getConstituentID().equals(userID)) || (project.getMembers() != null && project.getMembers().containsKey(userID))) {
                                        projectList.add(project);
                                    }
                                }
                            }
                        }
                    }
                    mutableLiveData.setValue(projectList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    mutableLiveData.setValue(null);
                }
            });
        }

        return mutableLiveData;
    }
}