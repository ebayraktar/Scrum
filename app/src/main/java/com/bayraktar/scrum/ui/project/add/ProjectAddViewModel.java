package com.bayraktar.scrum.ui.project.add;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bayraktar.scrum.model.MobileResult;
import com.bayraktar.scrum.model.Project;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static com.bayraktar.scrum.App.firebaseDatabase;
import static com.bayraktar.scrum.App.firebaseService;

public class ProjectAddViewModel extends ViewModel {

    private MutableLiveData<MobileResult> resultMutableLiveData;
    private DatabaseReference usersRef = firebaseDatabase.getReference("USERS");

    public ProjectAddViewModel() {
        resultMutableLiveData = new MutableLiveData<>();
    }

    public LiveData<MobileResult> insertProject(final Project project, List<String> emails) {
        final MobileResult result = new MobileResult();
        firebaseService.insertProject(project);
        resultMutableLiveData.setValue(result);
        return resultMutableLiveData;
    }

    public LiveData<MobileResult> updateProject(final Project project, List<String> emails) {
        final MobileResult result = new MobileResult();
        firebaseService.insertProject(project);
        resultMutableLiveData.setValue(result);
        return resultMutableLiveData;
    }

    private void getUser(String email, ValueEventListener listener) {
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(listener);
    }

}
