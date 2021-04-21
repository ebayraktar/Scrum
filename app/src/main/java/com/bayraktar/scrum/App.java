package com.bayraktar.scrum;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;

import com.bayraktar.scrum.manager.PrefManager;
import com.bayraktar.scrum.model.User;
import com.bayraktar.scrum.service.FirebaseService;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileNotFoundException;

import javax.inject.Singleton;

public class App extends Application {

    public static final String CHANNEL_1_ID = "channel1";
    public static FirebaseAuth firebaseAuth;
    public static FirebaseUser firebaseUser;
    public static FirebaseDatabase firebaseDatabase;
    public static FirebaseService firebaseService;
    public static User currentUser;
    public static FirebaseAnalytics mFirebaseAnalytics;
    @Singleton
    private static PrefManager prefManager;

    public static PrefManager getPrefManager() {
        return prefManager;
    }


    public App() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseService = new FirebaseService();
        createNotificationChannel();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        prefManager = new PrefManager(getApplicationContext());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Bildirim Kanalı",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription(getString(R.string.notification_channel));

            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(channel1);
        }
    }


    public static Bitmap decodeUri(Context c, Uri uri, final int requiredSize)
            throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o);

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (width_tmp / 2 >= requiredSize && height_tmp / 2 >= requiredSize) {
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o2);
    }
}
