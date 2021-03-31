package com.bayraktar.scrum.service;

import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bayraktar.scrum.model.Application;
import com.bayraktar.scrum.model.Invitation;
import com.bayraktar.scrum.model.Member;
import com.bayraktar.scrum.model.Project;
import com.bayraktar.scrum.model.Task;
import com.bayraktar.scrum.model.TaskComment;
import com.bayraktar.scrum.model.TaskHistory;
import com.bayraktar.scrum.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

import static com.bayraktar.scrum.App.firebaseDatabase;

public class FirebaseService {

    private static final String TASK_LIST = "taskList";
    private static final String TASK_COMMENT_LIST = "taskCommentList";
    private static final String TASK_HISTORY_LIST = "taskHistoryList";
    private static final String INVITATION_LIST = "invitations";
    private static final String APPLICATION_LIST = "applications";
    private static final String MEMBER_LIST = "members";

    DatabaseReference usersRef = firebaseDatabase.getReference("USERS");
    DatabaseReference projectsRef = firebaseDatabase.getReference("PROJECTS");
    DatabaseReference tasksRef = firebaseDatabase.getReference("TASKS");
    DatabaseReference taskHistoriesRef = firebaseDatabase.getReference("taskHistories");
    DatabaseReference taskCommentsRef = firebaseDatabase.getReference("taskComments");
    private final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();


    private final MutableLiveData<String> currentUserName;

    public FirebaseService() {
        currentUserName = new MutableLiveData<>();
    }

    public LiveData<String> getConstituent(String id) {
        usersRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        currentUserName.setValue(user.getName());
                    }
                } else {
                    currentUserName.setValue("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                currentUserName.setValue("Kullanıcı Bulunamadı");
            }
        });
        return currentUserName;
    }

    public void uploadUserImage(String userID, byte[] bytes, OnSuccessListener<UploadTask.TaskSnapshot> listener) {
        if (userID != null && !userID.equals("") && bytes != null && bytes.length > 0) {
            mStorageRef.child("users").child(userID + ".jpg").putBytes(bytes).addOnSuccessListener(listener);
        }
    }

    public void uploadProjectImage(String projectID, byte[] bytes, OnSuccessListener<UploadTask.TaskSnapshot> listener) {
        if (projectID != null && !projectID.equals("") && bytes != null && bytes.length > 0) {
            mStorageRef.child("projects").child(projectID + ".jpg").putBytes(bytes).addOnSuccessListener(listener);
        }
    }

    public void getUser(String userID, ValueEventListener valueEventListener) {
        usersRef.child(userID).addListenerForSingleValueEvent(valueEventListener);
    }

    public void setUser(User user) {
        if (user.getUserID().equals("")) {
            return;
        }
        usersRef.child(user.getUserID()).setValue(user);
    }

    public void getTask(String taskID, ValueEventListener listener) {
        tasksRef.child(taskID).addListenerForSingleValueEvent(listener);
    }

    public String setTaskHistory(final String taskID, final TaskHistory taskHistory) {
        if (taskID != null && !taskID.equals("") && taskHistory != null) {
            String taskKey = taskHistoriesRef.push().getKey();
            if (taskKey != null && !taskKey.equals("")) {
                taskHistoriesRef.child(taskKey).setValue(taskHistory);
                tasksRef.child(taskID).child(TASK_HISTORY_LIST).child(taskKey).setValue(true);
            }
            return taskKey;
        }
        return "";
    }

    public void getTaskHistory(String taskHistoryID, ValueEventListener listener) {
        if (taskHistoryID != null && !taskHistoryID.equals("")) {
            taskHistoriesRef.child(taskHistoryID).addListenerForSingleValueEvent(listener);

        }
    }

    public void getApplications(String projectID, ValueEventListener listener) {
        projectsRef.child(projectID).child(APPLICATION_LIST).addListenerForSingleValueEvent(listener);
    }

    public void updateApplication(final String projectID, final HashMap<String, Object> application) {
        if (projectID != null && !projectID.equals("") && application != null) {
            projectsRef.child(projectID).child(APPLICATION_LIST).updateChildren(application);
        }
    }

    public void deleteApplication(final String projectID, final String userID) {
        if (projectID != null && !projectID.equals("") && userID != null && !userID.equals("")) {
            projectsRef.child(projectID).child(APPLICATION_LIST).child(userID).removeValue();
        }
    }

    public String setTaskComment(final String taskID, final TaskComment taskComment) {
        if (taskID != null && !taskID.equals("") && taskComment != null) {
            String commentKey = taskCommentsRef.push().getKey();
            if (commentKey != null && !commentKey.equals("")) {
                taskCommentsRef.child(commentKey).setValue(taskComment);
                tasksRef.child(taskID).child(TASK_COMMENT_LIST).child(commentKey).setValue(true);
            }
            return commentKey;
        }
        return "";
    }

    public void getComment(String commentID, ValueEventListener listener) {
        if (commentID != null && !commentID.equals("")) {
            taskCommentsRef.child(commentID).addListenerForSingleValueEvent(listener);
        }
    }

    public void insertTask(final String projectID, final Task task, OnCompleteListener<Void> listener) {
        if (projectID != null && !projectID.equals("") && task != null) {
            if (task.getTaskID() == null || task.getTaskID().equals("")) {
                String key = tasksRef.push().getKey();
                task.setTaskID(key);
            }
            HashMap<String, Object> taskHasMap = new HashMap<>();
            taskHasMap.put(task.getTaskID(), task);
            tasksRef.updateChildren(taskHasMap);
            projectsRef.child(projectID).child(TASK_LIST).push().getKey();
            projectsRef.child(projectID).child(TASK_LIST).child(task.getTaskID()).setValue(task.getTaskStatusID()).addOnCompleteListener(listener);
        }
    }

    public void updateTask(final String taskID, final Task task, OnCompleteListener<Void> listener) {
        if (taskID != null && !taskID.equals("") && task != null) {
            HashMap<String, Object> taskHasMap = new HashMap<>();
            taskHasMap.put(task.getTaskID(), task);
            tasksRef.updateChildren(taskHasMap);
        }
    }

    public void deleteTask(String projectID, String taskID) {
        projectsRef.child(projectID).child(TASK_LIST).child(taskID).removeValue();
        tasksRef.child(taskID).removeValue();
    }

    public void getProject(String projectID, ValueEventListener listener) {
        projectsRef.child(projectID).addListenerForSingleValueEvent(listener);
    }

    public void insertProject(Project project) {
        if (project.getProjectID() == null || project.getProjectID().equals("")) {
            String id = projectsRef.push().getKey();
            if (id != null) {
                project.setProjectID(id);
            }
        }
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(project.getProjectID(), project);
        projectsRef.updateChildren(hashMap);
    }

    public void updateProjectTaskStatus(String projectID, String taskID, int status) {
        if (!TextUtils.isEmpty(projectID) && !TextUtils.isEmpty(taskID)) {
            projectsRef.child(projectID).child(TASK_LIST).child(taskID).setValue(status);
            tasksRef.child(taskID).child("taskStatusID").setValue(status);
        }
    }


    public void insertMember(String projectID, String memberID) {
        if (projectID != null && !projectID.equals("") && memberID != null && !memberID.equals("")) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(memberID, true);
            projectsRef.child(projectID).child(MEMBER_LIST).updateChildren(hashMap);
        }
    }
//
//    public void deleteMember(String projectID, String userID) {
//        if (projectID != null && !projectID.equals("") && userID != null && !userID.equals("")) {
//            projectsRef.child(projectID).child(MEMBER_LIST).child(userID).removeValue();
//        }
//    }

    public void deleteProject(String projectID) {
        projectsRef.child(projectID).removeValue();
    }

    public String getPriority(int priorityID) {
        switch (priorityID) {
            case 1:
                return "Düşük";
            case 2:
                return "Yüksek";
            default:
                return "Normal";
        }
    }
}
