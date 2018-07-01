package com.journalapp.michaelsinkolongo.journalapp.utilities;

import android.util.Log;

public abstract class Logger {
    public static final String DEGUG = "debug";
    public static final String ERROR = "error";
    private static final boolean DEBUG_ENABLED = false;
    private static final boolean ERROR_ENABLED = true;
    public static void printLog(String tag,String message, String type){
        if(DEBUG_ENABLED) {
            if (type.equals(DEGUG)) {
                Log.d(tag, message);

            }
        }
        if(ERROR_ENABLED) {
            if (type.equals(ERROR)) {
                Log.e(tag, message);
            }
        }

    }
}
