package com.bayraktar.scrum.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bayraktar.scrum.App;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;
        if (App.getPrefManager().isFirstTimeLaunch())
            intent = new Intent(SplashActivity.this, WizardActivity.class);
        else
            intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}