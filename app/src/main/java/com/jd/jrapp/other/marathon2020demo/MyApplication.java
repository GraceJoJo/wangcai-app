package com.jd.jrapp.other.marathon2020demo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.jd.jrapp.other.pet.BuildConfig;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Author: chenghuan15
 * Date: 2020/9/9
 * Time: 10:42 AM
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "7072d63ee8", true);
        CrashReport.setAppVersion(getApplicationContext(), BuildConfig.VERSION_NAME);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
