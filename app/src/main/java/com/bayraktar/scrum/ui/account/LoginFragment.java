package com.bayraktar.scrum.ui.account;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.airbnb.lottie.LottieAnimationView;
import com.bayraktar.scrum.BaseActivity;
import com.bayraktar.scrum.R;
import com.bayraktar.scrum.model.User;
import com.bayraktar.scrum.view.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static com.bayraktar.scrum.App.currentUser;
import static com.bayraktar.scrum.App.firebaseAuth;
import static com.bayraktar.scrum.App.firebaseService;
import static com.bayraktar.scrum.App.firebaseUser;

public class LoginFragment extends Fragment implements View.OnClickListener {

    ConstraintLayout clLogin;
    TextInputLayout tilEmail, tilPassword;
    TextInputEditText tietEmail, tietPassword;
    CardView cvLogin, cvRegister;


    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        clLogin = view.findViewById(R.id.clLogin);

        tilEmail = view.findViewById(R.id.tilEmail);
        tilPassword = view.findViewById(R.id.tilPassword);

        tietEmail = view.findViewById(R.id.tietEmail);
        tietPassword = view.findViewById(R.id.tietPassword);

        cvLogin = view.findViewById(R.id.cvLogin);
        cvRegister = view.findViewById(R.id.cvRegister);

        cvLogin.setOnClickListener(this);
        cvRegister.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }

    void login() {
        if (isValid()) {
            BaseActivity.hideKeyboard(getActivity());
            setLoading(true);
            String email, password;
            email = tietEmail.getText().toString().trim();
            password = tietPassword.getText().toString().trim();
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            setLoading(false);
                            if (task.isSuccessful()) {
                                firebaseUser = firebaseAuth.getCurrentUser();
                                getUserInformation();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("HATA")
                                        .setIcon(R.drawable.ic_info)
                                        .setMessage("Kullanıcı adı/parola hatalı")
                                        .setPositiveButton("TAMAM", null)
                                        .create().show();
                            }
                        }
                    });
        }
    }

    void setLoading(boolean isLoading) {
        if (isLoading) {
            BaseActivity.showLoading(getActivity(), "Giriş yapılıyor...");
        } else {
            BaseActivity.hideLoading();
        }
    }

    void getUserInformation() {
        ((MainActivity) getActivity()).getUserInformation(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ((MainActivity) getActivity()).onCurrentDataChanged(dataSnapshot);
                BaseActivity.hideKeyboard(getActivity());
                Navigation.findNavController(clLogin).navigate(R.id.action_nav_login_to_nav_main);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    boolean isValid() {
        boolean valid = true;
        if (tietEmail.getText().toString().trim().equals("")) {
            tilEmail.setError("Boş olamaz");
            valid = false;
        } else {
            tilEmail.setError("");
        }
        if (tietPassword.getText().toString().trim().equals("")) {
            tilPassword.setError("Boş olamaz");
            valid = false;
        } else {
            tilPassword.setError("");
        }
        return valid;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cvLogin:
                login();
                break;
            case R.id.cvRegister:
                Bundle bundle = new Bundle();
                bundle.putString("email", tietEmail.getText().toString());
                Navigation.findNavController(v).navigate(R.id.action_nav_login_to_nav_register, bundle);
                break;
        }
    }
}