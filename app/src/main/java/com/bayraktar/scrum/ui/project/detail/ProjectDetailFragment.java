package com.bayraktar.scrum.ui.project.detail;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bayraktar.scrum.R;
import com.bayraktar.scrum.model.Project;
import com.bayraktar.scrum.ui.project.detail.main.ProjectDetailMainFragment;
import com.bayraktar.scrum.view.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import static com.bayraktar.scrum.App.currentUser;
import static com.bayraktar.scrum.App.firebaseService;

public class ProjectDetailFragment extends Fragment implements View.OnClickListener {

    private ProjectDetailViewModel mViewModel;
    FloatingActionButton fab;
    BottomNavigationView navView;
    Fragment currentFragment;
    Project currentProject;

    private static final String PROJECT_ID = "projectID";
    private String projectID;

    public ProjectDetailFragment() {
    }

    public static ProjectDetailFragment newInstance(String projectID) {

        ProjectDetailFragment fragment = new ProjectDetailFragment();
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
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.project_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (currentProject == null) {
            return super.onOptionsItemSelected(item);
        }
        Bundle bundle = new Bundle();
        int itemId = item.getItemId();
        if (itemId == R.id.menu_delete) {
            if (currentProject.getConstituentID().equals(currentUser.getUserID())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Uyarı")
                        .setMessage("Projeyi silmek istediğinize emin misiniz?")
                        .setIcon(R.drawable.ic_info)
                        .setPositiveButton("SİL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                firebaseService.deleteProject(projectID);
                                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_nav_project_detail_to_nav_projects);
                            }
                        })
                        .setNegativeButton("İPTAL", null)
                        .create().show();
            } else {
                Snackbar.make(navView, "Bu işlemi yapabilmek için yetkiniz yok", BaseTransientBottomBar.LENGTH_LONG).show();
            }
            return true;
        } else if (itemId == R.id.menu_edit) {
            if (currentProject.getConstituentID().equals(currentUser.getUserID())) {
                bundle.putString("projectID", currentProject.getProjectID());
                bundle.putString("title", currentProject.getProjectName() + " Düzenle");
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_nav_project_detail_to_nav_new_project, bundle);
            } else {
                Snackbar.make(navView, "Bu işlemi yapabilmek için yetkiniz yok", BaseTransientBottomBar.LENGTH_LONG).show();
            }
            return true;
        } else if (itemId == R.id.menu_applications) {
            if (currentProject.getConstituentID().equals(currentUser.getUserID())) {
                bundle.putString("projectID", currentProject.getProjectID());
                bundle.putString("title", currentProject.getProjectName() + " Başvurular");
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_nav_project_detail_to_nav_project_applications, bundle);
            } else {
                Snackbar.make(navView, "Bu işlemi yapabilmek için yetkiniz yok", BaseTransientBottomBar.LENGTH_LONG).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment;
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_pending) {
                selectedFragment = ProjectDetailMainFragment.newInstance(0, projectID);
            } else if (itemId == R.id.navigation_active) {
                selectedFragment = ProjectDetailMainFragment.newInstance(1, projectID);
            } else if (itemId == R.id.navigation_completed) {
                selectedFragment = ProjectDetailMainFragment.newInstance(2, projectID);
            } else if (itemId == R.id.navigation_suspended) {
                selectedFragment = ProjectDetailMainFragment.newInstance(3, projectID);
            } else {
                return false;
            }
            if (currentFragment == selectedFragment) {
                return false;
            }
            currentFragment = selectedFragment;
            getChildFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.fade_out, android.R.anim.slide_in_left, android.R.anim.fade_out)
                    .replace(R.id.bottom_nav_host_fragment, currentFragment).commit();
            return true;
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(ProjectDetailViewModel.class);
        View view = inflater.inflate(R.layout.fragment_project_detail, container, false);
        fab = view.findViewById(R.id.fab);
        navView = view.findViewById(R.id.bottom_nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel.getProject(projectID).observe(getViewLifecycleOwner(), new Observer<Project>() {
            @Override
            public void onChanged(Project project) {
                currentProject = project;
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(project.getProjectName());
            }
        });
        fab.setOnClickListener(this);
        if (savedInstanceState == null) {
            mOnNavigationItemSelectedListener.onNavigationItemSelected(navView.getMenu().getItem(0));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) getActivity()).getSupportActionBar().setSubtitle("");
    }

    @Override
    public void onClick(View v) {
        if (currentProject == null)
            return;
        Bundle bundle = new Bundle();
        bundle.putString("projectID", currentProject.getProjectID());
        bundle.putString("title", currentProject.getProjectName() + " için yeni görev");
        Navigation.findNavController(v).navigate(R.id.action_nav_project_detail_to_nav_add_task, bundle);
    }
}