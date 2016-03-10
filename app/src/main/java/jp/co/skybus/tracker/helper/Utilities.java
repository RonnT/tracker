package jp.co.skybus.tracker.helper;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import jp.co.skybus.tracker.MyApp;
import jp.co.skybus.tracker.service.TrackerService;

/**
 * Created by Roman T. on 05.03.2016.
 */
public class Utilities {

    public static final int TIMESTAMP_MULTIPLIER = 1000;

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) MyApp.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) MyApp.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TrackerService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
