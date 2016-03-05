package jp.co.skybus.tracker.helper;

import android.util.Log;

/**
 * Created by Roman T. on 05.03.2016.
 */
public class Logger {
    public static final String LOG_TAG = "LOCTRACKER";
    public static final boolean IS_DEBUG = true;

    @SuppressWarnings("unused")
    public static void e(String msg) {
        Log.e(LOG_TAG, msg);
    }

    @SuppressWarnings("unused")
    public static void e(String msg, Throwable tr) {
        Log.e(LOG_TAG, msg, tr);
    }

    @SuppressWarnings("unused")
    public static int d(String msg) {
        if (IS_DEBUG) return Log.d(LOG_TAG, msg);

        return 0;
    }

    @SuppressWarnings("unused")
    public static int d(String msg, Throwable tr) {
        if (IS_DEBUG)
            return Log.d(LOG_TAG, msg, tr);

        return 0;
    }

    @SuppressWarnings("unused")
    public static int v(String msg) {
        if (IS_DEBUG)
            return Log.v(LOG_TAG, msg);

        return 0;
    }

    @SuppressWarnings("unused")
    public static int v(String msg, Throwable tr) {
        if (IS_DEBUG)
            return Log.v(LOG_TAG, msg, tr);

        return 0;
    }

    @SuppressWarnings("unused")
    public static int i(String msg) {
        if (IS_DEBUG)
            return Log.i(LOG_TAG, msg);

        return 0;
    }

    @SuppressWarnings("unused")
    public static int i(String msg, Throwable tr) {
        if (IS_DEBUG)
            return Log.i(LOG_TAG, msg, tr);

        return 0;
    }

    @SuppressWarnings("unused")
    public static int w(String msg) {
        if (IS_DEBUG)
            return Log.w(LOG_TAG, msg);

        return 0;
    }

    @SuppressWarnings("unused")
    public static int w(String msg, Throwable tr) {
        if (IS_DEBUG)
            return Log.w(LOG_TAG, msg, tr);

        return 0;
    }


    @SuppressWarnings("unused")
    public static int w(Throwable tr) {
        if (IS_DEBUG)
            return Log.w(LOG_TAG, tr);

        return 0;
    }

}
