package com.bayraktar.scrum.manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Emre BAYRAKTAR on 5/2/2021.
 */
public class PrefManager {
    // Shared preferences file name
    private static final String PREF_NAME = "_AppPref";

    private final SharedPreferences pref;

    private static final String IS_FIRST_TIME_LAUNCH = "_IS_FIRST_TIME_LAUNCH";
    private static final String IS_SHOWN_MAIN_PROJECT_TOOLTIP = "_IS_SHOWN_MAIN_PROJECT_TOOLTIP";
    private static final String IS_SHOWN_CREATE_PROJECT_TOOLTIP = "_IS_SHOWN_CREATE_PROJECT_TOOLTIP";
    private static final int PRIVATE_MODE = 0;

    public PrefManager(Context context) {
        // shared pref mode
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        pref.edit()
                .putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime)
                .apply();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setMainProjectTooltip(boolean isFirstTime) {
        pref.edit()
                .putBoolean(IS_SHOWN_MAIN_PROJECT_TOOLTIP, isFirstTime)
                .apply();
    }

    public boolean isShownMainProjectTooltip() {
        return pref.getBoolean(IS_SHOWN_MAIN_PROJECT_TOOLTIP, false);
    }

    public void setShownCreateProjectTooltip(boolean isFirstTime) {
        pref.edit()
                .putBoolean(IS_SHOWN_CREATE_PROJECT_TOOLTIP, isFirstTime)
                .apply();
    }

    public boolean isShownCreateProjectTooltip() {
        return pref.getBoolean(IS_SHOWN_CREATE_PROJECT_TOOLTIP, false);
    }
}
