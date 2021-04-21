package com.bayraktar.scrum.ui.account;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bayraktar.scrum.BaseActivity;
import com.bayraktar.scrum.R;
import com.bayraktar.scrum.view.MainActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.bayraktar.scrum.App.firebaseAuth;
import static com.bayraktar.scrum.App.firebaseUser;

public class LoginFragment extends Fragment {

    ConstraintLayout clLogin;
    TextInputLayout tilEmail;
    TextInputLayout tilPassword;

    TextInputEditText tietEmail;
    TextInputEditText tietPassword;


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


        view.findViewById(R.id.cvLogin).setOnClickListener(v -> login());
        view.findViewById(R.id.cvRegister).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("email", tietEmail.getText().toString());
            Navigation.findNavController(v).navigate(R.id.action_nav_login_to_nav_register, bundle);
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        if (getActivity() != null) {
            BaseActivity.hideKeyboard(getActivity());
        }
        super.onDestroyView();
    }

    void login() {
        if (isValid()) {
            BaseActivity.hideKeyboard(getActivity());
            setLoading(true);
            String email = tietEmail.getText().toString().trim();
            String password = tietPassword.getText().toString().trim();
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), task -> {
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
                //
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
}