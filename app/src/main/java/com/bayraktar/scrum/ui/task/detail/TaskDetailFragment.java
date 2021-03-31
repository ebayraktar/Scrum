package com.bayraktar.scrum.ui.task.detail;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bayraktar.scrum.BaseActivity;
import com.bayraktar.scrum.R;
import com.bayraktar.scrum.adapter.TaskCommentAdapter;
import com.bayraktar.scrum.adapter.TaskHistoryAdapter;
import com.bayraktar.scrum.model.Project;
import com.bayraktar.scrum.model.Task;
import com.bayraktar.scrum.model.TaskComment;
import com.bayraktar.scrum.model.TaskHistory;
import com.bayraktar.scrum.model.User;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.bayraktar.scrum.App.currentUser;
import static com.bayraktar.scrum.App.firebaseService;

public class TaskDetailFragment extends Fragment implements View.OnClickListener, TaskCommentAdapter.OnCommentListener, AdapterView.OnItemSelectedListener {

    View viewStatus;
    TextView tvStatus;
    TextView tvDescription;
    TextView tvStartDate;
    TextView tvDeadline;
    TextView tvConstituent;
    Spinner spPriority;
    Spinner spStatus;

    RecyclerView rvTaskHistory;
    TextView tvTaskHistory;
    RecyclerView rvTaskComment;
    TextView tvTaskComment;

    TextInputLayout tilComment;
    TextInputEditText tietComment;
    ImageView ivAttachFile;
    TextView tvAttachFile;
    CardView cvSend;

    Task currentTask;

    TaskCommentAdapter taskCommentAdapter;
    TaskHistoryAdapter taskHistoryAdapter;

    private static final String TASK_ID = "taskID";
    private static final String PROJECT_ID = "projectID";
    private String taskID;
    private String projectID;

    public TaskDetailFragment() {
    }

    public static TaskDetailFragment newInstance(String taskID, String projectID) {

        TaskDetailFragment fragment = new TaskDetailFragment();
        Bundle args = new Bundle();
        args.putString(TASK_ID, taskID);
        args.putString(PROJECT_ID, projectID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            taskID = getArguments().getString(TASK_ID);
            projectID = getArguments().getString(PROJECT_ID);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_detail, container, false);
        viewStatus = view.findViewById(R.id.viewStatus);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvStartDate = view.findViewById(R.id.tvStartDate);
        tvDeadline = view.findViewById(R.id.tvDeadline);
        tvConstituent = view.findViewById(R.id.tvConstituent);
        spPriority = view.findViewById(R.id.spPriority);
        spStatus = view.findViewById(R.id.spStatus);

        tilComment = view.findViewById(R.id.tilComment);
        tietComment = view.findViewById(R.id.tietComment);
        ivAttachFile = view.findViewById(R.id.ivAttachFile);
        tvAttachFile = view.findViewById(R.id.tvAttachFile);
        cvSend = view.findViewById(R.id.cvSend);

        cvSend.setOnClickListener(this);

        tvTaskHistory = view.findViewById(R.id.tvTaskHistory);
        rvTaskHistory = view.findViewById(R.id.rvTaskHistory);
        taskHistoryAdapter = new TaskHistoryAdapter(getContext());
        rvTaskHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTaskHistory.setAdapter(taskHistoryAdapter);

        tvTaskComment = view.findViewById(R.id.tvTaskComment);
        rvTaskComment = view.findViewById(R.id.rvTaskComment);
        taskCommentAdapter = new TaskCommentAdapter(getContext(), this);
        rvTaskComment.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTaskComment.setAdapter(taskCommentAdapter);

        List<String> priorityList = new ArrayList<>();
        priorityList.add("Normal");
        priorityList.add("Düşük");
        priorityList.add("Yüksek");
        SpinnerAdapter priorityAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, priorityList);
        spPriority.setAdapter(priorityAdapter);
        spPriority.setOnItemSelectedListener(this);


        List<String> statusList = new ArrayList<>();
        statusList.add("Bekliyor");
        statusList.add("Aktif");
        statusList.add("Tamamlandı");
        statusList.add("Askıda");
        SpinnerAdapter statusAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, statusList);
        spStatus.setAdapter(statusAdapter);
        spStatus.setOnItemSelectedListener(this);

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TaskDetailViewModel mViewModel = new ViewModelProvider(this).get(TaskDetailViewModel.class);
        mViewModel.getTask(projectID, taskID).observe(getViewLifecycleOwner(), new Observer<Task>() {
            @Override
            public void onChanged(Task task) {
                setTask(task);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.task_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Uyarı")
                    .setMessage("Görevi silmek istediğinize emin misiniz?")
                    .setIcon(R.drawable.ic_info)
                    .setPositiveButton("SİL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            firebaseService.deleteTask(projectID, taskID);
                            firebaseService.getProject(projectID, new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        Project project = snapshot.getValue(Project.class);
                                        if (project != null) {
                                            Bundle bundle = new Bundle();
                                            bundle.putString("projectID", projectID);
                                            bundle.putString("title", project.getProjectName());
                                            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_nav_task_detail_to_nav_project_detail, bundle);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    })
                    .setNegativeButton("İPTAL", null)
                    .create().show();
            return true;
        } else if (itemId == R.id.menu_edit) {
            editTask();
            //Toast.makeText(getContext(), "Edit Fragment", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void editTask() {
        Bundle bundle = new Bundle();
        bundle.putString("projectID", projectID);
        bundle.putString("taskID", taskID);
        bundle.putString("title", currentTask.getTaskName() + " Düzenle");
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_nav_task_detail_to_nav_new_task, bundle);
    }

    int statusColor;

    void setTask(Task task) {
        currentTask = task;
        if (currentTask == null)
            return;

        //int statusColor;
        int statusText;
        switch (currentTask.getTaskStatusID()) {
            case 1:
                statusColor = R.color.colorGreen;
                statusText = R.string.menu_active;
                break;
            case 2:
                statusColor = R.color.colorDarkGrey;
                statusText = R.string.menu_completed;
                break;
            case 3:
                statusColor = R.color.colorBlue;
                statusText = R.string.menu_suspended;
                break;
            default:
                statusColor = R.color.colorOrange;
                statusText = R.string.menu_pending;
                break;
        }

        ((BaseActivity) getActivity()).setActionBarColor(statusColor);

        if (currentTask.getTaskHistoryList() != null) {
            tvTaskHistory.setVisibility(View.GONE);
            rvTaskHistory.setVisibility(View.VISIBLE);
            setTaskHistories(currentTask.getTaskHistoryList());
        } else {
            tvTaskHistory.setVisibility(View.VISIBLE);
            rvTaskHistory.setVisibility(View.GONE);
        }

        if (currentTask.getTaskCommentList() != null) {
            tvTaskComment.setVisibility(View.GONE);
            rvTaskComment.setVisibility(View.VISIBLE);
            setTaskComments(currentTask.getTaskCommentList());
        } else {
            tvTaskComment.setVisibility(View.VISIBLE);
            rvTaskComment.setVisibility(View.GONE);
        }

        viewStatus.setBackgroundResource(statusColor);
        tvStatus.setText(statusText);

        tvDescription.setText(currentTask.getTaskDescription());
        if (TextUtils.isEmpty(currentTask.getTaskDescription())) {
            tvDescription.setText("Görev açıklaması bulunamadı");
            tvDescription.setTypeface(tvDescription.getTypeface(), Typeface.ITALIC);
        }
        tvStartDate.setText(DateFormat.getMediumDateFormat(getContext()).format(currentTask.getStartDate()));
        tvDeadline.setText(DateFormat.getMediumDateFormat(getContext()).format(currentTask.getDeadline()));

        firebaseService.getUser(currentTask.getConstituent(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        tvConstituent.setText(user.getName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        switch (currentTask.getPriorityID()) {
            case 0:
                spPriority.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorDarkGrey));
                break;
            case 1:
                spPriority.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
                break;
            case 2:
                spPriority.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorRed));
                break;
        }
        spPriority.setSelection(currentTask.getPriorityID());
        spStatus.setSelection(currentTask.getTaskStatusID());
    }

    private void setTaskComments(HashMap<String, Boolean> taskCommentList) {
        if (taskCommentList != null) {
            List<String> commentKeys = new ArrayList<>(taskCommentList.keySet());
            taskCommentAdapter.setTaskCommentModelList(commentKeys);
        }
    }

    private void setTaskHistories(HashMap<String, Boolean> taskHistoryList) {
        if (taskHistoryList != null) {
            List<String> historyKeys = new ArrayList<>(taskHistoryList.keySet());
            taskHistoryAdapter.setTaskHistoryList(historyKeys);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ((BaseActivity) getActivity()).setActionBarColor(R.color.colorPrimary);
    }

    void sendComment() {

        TaskComment comment = new TaskComment();
        comment.setComment(tietComment.getText().toString());
        comment.setCommentDate(Calendar.getInstance().getTime());
        comment.setConstituent(currentUser.getUserID());
        String commentID = firebaseService.setTaskComment(taskID, comment);
        comment.setCommentID(commentID);


        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getActivity().getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(getActivity());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        tietComment.setText("");
        HashMap<String, Boolean> comments = currentTask.getTaskCommentList();
        if (comments == null) {
            comments = new HashMap<>();
        }
        comments.put(commentID, true);
        currentTask.setTaskCommentList(comments);
        setTask(currentTask);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.cvSend) {
            if (tietComment.getText().toString().trim().equals("")) {
                tilComment.setError("Boş olamaz!");
            } else {
                sendComment();
            }
        }
    }

    @Override
    public void onAttachmentClick(int position) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int parentId = parent.getId();
        if (parentId == R.id.spStatus) {
            if (currentTask != null && currentTask.getTaskStatusID() != position) {
                Date currentDate = Calendar.getInstance().getTime();
                TaskHistory historyModel = new TaskHistory();
                historyModel.setChangeDate(currentDate);
                historyModel.setOldTaskStatus(currentTask.getTaskStatusID());
                historyModel.setCurrentTaskStatus(position);
                historyModel.setConstituent(currentUser.getUserID());
                String historyID = firebaseService.setTaskHistory(taskID, historyModel);
                historyModel.setHistoryID(historyID);
                firebaseService.updateProjectTaskStatus(projectID, taskID, position);
                HashMap<String, Boolean> historyModelList = currentTask.getTaskHistoryList();
                if (historyModelList == null) {
                    historyModelList = new HashMap<>();
                }
                historyModelList.put(historyID, true);
                currentTask.setTaskStatusID(position);
                currentTask.setTaskHistoryList(historyModelList);
                setTask(currentTask);
            }
        } else if (parentId == R.id.spPriority) {
            if (currentTask != null && currentTask.getPriorityID() != position) {
                currentTask.setPriorityID(position);
                firebaseService.updateTask(taskID, currentTask, null);
                setTask(currentTask);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Snackbar.make(parent.getSelectedView(), "Seçim yapınız", BaseTransientBottomBar.LENGTH_SHORT).show();
    }
}