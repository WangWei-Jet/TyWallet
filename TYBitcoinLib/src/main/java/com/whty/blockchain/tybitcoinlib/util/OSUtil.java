package com.whty.blockchain.tybitcoinlib.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class OSUtil {

    private static OSUtil instance = null;
    private static Context context = null;

    private OSUtil() {
    }

    public static OSUtil getInstance(Context ctx) {
        context = ctx;
        if (instance == null) {
            instance = new OSUtil();
        }
        return instance;
    }

    public boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo s : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(s.service.getClassName())) {
                return true;
            }
        }

        return false;
    }
}
