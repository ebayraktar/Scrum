package com.bayraktar.scrum.view;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bayraktar.scrum.App;
import com.bayraktar.scrum.BaseActivity;
import com.bayraktar.scrum.R;
import com.bayraktar.scrum.model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.bayraktar.scrum.App.currentUser;
import static com.bayraktar.scrum.App.firebaseAuth;
import static com.bayraktar.scrum.App.firebaseService;
import static com.bayraktar.scrum.App.firebaseUser;

public class MainActivity extends BaseActivity {

    private DrawerLayout drawer;
    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private NavigationView navigationView;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_exit).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                logout();
                return true;
            }
        });

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_chart, R.id.nav_projects, R.id.nav_tasks)
                .setDrawerLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        initialize();
    }

    private void initialize() {
        if (firebaseUser != null) {
            getUserInformation(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    onCurrentDataChanged(dataSnapshot);
                    if (navController.getCurrentDestination().getId() == R.id.nav_main) {
                        navController.navigate(R.id.action_nav_main_self);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void onCurrentDataChanged(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            User tempUser = dataSnapshot.getValue(User.class);
            if (tempUser == null) {
                tempUser = new User();
                tempUser.setUserID(firebaseUser.getUid());
                tempUser.setEmail(firebaseUser.getEmail());
                tempUser.setVerified(firebaseUser.isEmailVerified());
                if (firebaseUser.getPhotoUrl() != null)
                    tempUser.setPhotoURL(firebaseUser.getPhotoUrl().toString());
                firebaseService.setUser(tempUser);
            }
            currentUser = tempUser;
            login(true);
        }
    }

    public void login(boolean isLogin) {
        Menu menu = navigationView.getMenu();
        MenuItem itemProjects = menu.findItem(R.id.nav_projects);
        if (itemProjects != null) {
            itemProjects.setVisible(isLogin);
        }
        MenuItem itemTasks = menu.findItem(R.id.nav_tasks);
        if (itemTasks != null) {
            itemTasks.setVisible(isLogin);
        }
        MenuItem itemAccount = menu.findItem(R.id.nav_account);
        if (itemAccount != null) {
            itemAccount.setVisible(isLogin);
        }
        MenuItem itemExit = menu.findItem(R.id.nav_exit);
        if (itemExit != null) {
            itemExit.setVisible(isLogin);
        }
//        MenuItem itemNotifications = menu.findItem(R.id.nav_notifications);
//        if (itemNotifications != null) {
//            itemNotifications.setVisible(isLogin);
//        }
    }

    public void logout() {
        login(false);
        firebaseAuth.signOut();
        currentUser = null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (navController.getCurrentDestination().getId() == R.id.nav_main) {
            navController.navigate(R.id.action_nav_main_self);
        } else if (navController.getCurrentDestination().getId() == R.id.nav_projects) {
            navController.navigate(R.id.action_nav_projects_to_nav_main);
        } else if (navController.getCurrentDestination().getId() == R.id.nav_tasks) {
            navController.navigate(R.id.action_nav_tasks_to_nav_main);
        }
    }

    public void getUserInformation(ValueEventListener listener) {
        firebaseService.getUser(firebaseUser.getUid(), listener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
