package com.bayraktar.scrum.ui.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bayraktar.scrum.R;
import com.bayraktar.scrum.adapter.MainListAdapter;
import com.bayraktar.scrum.model.Application;
import com.bayraktar.scrum.model.Invitation;
import com.bayraktar.scrum.model.InvitationStatus;
import com.bayraktar.scrum.model.Member;
import com.bayraktar.scrum.model.Project;
import com.bayraktar.scrum.model.User;
import com.bayraktar.scrum.ui.project.ProjectViewModel;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.bayraktar.scrum.App.currentUser;
import static com.bayraktar.scrum.App.firebaseService;

public class MainFragment extends Fragment implements View.OnClickListener, MainListAdapter.OnMainListListener {

    private ProjectViewModel mViewModel;
    ConstraintLayout clUserProfile;
    ConstraintLayout clLogin;
    CardView cvLogin;
    CardView cvProjects;
    CardView cvTasks;
    CardView cvAccount;
    LottieAnimationView av_splash_animation;

    ImageView ivUserImage;
    TextView tvFullName, tvUserJobTitle, tvLocation, tvBirthDate;
    TextView tvProjects, tvTasks, tvAccount;

    TextView tvNoProject;
    RecyclerView rvProjectList;
    MainListAdapter mainListAdapter;

    List<Project> projectList;
    AlertDialog projectDialog;

    final String myFormat = "dd/MM/yyyy"; //In which you need put here

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(ProjectViewModel.class);

        View root = inflater.inflate(R.layout.fragment_main, container, false);

        av_splash_animation = root.findViewById(R.id.av_splash_animation);

        clUserProfile = root.findViewById(R.id.clUserProfile);
        clLogin = root.findViewById(R.id.clLogin);

        cvLogin = root.findViewById(R.id.cvLogin);
        cvProjects = root.findViewById(R.id.cvProjects);
        cvTasks = root.findViewById(R.id.cvTasks);
        cvAccount = root.findViewById(R.id.cvAccount);

        ivUserImage = root.findViewById(R.id.ivUserImage);
        tvFullName = root.findViewById(R.id.tvFullName);
        tvUserJobTitle = root.findViewById(R.id.tvUserJobTitle);
        tvLocation = root.findViewById(R.id.tvLocation);
        tvBirthDate = root.findViewById(R.id.tvBirthDate);

        tvProjects = root.findViewById(R.id.tvProjects);
        tvTasks = root.findViewById(R.id.tvTasks);
        tvAccount = root.findViewById(R.id.tvAccount);

        tvNoProject = root.findViewById(R.id.tvNoProject);
        rvProjectList = root.findViewById(R.id.rvProjectList);
        mainListAdapter = new MainListAdapter(getContext(), this);
        rvProjectList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvProjectList.setAdapter(mainListAdapter);
        cvLogin.setOnClickListener(this);
        cvProjects.setOnClickListener(this);
        cvTasks.setOnClickListener(this);
        cvAccount.setOnClickListener(this);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.getPublicProjects().observe(getViewLifecycleOwner(), new Observer<List<Project>>() {
            @Override
            public void onChanged(List<Project> projects) {
                setProject(projects);
            }
        });
        checkLoggedIn();
    }

    void stopLoading() {
        av_splash_animation.setVisibility(View.GONE);
    }

    void checkLoggedIn() {
        if (currentUser != null) {
            clUserProfile.setVisibility(View.VISIBLE);
            clLogin.setVisibility(View.GONE);
            setUserProfile();
        } else {
            clUserProfile.setVisibility(View.GONE);
            clLogin.setVisibility(View.VISIBLE);
        }
    }

    void setUserProfile() {
        Glide.with(clUserProfile)
                .load(currentUser.getPhotoURL())
                .fitCenter()
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_broken_image)
                .into(ivUserImage);

        if (currentUser.getName() != null && !TextUtils.isEmpty(currentUser.getName())) {
            tvFullName.setText(currentUser.getName());
        } else {
            tvFullName.setText("Ad bulunamadı");
            tvFullName.setTypeface(tvFullName.getTypeface(), Typeface.ITALIC);
        }

        if (currentUser.getJobTitle() != null && !TextUtils.isEmpty(currentUser.getJobTitle())) {
            tvUserJobTitle.setText(currentUser.getJobTitle());
        } else {
            tvUserJobTitle.setText("Görev bulunamadı");
            tvUserJobTitle.setTypeface(tvUserJobTitle.getTypeface(), Typeface.ITALIC);
        }

        if (currentUser.getLocation() != null && !TextUtils.isEmpty(currentUser.getLocation())) {
            tvLocation.setText(currentUser.getLocation());
        } else {
            tvLocation.setText("Konum bulunamadı");
            tvLocation.setTypeface(tvLocation.getTypeface(), Typeface.ITALIC);
        }

        if (currentUser.getBirthDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
            tvBirthDate.setText(sdf.format(currentUser.getBirthDate().getTime()));
        } else {
            tvBirthDate.setText("Doğum tarihi bulunamadı");
            tvBirthDate.setTypeface(tvBirthDate.getTypeface(), Typeface.ITALIC);
        }
    }


    void setProject(List<Project> projectList) {
        isLoadSuccess(currentUser != null && projectList != null && projectList.size() > 0);
        if (projectList != null) {
            this.projectList = projectList;
            mainListAdapter.setProjectList(this.projectList);
        }
    }

    void isLoadSuccess(boolean isSuccess) {
        stopLoading();
        if (isSuccess) {
            rvProjectList.setVisibility(View.VISIBLE);
            tvNoProject.setVisibility(View.GONE);
        } else {
            rvProjectList.setVisibility(View.GONE);
            tvNoProject.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cvLogin) {
            Navigation.findNavController(v).navigate(R.id.action_nav_main_to_nav_login);
        } else if (id == R.id.cvProjects) {
            Navigation.findNavController(v).navigate(R.id.action_nav_main_to_nav_projects);
        } else if (id == R.id.cvTasks) {
            Navigation.findNavController(v).navigate(R.id.action_nav_main_to_nav_tasks);
        } else if (id == R.id.cvAccount) {
            Navigation.findNavController(v).navigate(R.id.action_nav_main_to_nav_account);
        }
    }

    @Override
    public void onMainListInvitationClick(View v, int position) {
        if (currentUser == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("UYARI")
                    .setIcon(R.drawable.ic_info)
                    .setMessage("Başvuru yapabilmek için giriş yapmalısınız")
                    .setPositiveButton("TAMAM", null)
                    .create().show();
            return;
        }
        Project tempProject = projectList.get(position);

        if (tempProject.getConstituentID().equals(currentUser.getUserID())) {
            Snackbar.make(v, "Kendi projenize başvuramazsınız", BaseTransientBottomBar.LENGTH_SHORT).show();
            return;
        }

        if (tempProject.getMembers() != null && tempProject.getMembers().containsKey(currentUser.getUserID())) {
            Snackbar.make(v, "Projeye katıldınız", BaseTransientBottomBar.LENGTH_SHORT).show();
            return;
        }

        boolean canApply = true;
        boolean isInvited = false;
        if (tempProject.getApplications() != null && tempProject.getApplications().containsKey(currentUser.getUserID())) {
            canApply = false;
        }
        if (tempProject.getInvitations() != null && tempProject.getInvitations().containsKey(currentUser.getUserID())) {
            canApply = false;
            Integer invitation = tempProject.getInvitations().get(currentUser.getUserID());
            if (invitation != null && invitation == 0) {
                isInvited = true;
            }
        }
        if (isInvited) {
            acceptInvitation(v, tempProject, position);
        } else if (canApply) {
            applyProject(v, tempProject, position);
        } else {
            Snackbar.make(v, "Başvuru zaten yapılmış", BaseTransientBottomBar.LENGTH_SHORT).show();
        }
    }

    void applyProject(View v, final Project tempProject, final int position) {
        HashMap<String, Object> application = new HashMap<>();
        application.put(currentUser.getUserID(), 0);
        HashMap<String, Integer> applicationList = tempProject.getApplications();
        if (applicationList == null) {
            applicationList = new HashMap<>();
        }
        applicationList.put(currentUser.getUserID(), 0);
        tempProject.setApplications(applicationList);
        firebaseService.updateApplication(tempProject.getProjectID(), application);
        mainListAdapter.notifyItemChanged(position);
        Snackbar.make(v, "Başvuru yapıldı", BaseTransientBottomBar.LENGTH_SHORT).setAction("İPTAL", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Integer> applicationList = tempProject.getApplications();
                if (applicationList == null) {
                    applicationList = new HashMap<>();
                }
                applicationList.remove(currentUser.getUserID());
                tempProject.setApplications(applicationList);
                firebaseService.deleteApplication(tempProject.getProjectID(), currentUser.getUserID());
                mainListAdapter.notifyItemChanged(position);
            }
        }).show();
    }

    void acceptInvitation(View v, final Project tempProject, final int position) {
//        if (tempProject.getInvitations() != null && tempProject.getInvitations().containsKey(currentUser.getUserID())) {
//            Integer invitation = tempProject.getInvitations().get(currentUser.getUserID());
//            if (invitation != null) {
//                invitation = 1;
//                HashMap<String, Integer> hashMap = tempProject.getInvitations();
//                tempProject.setInvitations(hashMap);
//                firebaseService.updateInvitation(tempProject.getProjectID(), invitation);
//                Member newMember = new Member();
//                newMember.setUser(currentUser);
//                newMember.setMembershipDate(Calendar.getInstance().getTime());
//                newMember.setAuthorityGroupID(0);
//                HashMap<String, Integer> memberHashMap = tempProject.getMembers();
//                if (memberHashMap == null) {
//                    memberHashMap = new HashMap<>();
//                }
//                memberHashMap.put(newMember.getUser().getUserID(), newMember);
//                tempProject.setMemberList(memberHashMap);
//                firebaseService.setUserInvitation(currentUser.getUserID(), invitation);
//                firebaseService.insertMember(tempProject.getProjectID(), newMember);
//                mainListAdapter.notifyItemChanged(position);
//                final HashMap<String, Member> finalMemberHashMap = memberHashMap;
//                Snackbar.make(v, "Davet kabul edildi", BaseTransientBottomBar.LENGTH_SHORT).setAction("İPTAL", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        invitation.setInvitationStatus(new InvitationStatus(0, "Davet Edildiniz"));
//                        HashMap<String, Invitation> hashMap = tempProject.getInvitationList();
//                        hashMap.put(invitation.getInvitedUser().getUserID(), invitation);
//                        tempProject.setInvitationList(hashMap);
//
//                        finalMemberHashMap.remove(currentUser.getUserID());
//                        tempProject.setMemberList(finalMemberHashMap);
//                        firebaseService.setUserInvitation(currentUser.getUserID(), invitation);
//                        firebaseService.updateInvitation(tempProject.getProjectID(), invitation);
//                        firebaseService.deleteMember(tempProject.getProjectID(), currentUser.getUserID());
//                        mainListAdapter.notifyItemChanged(position);
//                    }
//                }).show();
//            }
//        }
    }

    void showProjectDetail(final View v, final Project project, final User user, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_project_detail, null, false);

        ImageView ivProject = view.findViewById(R.id.ivProject);
        TextView tvProject = view.findViewById(R.id.tvProject);

        ImageView ivConstituent = view.findViewById(R.id.ivConstituent);
        TextView tvUsername = view.findViewById(R.id.tvUsername);
        TextView tvJobTitle = view.findViewById(R.id.tvJobTitle);
        TextView tvLocation = view.findViewById(R.id.tvLocation);

        TextView tvDateValue = view.findViewById(R.id.tvDateValue);
        TextView tvMemberValue = view.findViewById(R.id.tvMemberValue);
        TextView tvInvitationValue = view.findViewById(R.id.tvInvitationValue);

        CardView cvApply = view.findViewById(R.id.cvApply);
        TextView tvApply = view.findViewById(R.id.tvApply);

        Glide.with(v)
                .load(project.getProjectImageURL())
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_broken_image)
                .fitCenter()
                .into(ivProject);
        tvProject.setText(project.getProjectName());

        Glide.with(v)
                .load(user.getPhotoURL())
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_broken_image)
                .fitCenter()
                .into(ivConstituent);
        tvUsername.setText(user.getName());
        tvJobTitle.setText(user.getJobTitle());
        tvLocation.setText(user.getLocation());

        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        tvDateValue.setText(sdf.format(project.getCreateDate().getTime()));
        int memberValue = 0;
        if (project.getMembers() != null)
            memberValue = project.getMembers().size();
        tvMemberValue.setText(String.valueOf(memberValue));

        int applicationValue = 0;
        if (project.getApplications() != null)
            applicationValue = project.getApplications().size();
        tvInvitationValue.setText(String.valueOf(applicationValue));

        boolean isInvited = false;
        boolean canApply = true;

        if (currentUser != null) {
            if (project.getConstituentID().equals(currentUser.getUserID())) {
                tvApply.setText("Proje Size Ait");
                cvApply.setEnabled(false);
            } else if (project.getMembers() != null && project.getMembers().containsKey(currentUser.getUserID())) {
                tvApply.setText("Katıldınız");
                cvApply.setEnabled(false);
            } else if (project.getInvitations() != null && project.getInvitations().containsKey(currentUser.getUserID())) {
                canApply = false;
                Integer invitation = project.getInvitations().get(currentUser.getUserID());
                if (invitation != null && invitation == 0) {
                    tvApply.setText("Davet edildiniz");
                    isInvited = true;
                }
            } else if (project.getApplications() != null && project.getApplications().containsKey(currentUser.getUserID())) {
                Integer application = project.getApplications().get(currentUser.getUserID());
                if (application != null && application == 0) {
                    canApply = false;
                    tvApply.setText("Başvurdunuz");
                    cvApply.setEnabled(false);
                }
            }
        }

        view.findViewById(R.id.cvCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (projectDialog != null && projectDialog.isShowing()) {
                    projectDialog.dismiss();
                    projectDialog = null;
                }
            }
        });
        final boolean finalIsInvited = isInvited;
        final boolean finalCanApply = canApply;
        view.findViewById(R.id.cvApply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalIsInvited) {
                    acceptInvitation(v, project, position);
                } else if (finalCanApply) {
                    applyProject(v, project, position);
                }
                if (projectDialog != null && projectDialog.isShowing()) {
                    projectDialog.dismiss();
                    projectDialog = null;
                }
            }
        });

        projectDialog = builder.setView(view).create();
        projectDialog.show();
    }

    @Override
    public void onMainListDetailClick(final View v, final int position) {
        final Project tempProject = projectList.get(position);

        firebaseService.getUser(tempProject.getConstituentID(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        showProjectDetail(v, tempProject, user, position);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onMainListItemClick(View v, int position) {
//        Project taskListModel = projectList.get(position);
//        Bundle bundle = new Bundle();
//        bundle.putString("projectID", taskListModel.getProjectID());
//        bundle.putString("title", taskListModel.getProjectName());
//        Navigation.findNavController(v).navigate(R.id.action_nav_main_to_nav_project_detail, bundle);
    }
}