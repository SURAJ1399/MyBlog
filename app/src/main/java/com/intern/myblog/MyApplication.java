package com.intern.myblog;

import android.app.Application;

import com.onesignal.OneSignal;

public class MyApplication extends Application {


    public static MyApplication instance;

    public MyApplication(){

        instance = this;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;
        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

    }


    public static synchronized MyApplication getInstance(){

        return  instance;
    }

}
