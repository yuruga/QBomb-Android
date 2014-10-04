package jp.yuruga.qbomb.common;

import android.util.Log;

/**
 * Created by ichikawa on 2014/09/17.
 */
public class Share {
    public static void log(String message){
        Log.d(getCallerClassName(), message);
    }
    public static String getCallerClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i=1; i<stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(Share.class.getName()) && ste.getClassName().indexOf("java.lang.Thread")!=0) {
                return ste.getClassName();
            }
        }
        return null;
    }
}