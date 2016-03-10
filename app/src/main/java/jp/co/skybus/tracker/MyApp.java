package jp.co.skybus.tracker;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import jp.co.skybus.tracker.helper.DatabaseHelper;

public class MyApp extends Application {

    private static Context sContext;
    private static DatabaseHelper sDatabaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());
        //Fabric.with(this, new Crashlytics());
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
