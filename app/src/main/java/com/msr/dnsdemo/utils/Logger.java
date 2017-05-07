package com.msr.dnsdemo.utils;

import android.util.Log;

/**
 * Created by SANDEEP on 06-05-2017.
 */

public class Logger {
    private static final String TAG = "====DNS Demo===";

    public static void getInfo(String message) {
        Log.i(TAG, "===" + message + "===");
    }

    public static void getWarn(String message) {
        Log.w(TAG, "===" + message + "===");
    }

    public static void getError(String message) {
        Log.e(TAG, "===" + message + "===");
    }
}
