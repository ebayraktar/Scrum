package com.bayraktar.scrum.ui.account;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bayraktar.scrum.BaseActivity;
import com.bayraktar.scrum.R;
import com.bayraktar.scrum.model.User;
import com.bayraktar.scrum.view.MainActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

import static com.bayraktar.scrum.App.firebaseAuth;
import static com.bayraktar.scrum.App.firebaseService;
import static com.bayraktar.scrum.App.firebaseUser;

public class RegisterFragment extends Fragment {

    private static final String MAIL_ADDRESS = "email";

    private ConstraintLayout clRegister;
    private TextInputLayout tilFullName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputLayout tilPasswordConfirm;

    private TextInputEditText tietFullName;
    private TextInputEditText tietEmail;
    private TextInputEditText tietPassword;
    private TextInputEditText tietPasswordConfirm;

    private String email;

    public static RegisterFragment newInstance(String email) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(MAIL_ADDRESS, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            email = getArguments().getString(MAIL_ADDRESS);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        clRegister = view.findViewById(R.id.clRegister);

        tilFullName = view.findViewById(R.id.tilFullName);
        tilEmail = view.findViewById(R.id.tilEmail);
        tilPassword = view.findViewById(R.id.tilPassword);
        tilPasswordConfirm = view.findViewById(R.id.tilPasswordConfirm);

        tietFullName = view.findViewById(R.id.tietFullName);
        tietEmail = view.findViewById(R.id.tietEmail);

        tietPassword = view.findViewById(R.id.tietPassword);
        tietPasswordConfirm = view.findViewById(R.id.tietPasswordConfirm);

        view.findViewById(R.id.cvSignUp).setOnClickListener(v -> register());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tietEmail.setText(email);
    }

    void register() {
        if (isValid()) {
            showLoading();
            final String email = tietEmail.getText().toString().trim();
            final String password = tietPassword.getText().toString().trim();
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), task -> {
                        if (task.isSuccessful()) {
                            firebaseUser = firebaseAuth.getCurrentUser();
                            assert firebaseUser != null;
                            if (!firebaseUser.isEmailVerified()) {
                                firebaseUser.sendEmailVerification();
                            }
                            User tempUser = new User();
                            tempUser.setUserID(firebaseUser.getUid());
                            tempUser.setEmail(email);
                            tempUser.setPhotoURL("https://firebasestorage.googleapis.com/v0/b/scrum-aabe7.appspot.com/o/users%2Fdefault%2Fdefault_user_1.png?alt=media&token=6b04c996-6262-4302-a1f3-da333dac1027");
                            tempUser.setVerified(firebaseUser.isEmailVerified());
                            tempUser.setName(tietFullName.getText().toString());
                            tempUser.setMembershipDate(Calendar.getInstance().getTime());
                            firebaseService.setUser(tempUser);
                            getUserInformation();
                        } else {
                            Toast.makeText(getContext(), "Kayıt başarısız " + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        hideLoading();
                    });
        }
    }

    void showLoading() {
        BaseActivity.showLoading(getActivity(), "Kullanıcı oluşturuluyor...");
    }

    void hideLoading() {
        BaseActivity.hideLoading();
    }

    void getUserInformation() {
        ((MainActivity) getActivity()).getUserInformation(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ((MainActivity) getActivity()).onCurrentDataChanged(dataSnapshot);
                BaseActivity.hideKeyboard(getActivity());
                Navigation.findNavController(clRegister).navigate(R.id.action_nav_register_to_nav_main);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //
            }
        });
    }

    boolean isValid() {
        boolean valid = true;

        if (TextUtils.isEmpty(tietFullName.getText())) {
            tilFullName.setError(getString(R.string.required_field));
            valid = false;
        } else {
            tilFullName.setError("");
            tilFullName.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(tietEmail.getText())) {
            tilEmail.setError(getString(R.string.required_field));
            valid = false;
        } else {
            tilEmail.setError("");
            tilEmail.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(tietPassword.getText())) {
            tilPassword.setError(getString(R.string.required_field));
            valid = false;
        } else {
            tilPassword.setError("");
            tilPassword.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(tietPasswordConfirm.getText())) {
            tilPasswordConfirm.setError(getString(R.string.required_field));
            valid = false;
        } else {
            tilPasswordConfirm.setError("");
            tilPasswordConfirm.setErrorEnabled(false);
        }
        return valid;
    }
}