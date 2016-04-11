package jp.co.skybus.tracker;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import io.fabric.sdk.android.Fabric;
import jp.co.skybus.tracker.helper.DatabaseHelper;

public class MyApp extends Application {

    private static Context sContext;
    private static DatabaseHelper sDatabaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        sContext = getApplicationContext();
        sDatabaseHelper = OpenHelperManager.getHelper(sContext, DatabaseHelper.class);
    }

    @Override
    public void onTerminate() {
        sContext = null;
        super.onTerminate();
    }

    public static Context getAppContext() {
        return sContext;
    }

    public static DatabaseHelper getDBHelper(){
        return sDatabaseHelper;
    }

    public static String getStringFromRes(int id) {
        return sContext != null ? sContext.getString(id) : "";
    }

    public static int getColorFromRes(int pId) {
        return getAppContext().getResources().getColor(pId);
    }
}
