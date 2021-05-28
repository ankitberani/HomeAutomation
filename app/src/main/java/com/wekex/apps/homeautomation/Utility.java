package com.wekex.apps.homeautomation;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utility {

    public  SharedPreferences sharedpreferences;
    static SharedPreferences.Editor editor;
    private static final String PREFERENCES_NAME = "homeAutomationPrefs";
    private static final String IS_FRESH         = "isLoggedIn";

    public Utility(Context context) {
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedpreferences.edit();
    }

    public  void putString(String key, String value) {
        editor.putString(key, value).apply();
        editor.commit();
    }
    public  void putInteger(String key, Integer value) {
        editor.putInt(key, value).apply();
        editor.commit();
    }

    public  Integer getInteger(String key) {
        return sharedpreferences.getInt(key, 0);
    }
    public  String getString(String key) {
        return sharedpreferences.getString(key, "");
    }

    public  void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value).apply();
        editor.commit();
    }
    public  boolean getBoolean(String key, boolean defaultValue) {
        return sharedpreferences.getBoolean(key, defaultValue);
    }

    //Context
    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    //Session
    public static void setIsFresh(Context context, boolean value) {
        SharedPreferences.Editor prefs = Utility.getPreferences(context).edit();
        prefs.putBoolean(IS_FRESH, value);
        prefs.apply();
    }
    public static boolean getIsFresh(Context context) {
        return Utility.getPreferences(context).getBoolean(IS_FRESH, true);
    }
}
