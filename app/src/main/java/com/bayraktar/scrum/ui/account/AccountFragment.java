package com.bayraktar.scrum.ui.account;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.bayraktar.scrum.R;
import com.bayraktar.scrum.ui.account.edit.AccountEditFragment;
import com.bayraktar.scrum.ui.account.edit.AccountEditViewModel;
import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.bayraktar.scrum.App.currentUser;

public class AccountFragment extends Fragment implements View.OnClickListener {

    ImageView ivUserImage;
    TextView tvFullName, tvJobTitle, tvAbout, tvBirthDate, tvMembershipDate, tvLocation;

    private static final String ARG_PARAM1 = "param1";
    private int mParam1;

    SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
    final String myFormat = "dd/MM/yyyy"; //In which you need put here

    Calendar myCalendar = Calendar.getInstance();

    public AccountFragment() {
    }

    public static AccountFragment newInstance(int param1) {

        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        ivUserImage = view.findViewById(R.id.ivUserImage);

        tvFullName = view.findViewById(R.id.tvFullName);
        tvJobTitle = view.findViewById(R.id.tvJobTitle);
        tvAbout = view.findViewById(R.id.tvAbout);
        tvBirthDate = view.findViewById(R.id.tvBirthDate);
        tvMembershipDate = view.findViewById(R.id.tvMembershipDate);
        tvLocation = view.findViewById(R.id.tvLocation);

        ivUserImage.setOnClickListener(this);
        view.findViewById(R.id.cvEditProfile).setOnClickListener(this);

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
            tvFullName.setText(currentUser.getName());
            tvJobTitle.setText(currentUser.getJobTitle());
            tvAbout.setText(currentUser.getAbout());
            if (currentUser.getBirthDate() != null) {
                myCalendar.setTime(currentUser.getBirthDate());
                updateLabel(tvBirthDate);
            }
            if (currentUser.getMembershipDate() != null) {
                myCalendar.setTime(currentUser.getMembershipDate());
                updateLabel(tvMembershipDate);
            }
            tvLocation.setText(currentUser.getLocation());
        }
    }

    void updateLabel(TextView textView) {
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        textView.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivUserImage:
                Toast.makeText(v.getContext(), "FOTOGRAF SEC", Toast.LENGTH_SHORT).show();
                break;
            case R.id.cvEditProfile:
                Navigation.findNavController(v).navigate(R.id.action_nav_account_to_nav_account_edit);
                break;
        }
    }
}