package com.bayraktar.scrum.ui.project.applications;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bayraktar.scrum.R;
import com.bayraktar.scrum.adapter.ApplicationAdapter;
import com.bayraktar.scrum.model.Application;
import com.bayraktar.scrum.model.Project;
import com.bayraktar.scrum.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bayraktar.scrum.App.firebaseService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProjectApplicationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProjectApplicationsFragment extends Fragment implements ApplicationAdapter.OnApplicationListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PROJECT_ID = "projectID";

    // TODO: Rename and change types of parameters
    private String projectID;

    ProjectApplicationsViewModel mViewModel;

    LottieAnimationView av_splash_animation;
    TextView tvNoApplication;
    RecyclerView rvApplications;
    ApplicationAdapter applicationAdapter;
    List<Application> applicationList;
    Project currentProject;

    public ProjectApplicationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param projectID Parameter 1.
     * @return A new instance of fragment ProjectApplicationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProjectApplicationsFragment newInstance(String projectID) {
        ProjectApplicationsFragment fragment = new ProjectApplicationsFragment();
        Bundle args = new Bundle();
        args.putString(PROJECT_ID, projectID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            projectID = getArguments().getString(PROJECT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_project_applications, container, false);
        mViewModel = new ViewModelProvider(this).get(ProjectApplicationsViewModel.class);
        av_splash_animation = view.findViewById(R.id.av_splash_animation);
        tvNoApplication = view.findViewById(R.id.tvNoApplication);
        rvApplications = view.findViewById(R.id.rvApplications);
        applicationAdapter = new ApplicationAdapter(this);
        rvApplications.setLayoutManager(new LinearLayoutManager(getContext()));
        rvApplications.setAdapter(applicationAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.getProject(projectID).observe(getViewLifecycleOwner(), new Observer<Project>() {
            @Override
            public void onChanged(Project project) {
                currentProject = project;
                List<Application> tempList = new ArrayList<>();
                if (project.getApplications() != null) {
                    for (Map.Entry<String, Integer> values : project.getApplications().entrySet()) {
                        Application tempApplication = new Application();
                        tempApplication.setInvitationStatus(Integer.parseInt(String.valueOf(values.getValue())));//java.lang.ClassCastException: java.lang.Long cannot be cast to java.lang.Integer
                        tempApplication.setAppliedUserID(values.getKey());
                        if (tempApplication.getInvitationStatus() == 0)
                            tempList.add(tempApplication);
                    }
                }
                setApplications(tempList);
            }
        });
    }

    void setApplications(List<Application> applications) {
        isLoadSuccess(applications != null && applications.size() > 0);
        if (applications != null) {
            applicationList = applications;
            applicationAdapter.setApplicationPermissionList(applications);
        }
    }

    void isLoadSuccess(boolean isSuccess) {
        av_splash_animation.setVisibility(View.GONE);
        if (isSuccess) {
            rvApplications.setVisibility(View.VISIBLE);
            tvNoApplication.setVisibility(View.GONE);
        } else {
            rvApplications.setVisibility(View.GONE);
            tvNoApplication.setVisibility(View.VISIBLE);
        }
    }

    void showUserDetail(User user) {
        Toast.makeText(getContext(), user.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onApplicationClick(int position) {
        Application application = applicationList.get(position);
        firebaseService.getUser(application.getAppliedUserID(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        showUserDetail(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onAnswerClick(int position, boolean isAccept) {
        if (currentProject != null) {
            Application application = applicationList.get(position);
            HashMap<String, Object> apl = new HashMap<>();
            int value;
            if (isAccept) {
                value = 1;
                firebaseService.insertMember(projectID, application.getAppliedUserID());
            } else {
                value = 2;
            }
            apl.put(application.getAppliedUserID(), value);
            application.setInvitationStatus(value);
            firebaseService.updateApplication(projectID, apl);
            applicationAdapter.notifyItemChanged(position);
        }
    }
}