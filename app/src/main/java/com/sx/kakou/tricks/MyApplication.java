package com.sx.kakou.tricks;

import android.app.Application;

import com.nrs.utils.tools.CrashHandler;

/**
 * Created by mglory on 2015/10/9.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }
}
