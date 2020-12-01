package com.bayraktar.scrum.ui.project.add;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bayraktar.scrum.BaseActivity;
import com.bayraktar.scrum.R;
import com.bayraktar.scrum.model.Invitation;
import com.bayraktar.scrum.model.MobileResult;
import com.bayraktar.scrum.model.Project;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;
import static com.bayraktar.scrum.App.currentUser;
import static com.bayraktar.scrum.App.firebaseService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProjectAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProjectAddFragment extends Fragment implements View.OnClickListener {

    ProjectAddViewModel viewModel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PROJECT_ID = "projectID";

    // TODO: Rename and change types of parameters
    private String projectID;

    ConstraintLayout clNewProject;

    TextInputLayout tilProjectName;
    TextInputEditText tietProjectName;
    ImageView ivProject;

    SwitchCompat swcPrivacyMode;

    TextInputLayout tilInvitation;
    TextInputEditText tietInvitation;

    ListView lvInvitation;

    boolean isNewProject;

    ArrayAdapter<String> adapter;
    List<String> invitationEmails;

    Project currentProject;
    Uri selectedImage;
    String defaultImage;

    public ProjectAddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param projectID Parameter 1.
     * @return A new instance of fragment ProjectAddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProjectAddFragment newInstance(String projectID) {
        ProjectAddFragment fragment = new ProjectAddFragment();
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
            isNewProject = projectID == null || projectID.equals("");
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroyView() {
        BaseActivity.hideKeyboard(getActivity());
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_project_add, container, false);

        viewModel = new ViewModelProvider(this).get(ProjectAddViewModel.class);

        clNewProject = view.findViewById(R.id.clNewProject);

        tilProjectName = view.findViewById(R.id.tilProjectName);
        tietProjectName = view.findViewById(R.id.tietProjectName);
        ivProject = view.findViewById(R.id.ivProject);

        swcPrivacyMode = view.findViewById(R.id.swcPrivacyMode);

        tilInvitation = view.findViewById(R.id.tilInvitation);
        tietInvitation = view.findViewById(R.id.tietInvitation);

        lvInvitation = view.findViewById(R.id.lvInvitation);
        defaultImage = "https://firebasestorage.googleapis.com/v0/b/scrum-aabe7.appspot.com/o/projects%2Fdefault%2Fdefault_project_1.png?alt=media&token=b8484d50-1acc-4fc3-a947-90eb74d8c40b";

        swcPrivacyMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonView.setText("Özel");
                } else {
                    buttonView.setText("Genel");
                }
            }
        });

        ivProject.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tietProjectName.requestFocus();

        if (currentUser == null)
            return;
        tietInvitation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String text = v.getText().toString();
                    if (!text.equals("")) {
                        if (validateEmail(text)) {
                            adapter.add(v.getText().toString());
                            v.setText("");
                            tilInvitation.setError("");
                        } else {
                            tilInvitation.setError("Geçerli adres giriniz");
                        }
                    }
                }
                return true;
            }
        });
        tietProjectName.requestFocus();
        BaseActivity.showKeyboard(getActivity());
        initialize();
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
//                if (isNewProject) {
                saveProject();
//                } else {
//                    updateProject();
//                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void showLoadingDialog() {
        if (isNewProject) {
            BaseActivity.showLoading(getActivity(), "Proje oluşturuluyor...");
        } else {
            BaseActivity.showLoading(getActivity(), "Proje güncelleniyor...");
        }
    }

    void hideLoadingDialog() {
        BaseActivity.hideLoading();
    }

    Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    void initialize() {
        invitationEmails = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, invitationEmails);
        lvInvitation.setAdapter(adapter);

        Glide.with(ivProject)
                .load("https://firebasestorage.googleapis.com/v0/b/scrum-aabe7.appspot.com/o/projects%2Fdefault%2Fdefault_project_1.png?alt=media&token=b8484d50-1acc-4fc3-a947-90eb74d8c40b")
                .error(R.drawable.ic_broken_image)
                .fitCenter()
                .placeholder(R.drawable.ic_image)
                .into(ivProject);

        if (!isNewProject) {
            initializeProject();
        }
    }

    void initializeProject() {
        firebaseService.getProject(projectID, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentProject = snapshot.getValue(Project.class);
                    if (currentProject != null) {
                        tietProjectName.setText(currentProject.getProjectName());
                        Glide.with(ivProject)
                                .load(currentProject.getProjectImageURL())
                                .error(R.drawable.ic_broken_image)
                                .fitCenter()
                                .placeholder(R.drawable.ic_image)
                                .into(ivProject);
                        swcPrivacyMode.setChecked(currentProject.isPrivacyMode());
                        invitationEmails.clear();
                        defaultImage = currentProject.getProjectImageURL();
                        Log.d("TAG", "Tasks: " + currentProject.getTaskList());
//                        if (currentProject.getInvitations() != null) {
//                            for (Integer invitation : currentProject.getInvitations().values()) {
//                                invitationEmails.add(invitation.getInvitedUser().getEmail());
//                            }
//                            adapter.notifyDataSetChanged();
//                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    boolean isFormValid() {
        boolean validate = true;
        if (tietProjectName.getText().toString().trim().equals("")) {
            tilProjectName.setError("Boş olamaz");
            validate = false;
        } else {
            tilProjectName.setError("");
        }
        return validate;

    }

    void saveProject() {
        BaseActivity.hideKeyboard(getActivity());
        if (currentUser != null) {
            showLoadingDialog();
            if (currentProject == null) {
                currentProject = new Project();
                currentProject.setCreateDate(Calendar.getInstance().getTime());
                currentProject.setConstituentID(currentUser.getUserID());
            }
            currentProject.setProjectName(tietProjectName.getText().toString().trim());
            currentProject.setPrivacyMode(swcPrivacyMode.isChecked());
            currentProject.setProjectImageURL(defaultImage);
            viewModel.insertProject(currentProject, invitationEmails).
                    observe(getViewLifecycleOwner(), new Observer<MobileResult>() {
                        @Override
                        public void onChanged(MobileResult mobileResult) {

                            if (mobileResult.getCode() != 0) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Hata")
                                        .setMessage("Proje oluşturulurken hata meydana geldi: " + mobileResult.getMessage())
                                        .setIcon(R.drawable.ic_info)
                                        .setPositiveButton("TAMAM", null)
                                        .create().show();
                                hideLoadingDialog();
                                return;
                            }
                            if (selectedImage != null) {
                                firebaseService.uploadProjectImage(currentProject.getProjectID(), selectedImage, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                hideLoadingDialog();
                                                currentProject.setProjectImageURL(task.getResult().toString());
                                                firebaseService.insertProject(currentProject);
                                                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_nav_new_project_to_nav_projects);
                                            }
                                        });
                                    }
                                });
                            } else {
                                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_nav_new_project_to_nav_projects);
                                hideLoadingDialog();
                            }
                        }
                    });
        }
    }

//    void updateProject() {
//        BaseActivity.hideKeyboard(getActivity());
//        showLoadingDialog();
//        currentProject.setProjectName(tietProjectName.getText().toString().trim());
//        currentProject.setPrivacyMode(swcPrivacyMode.isChecked());
//        viewModel.updateProject(currentProject, invitationEmails).observe(getViewLifecycleOwner(), new Observer<MobileResult>() {
//            @Override
//            public void onChanged(MobileResult mobileResult) {
//                if (mobileResult.getCode() == -1) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                    builder.setTitle("Hata")
//                            .setMessage("Proje güncellenirken hata meydana geldi: " + mobileResult.getMessage())
//                            .setIcon(R.drawable.ic_info)
//                            .setPositiveButton("TAMAM", null)
//                            .create().show();
//                    hideLoadingDialog();
//                }
//
//                if (selectedImage != null) {
//                    firebaseService.uploadProjectImage(currentProject.getProjectID(), selectedImage, new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Uri> task) {
//                                    currentProject.setProjectImageURL(task.getResult().toString());
//                                    firebaseService.updateProject(currentProject);
//                                    Bundle bundle = new Bundle();
//                                    bundle.putString("title", currentProject.getProjectName());
//                                    bundle.putString("projectID", currentProject.getProjectID());
//                                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_nav_new_project_to_nav_project_detail, bundle);
//                                    hideLoadingDialog();
//                                }
//                            });
//                        }
//                    });
//                } else {
//                    Bundle bundle = new Bundle();
//                    bundle.putString("title", currentProject.getProjectName());
//                    bundle.putString("projectID", currentProject.getProjectID());
//                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_nav_new_project_to_nav_project_detail, bundle);
//                    hideLoadingDialog();
//                }
//            }
//        });
//    }

    void selectImage() {
//        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(takePicture, 0);//zero can be replaced with any action code (called requestCode)
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Log.d("TAG", "onActivityResult: " + data.getData());
                    Uri selectedImage = data.getData();
                    ivProject.setImageURI(selectedImage);
                }

                break;
            case 1:

                if (data != null) {
                    Log.d("TAG", "onActivityResult: " + data.getData());
                }
                if (resultCode == RESULT_OK) {
                    selectedImage = data.getData();
                    ivProject.setImageURI(selectedImage);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivProject) {
            selectImage();
        }
    }
}