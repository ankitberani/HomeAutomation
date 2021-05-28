package com.wekex.apps.homeautomation.utils;

import android.app.Application;

import com.onesignal.OneSignal;
//import com.onesignal.OneSignal;
//import com.onesignal.OneSignal.OSInFocusDisplayOption;

public class ApplicationClass extends Application {
    String TAG = "ApplicationClass";

    private static final String ONESIGNAL_APP_ID = "ea84fc65-2dcc-493b-9423-fd4c1a3da63c";

    public void onCreate() {
        super.onCreate();
//        OneSignal.startInit(this).inFocusDisplaying(OSInFocusDisplayOption.Notification).unsubscribeWhenNotificationsAreDisabled(true).init();

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
    }
}
