package com.wekex.apps.homeautomation.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {

    private static final String PREFERENCES_NAME = "HomeAutomationPrefs";
    private static final String ALL_DEVICES      = "allDevices";
    private static final String USER_ID          = "userID";

    //Context
    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    //All Devices
    public static void setAllDevices(Context context, String value) {
        SharedPreferences.Editor prefs = PreferencesHelper.getPreferences(context).edit();
        prefs.putString(ALL_DEVICES, value);
        prefs.apply();
    }

    public static String getAllDevices(Context context) {
        return PreferencesHelper.getPreferences(context).getString(ALL_DEVICES, null);
    }

    //USER ID
    public static void setUserId(Context context, String value) {
        SharedPreferences.Editor prefs = PreferencesHelper.getPreferences(context).edit();
        prefs.putString(USER_ID, value);
        prefs.apply();
    }

    public static String getUserId(Context context) {
        return PreferencesHelper.getPreferences(context).getString(USER_ID, null);
    }

    //Clear
    public static void clearData(Context context) {
        SharedPreferences.Editor prefs = PreferencesHelper.getPreferences(context).edit();
        prefs.putString(PREFERENCES_NAME, null);
        prefs.putString(ALL_DEVICES, null);
        prefs.apply();
    }
}
