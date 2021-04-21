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
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.bayraktar.scrum.R;
import com.bayraktar.scrum.adapter.ViewPagerAdapter;
import com.bayraktar.scrum.model.Project;
import com.bayraktar.scrum.view.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import static com.bayraktar.scrum.App.currentUser;
import static com.bayraktar.scrum.App.firebaseService;

public class ProjectDetailFragment extends Fragment implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {

    private ViewPager viewPager;
    private FloatingActionButton fab;
    private BottomNavigationView bottomNavView;

    private Project currentProject;

    private static final String PROJECT_ID = "projectID";
    private String projectID;

    private ViewPagerAdapter mViewPagerAdapter;
    private LottieAnimationView av_splash_animation;

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
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.project_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_detail, container, false);
        viewPager = view.findViewById(R.id.view_pager);
        fab = view.findViewById(R.id.fab);
        bottomNavView = view.findViewById(R.id.bottom_nav_view);
        av_splash_animation = view.findViewById(R.id.av_splash_animation);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProjectDetailViewModel mViewModel = new ViewModelProvider(this).get(ProjectDetailViewModel.class);
        mViewModel.getProject(projectID).observe(getViewLifecycleOwner(), new Observer<Project>() {
            @Override
            public void onChanged(Project project) {
                currentProject = project;
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(project.getProjectName());
                init();
            }
        });

        fab.setOnClickListener(this);
        bottomNavView.setOnNavigationItemSelectedListener(this);

        mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        if (savedInstanceState == null) {
            onNavigationItemSelected(bottomNavView.getMenu().getItem(0));
        }
    }

    private void init() {
        setHasOptionsMenu(true);

        mViewPagerAdapter.setProjectId(projectID);
        viewPager.setAdapter(mViewPagerAdapter);
        viewPager.addOnPageChangeListener(this);
        av_splash_animation.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();
        if (currentProject != null) {
            if (itemId == R.id.menu_delete) {
                onDelete();
                return true;
            } else if (itemId == R.id.menu_edit) {
                onEdit();
                return true;
            } else if (itemId == R.id.menu_applications) {
                onApplications();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void onDelete() {
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
            Snackbar.make(bottomNavView, "Bu işlemi yapabilmek için yetkiniz yok", BaseTransientBottomBar.LENGTH_LONG).show();
        }
    }

    private void onEdit() {
        if (currentProject.getConstituentID().equals(currentUser.getUserID())) {
            Bundle bundle = new Bundle();
            bundle.putString("projectID", currentProject.getProjectID());
            bundle.putString("title", currentProject.getProjectName() + " Düzenle");
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_nav_project_detail_to_nav_new_project, bundle);
        } else {
            Snackbar.make(bottomNavView, "Bu işlemi yapabilmek için yetkiniz yok", BaseTransientBottomBar.LENGTH_LONG).show();
        }
    }

    private void onApplications() {
        if (currentProject.getConstituentID().equals(currentUser.getUserID())) {
            Bundle bundle = new Bundle();
            bundle.putString("projectID", currentProject.getProjectID());
            bundle.putString("title", currentProject.getProjectName() + " Başvurular");
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_nav_project_detail_to_nav_project_applications, bundle);
        } else {
            Snackbar.make(bottomNavView, "Bu işlemi yapabilmek için yetkiniz yok", BaseTransientBottomBar.LENGTH_LONG).show();
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

    //BottomNavigationView.OnNavigationItemSelectedListener
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == R.id.navigation_pending) {
            viewPager.setCurrentItem(0);
        } else if (itemId == R.id.navigation_active) {
            viewPager.setCurrentItem(1);
        } else if (itemId == R.id.navigation_completed) {
            viewPager.setCurrentItem(2);
        } else if (itemId == R.id.navigation_suspended) {
            viewPager.setCurrentItem(3);
        } else {
            return false;
        }
        return true;
    }

    //ViewPager.OnPageChangeListener
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                bottomNavView.getMenu().findItem(R.id.navigation_pending).setChecked(true);
                break;
            case 1:
                bottomNavView.getMenu().findItem(R.id.navigation_active).setChecked(true);
                break;
            case 2:
                bottomNavView.getMenu().findItem(R.id.navigation_completed).setChecked(true);
                break;
            case 3:
                bottomNavView.getMenu().findItem(R.id.navigation_suspended).setChecked(true);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}