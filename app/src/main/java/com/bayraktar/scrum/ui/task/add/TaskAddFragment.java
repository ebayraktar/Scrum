package com.bayraktar.scrum.ui.task.add;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bayraktar.scrum.BaseActivity;
import com.bayraktar.scrum.R;
import com.bayraktar.scrum.adapter.AttendantAdapter;
import com.bayraktar.scrum.model.Project;
import com.bayraktar.scrum.model.Task;
import com.bayraktar.scrum.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.bayraktar.scrum.App.currentUser;
import static com.bayraktar.scrum.App.firebaseService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskAddFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PROJECT_ID = "projectID";
    private static final String TASK_ID = "taskID";
    TextInputEditText tietTaskName, tietTaskDescription, tietTaskStartDate, tietTaskDeadline;
    TextInputLayout tilTaskName, tilTaskDescription, tilTaskStartDate, tilTaskDeadline;
    Spinner spStatus, spPriority;
    final Calendar startDateCalendar = Calendar.getInstance();
    final Calendar deadlineCalendar = Calendar.getInstance();
    final String myFormat = "dd/MM/yyyy"; //In which you need put here

    // TODO: Rename and change types of parameters
    private String projectID;
    private String taskID;

    private TaskAddViewModel mViewModel;
    Project currentProject;
    Task currentTask;
    HashMap<String, Boolean> attendantList;

    boolean isNewTask;

    public TaskAddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param projectID Parameter 1.
     * @return A new instance of fragment AddTaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskAddFragment newInstance(String projectID, String taskID) {
        TaskAddFragment fragment = new TaskAddFragment();
        Bundle args = new Bundle();
        args.putString(PROJECT_ID, projectID);
        args.putString(TASK_ID, taskID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            projectID = getArguments().getString(PROJECT_ID);
            taskID = getArguments().getString(TASK_ID);
            isNewTask = taskID == null || taskID.equals("");
        }
        attendantList = new HashMap<>();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_add, container, false);
        mViewModel = new ViewModelProvider(this).get(TaskAddViewModel.class);
        tietTaskName = view.findViewById(R.id.tietTaskName);
        tietTaskDescription = view.findViewById(R.id.tietTaskDescription);
        tietTaskStartDate = view.findViewById(R.id.tietTaskStartDate);
        tietTaskDeadline = view.findViewById(R.id.tietTaskDeadline);

        tilTaskName = view.findViewById(R.id.tilTaskName);
        tilTaskDescription = view.findViewById(R.id.tilTaskDescription);
        tilTaskStartDate = view.findViewById(R.id.tilTaskStartDate);
        tilTaskDeadline = view.findViewById(R.id.tilTaskDeadline);

        spStatus = view.findViewById(R.id.spStatus);
        spPriority = view.findViewById(R.id.spPriority);

        view.findViewById(R.id.cvAttendant).setOnClickListener(this);
        updateLabel(tietTaskStartDate, startDateCalendar);
        updateLabel(tietTaskDeadline, deadlineCalendar);
        tietTaskStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                //Find the currently focused view, so we can grab the correct window token from it.
                View view = getActivity().getCurrentFocus();
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (view == null) {
                    view = new View(getActivity());
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), startDate, startDateCalendar
                        .get(Calendar.YEAR), startDateCalendar.get(Calendar.MONTH),
                        startDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        tietTaskDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                //Find the currently focused view, so we can grab the correct window token from it.
                View view = getActivity().getCurrentFocus();
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (view == null) {
                    view = new View(getActivity());
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), deadline, deadlineCalendar
                        .get(Calendar.YEAR), deadlineCalendar.get(Calendar.MONTH),
                        deadlineCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.getProject(projectID).observe(getViewLifecycleOwner(), new Observer<Project>() {
            @Override
            public void onChanged(Project project) {
                currentProject = project;
            }
        });
        if (taskID != null) {
            initializeTask();
        }
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.save_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_save) {
            if (isFormValid()) {
//                if (isNewTask) {
                saveTask();
//                } else {
//                    updateTask();
//                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void initializeTask() {
        firebaseService.getTask(taskID, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Task task = snapshot.getValue(Task.class);
                    if (task != null) {
                        setTask(task);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void setTask(Task task) {
        currentTask = task;
        getActivity().setTitle(task.getTaskName() + " Düzenle");
        tietTaskName.setText(task.getTaskName());
        tietTaskDescription.setText(task.getTaskDescription());
        startDateCalendar.setTime(task.getStartDate());
        deadlineCalendar.setTime(task.getDeadline());
        updateLabel(tietTaskStartDate, startDateCalendar);
        updateLabel(tietTaskDeadline, deadlineCalendar);
        spStatus.setSelection(task.getTaskStatusID());
        spPriority.setSelection(task.getPriorityID());
        attendantList.clear();
        if (task.getAttendants() != null) {
            attendantList.putAll(task.getAttendants());
        }
    }

    void showAttendants(List<String> userIDList) {

        final List<Boolean> selectedUsers = new ArrayList<>();

        for (int i = 0; i < userIDList.size(); i++) {
            String userID = userIDList.get(i);
            if (attendantList != null && attendantList.containsKey(userID)) {
                selectedUsers.add(true);
            } else {
                selectedUsers.add(false);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View view = getLayoutInflater().inflate(R.layout.dialog_attendants, null, false);
        RecyclerView rvAttendants = view.findViewById(R.id.rvAttendants);
        final AttendantAdapter attendantAdapter = new AttendantAdapter(new AttendantAdapter.OnAttendantListener() {
            @Override
            public void onCheckedListener(int position, boolean isChecked) {
                selectedUsers.set(position, isChecked);
            }
        });

        attendantAdapter.setUserIDList(userIDList);
        attendantAdapter.setSelectedUsers(selectedUsers);
        rvAttendants.setLayoutManager(new

                LinearLayoutManager(getContext()));
        rvAttendants.setAdapter(attendantAdapter);

        AlertDialog dialog = builder.setTitle("Görevli Seçiniz")
                .setView(view)
                .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        attendantList.clear();
                        for (int i = 0; i < selectedUsers.size(); i++) {
                            if (selectedUsers.get(i)) {
                                User user = attendantAdapter.getUser(i);
                                attendantList.put(user.getUserID(), true);
                            }
                        }
                    }
                })
                .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();

    }

    DatePickerDialog.OnDateSetListener startDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            startDateCalendar.set(Calendar.YEAR, year);
            startDateCalendar.set(Calendar.MONTH, monthOfYear);
            startDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(tietTaskStartDate, startDateCalendar);
        }
    };

    DatePickerDialog.OnDateSetListener deadline = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            deadlineCalendar.set(Calendar.YEAR, year);
            deadlineCalendar.set(Calendar.MONTH, monthOfYear);
            deadlineCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(tietTaskDeadline, deadlineCalendar);
        }
    };

    void updateLabel(TextInputEditText textInputEditText, Calendar myCalendar) {
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        textInputEditText.setText(sdf.format(myCalendar.getTime()));
    }

    boolean isFormValid() {
        boolean validate = true;
        if (tietTaskName.getText().toString().trim().equals("")) {
            tilTaskName.setError("Boş olamaz");
            validate = false;
        } else {
            tilTaskName.setError("");
        }

        return validate;
    }

    void saveTask() {
        showLoading();
        SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date _startDate = formatter1.parse(tietTaskStartDate.getText().toString());
            Date _deadline = formatter1.parse(tietTaskDeadline.getText().toString());

            if (currentTask == null) {
                currentTask = new Task();
                currentTask.setConstituent(currentUser.getUserID());
                currentTask.setProjectID(projectID);
            }
            currentTask.setTaskName(tietTaskName.getText().toString());
            currentTask.setTaskDescription(tietTaskDescription.getText().toString());
            currentTask.setStartDate(_startDate);
            currentTask.setDeadline(_deadline);
            currentTask.setAttendants(attendantList);
            currentTask.setPriorityID(spPriority.getSelectedItemPosition());
            currentTask.setTaskStatusID(spStatus.getSelectedItemPosition());

            firebaseService.insertTask(projectID, currentTask, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task task) {
                    hideLoading();
                    if (task.isSuccessful()) {
                        if (isNewTask) {
                            success(projectID, null, "");
                        } else {
                            success(projectID, null, currentTask.getTaskName());
                        }
                    } else {
                        String message = "Görev oluşturulurken hata oluştu";
                        if (task.getException() != null) {
                            message += ": " + task.getException().getMessage();
                        }
                        error("HATA", message, R.drawable.ic_info);
                    }
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("TAG", "saveTask: " + e.getMessage());
        }
    }

//    void updateTask() {
//        showLoading();
//        SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
//        boolean isValid = false;
//        try {
//            Date _startDate = formatter1.parse(tietTaskStartDate.getText().toString());
//            Date _deadline = formatter1.parse(tietTaskDeadline.getText().toString());
//
//            if (currentTask != null) {
//                isValid = true;
//                currentTask.setDeadline(_deadline);
//                currentTask.setStartDate(_startDate);
//                currentTask.setTaskName(tietTaskName.getText().toString());
//                currentTask.setTaskDescription(tietTaskDescription.getText().toString());
//                currentTask.setPriorityID(spPriority.getSelectedItemPosition());
//                currentTask.setTaskStatusID(spStatus.getSelectedItemPosition());
//                currentTask.setAttendants(attendantList);
//                firebaseService.updateTask(projectID, currentTask.getTaskID(), currentTask, new OnCompleteListener() {
//                    @Override
//                    public void onComplete(@NonNull final com.google.android.gms.tasks.Task task) {
//                        hideLoading();
//                        if (task.isSuccessful()) {
//                            success(projectID, currentTask.getTaskID(), currentTask.getTaskName());
//                        } else {
//                            String message = "Görev oluşturulurken hata oluştu";
//                            if (task.getException() != null) {
//                                message += ": " + task.getException().getMessage();
//                            }
//                            error("HATA", message, R.drawable.ic_info);
//                        }
//
//                    }
//                });
//
//            }
//            if (!isValid)
//                hideLoading();
//        } catch (ParseException e) {
//            e.printStackTrace();
//            error("HATA", e.getMessage(), R.drawable.ic_info);
//        }
//    }

    void success(String projectID, String taskID, String title) {
        Bundle bundle = new Bundle();
        bundle.putString("projectID", projectID);
        bundle.putString("taskID", taskID);
        bundle.putString("title", title);
        if (taskID != null && !taskID.equals("")) {
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_nav_new_task_to_nav_task_detail, bundle);
        } else {
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_nav_new_task_to_nav_project_detail, bundle);
        }
    }

    void error(String title, String message, int icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title)
                .setMessage(message)
                .setIcon(icon)
                .setPositiveButton("TAMAM", null)
                .create().show();
    }

    void showLoading() {
        if (isNewTask) {
            BaseActivity.showLoading(getActivity(), "Görev oluşturuluyor...");
        } else {
            BaseActivity.showLoading(getActivity(), "Görev güncelleniyor...");
        }
    }

    void hideLoading() {
        BaseActivity.hideLoading();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cvAttendant) {
            List<String> userIDList = new ArrayList<>();
            userIDList.add(currentProject.getConstituentID());
            if (currentProject.getMembers() != null) {
                userIDList.addAll(currentProject.getMembers().keySet());
            }
            showAttendants(userIDList);
        }
    }
}