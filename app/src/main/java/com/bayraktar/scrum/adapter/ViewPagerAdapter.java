package com.bayraktar.scrum.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.bayraktar.scrum.ui.project.detail.main.ProjectDetailMainFragment;

/**
 * Created by Emre BAYRAKTAR on 5/1/2021.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private String projectId;

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return ProjectDetailMainFragment.newInstance(position, projectId);
    }

    @Override
    public int getCount() {
        return 4;
    }
}
