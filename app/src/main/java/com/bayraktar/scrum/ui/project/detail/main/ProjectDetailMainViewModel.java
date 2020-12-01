package com.bayraktar.scrum.ui.project.detail.main;

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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static com.bayraktar.scrum.App.firebaseDatabase;

public class ProjectDetailMainViewModel extends ViewModel {
    private MutableLiveData<List<String>> mutableLiveData;
    private DatabaseReference projectsRef = firebaseDatabase.getReference("PROJECTS");

    public ProjectDetailMainViewModel() {
        mutableLiveData = new MutableLiveData<>();
    }

    public LiveData<List<String>> getTasks(final int status, String projectID) {
        final List<String> values = new ArrayList<>();
        projectsRef.child(projectID).child("taskList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HashMap<String, Integer> map = (HashMap<String, Integer>) snapshot.getValue();
                    if (map != null && map.size() > 0)
                        for (String key : map.keySet()) {
                            if (key != null && !key.equals("")) {
                                if (Integer.valueOf(String.valueOf(map.get(key))) == status) {
                                    values.add(key);
                                }
                            }
                        }
                }
                mutableLiveData.setValue(values);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mutableLiveData.setValue(null);
            }
        });
        return mutableLiveData;
    }
}