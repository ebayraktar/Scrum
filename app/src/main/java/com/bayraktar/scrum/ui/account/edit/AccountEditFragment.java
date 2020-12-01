package com.bayraktar.scrum.ui.account.edit;

import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bayraktar.scrum.BaseActivity;
import com.bayraktar.scrum.R;
import com.bayraktar.scrum.ui.project.detail.main.ProjectDetailMainFragment;
import com.bayraktar.scrum.ui.project.detail.main.ProjectDetailMainViewModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.bayraktar.scrum.App.currentUser;
import static com.bayraktar.scrum.App.firebaseService;

public class AccountEditFragment extends Fragment implements View.OnClickListener {


    private static final String ARG_PARAM1 = "param1";
    private int mParam1;

    ImageView ivUserImage, ivCalendar;
    TextView tvBirthDate;

    TextInputLayout tilFullName, tilJobTitle, tilAbout, tilLocation;
    TextInputEditText tietFullName, tietJobTitle, tietAbout, tietLocation;

    final Calendar deadlineCalendar = Calendar.getInstance();
    final String myFormat = "dd/MM/yyyy"; //In which you need put here
    SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");

    Uri selectedImage;

    public AccountEditFragment() {
    }

    public static AccountEditFragment newInstance(int param1) {

        AccountEditFragment fragment = new AccountEditFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }


    DatePickerDialog.OnDateSetListener deadline = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            deadlineCalendar.set(Calendar.YEAR, year);
            deadlineCalendar.set(Calendar.MONTH, monthOfYear);
            deadlineCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(tvBirthDate, deadlineCalendar);
        }
    };

    void updateLabel(TextView textInputEditText, Calendar myCalendar) {
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        textInputEditText.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_edit, container, false);
        ivUserImage = view.findViewById(R.id.ivUserImage);

        tilFullName = view.findViewById(R.id.tilFullName);
        tilJobTitle = view.findViewById(R.id.tilJobTitle);
        tilAbout = view.findViewById(R.id.tilAbout);
        tilLocation = view.findViewById(R.id.tilLocation);

        tietFullName = view.findViewById(R.id.tietFullName);
        tietJobTitle = view.findViewById(R.id.tietJobTitle);
        tietAbout = view.findViewById(R.id.tietAbout);
        tietLocation = view.findViewById(R.id.tietLocation);

        ivCalendar = view.findViewById(R.id.ivCalendar);

        tvBirthDate = view.findViewById(R.id.tvBirthDate);

        ivUserImage.setOnClickListener(this);
        ivCalendar.setOnClickListener(this);
        view.findViewById(R.id.cvSave).setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeUser();
    }

    void initializeUser() {
        if (currentUser != null) {
            if (currentUser.getPhotoURL() != null) {
                Glide.with(ivUserImage)
                        .load(currentUser.getPhotoURL())
                        .placeholder(R.drawable.ic_image)
                        .error(R.drawable.ic_broken_image)
                        .fitCenter()
                        .into(ivUserImage);
            }
            tietFullName.setText(currentUser.getName());
            tietJobTitle.setText(currentUser.getJobTitle());
            tietAbout.setText(currentUser.getAbout());
            tietLocation.setText(currentUser.getLocation());
            if (currentUser.getBirthDate() != null) {
                deadlineCalendar.setTime(currentUser.getBirthDate());
                updateLabel(tvBirthDate, deadlineCalendar);
            }
        }
    }

    void showCalendar() {
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

    boolean validateForm() {
        boolean isValid = true;
        if (tietFullName.getText().toString().equals("")) {
            tilFullName.setError("Boş olamaz");
            isValid = false;
        } else {
            tilFullName.setError("");
        }
        return isValid;
    }

    void showLoading() {
        BaseActivity.showLoading(getActivity(), "Profil güncelleniyor...");
    }

    void hideLoading() {
        BaseActivity.hideLoading();
    }

    void updateUser(final View v) {

        try {
            showLoading();
            BaseActivity.hideKeyboard(getActivity());
            Date _deadline = formatter1.parse(tvBirthDate.getText().toString());

            currentUser.setName(tietFullName.getText().toString());
            currentUser.setJobTitle(tietJobTitle.getText().toString());
            currentUser.setAbout(tietAbout.getText().toString());
            currentUser.setBirthDate(_deadline);
            currentUser.setLocation(tietLocation.getText().toString());
            firebaseService.setUser(currentUser);
            if (selectedImage != null) {
                firebaseService.uploadUserImage(currentUser.getUserID(), selectedImage, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                hideLoading();
                                currentUser.setPhotoURL(task.getResult().toString());
                                firebaseService.setUser(currentUser);
                                Navigation.findNavController(v).navigate(R.id.action_fragment_account_edit_to_account);
                            }
                        });
                    }
                });
            } else {
                hideLoading();
                Navigation.findNavController(v).navigate(R.id.action_fragment_account_edit_to_account);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Log.d("TAG", "onActivityResult: " + data.getData());
                    Uri selectedImage = data.getData();
                    ivUserImage.setImageURI(selectedImage);
                }

                break;
            case 1:

                if (data != null) {
                    Log.d("TAG", "onActivityResult: " + data.getData());
                }
                if (resultCode == RESULT_OK) {
                    selectedImage = data.getData();
                    ivUserImage.setImageURI(selectedImage);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void selectImage() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivUserImage:
                selectImage();
                break;
            case R.id.ivCalendar:
                showCalendar();
                break;
            case R.id.cvSave:
                if (validateForm()) {
                    updateUser(v);
                }
                break;
        }
    }
}