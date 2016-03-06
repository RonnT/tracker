package jp.co.skybus.tracker.helper;

import android.content.Context;
import android.content.SharedPreferences;

import jp.co.skybus.tracker.CONST;
import jp.co.skybus.tracker.MyApp;

/**
 * Created by Roman T. on 05.03.2016.
 */
public class PrefsHelper {

    private static class SingletonHolder {
        private static PrefsHelper INSTANCE;
    }

    public static PrefsHelper getInstance() {
        if (SingletonHolder.INSTANCE == null) {
            SingletonHolder.INSTANCE = new PrefsHelper(MyApp.getAppContext());
        }
        return SingletonHolder.INSTANCE;
    }

    //----------------------------------------------------------------

    private static final String
            PREFS_NAME  = "PREFS_NAME",

            SERVER_ADDR = "SERVER_ADDR",
            LOC_THRESHOLD = "LOC_THRESHOLD",
            BAT_THRESHOLD = "BAT_THRESHOLD",
            SEND_INTERVAL = "SEND_INTERVAL";

    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;

    private PrefsHelper(Context pContext) {
        mPrefs = pContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private void putString(String name, String value) {
        mEditor = mPrefs.edit();
        mEditor.putString(name, value);

        if (!mEditor.commit()) Logger.e("Can't commit putString(" + name + ", " + value + ")");
    }

    private void putLong(String name, long value) {
        mEditor = mPrefs.edit();
        mEditor.putLong(name, value);

        if (!mEditor.commit()) Logger.e("Can't commit putLong(" + name + ", " + value + ")");
    }

    private void putBoolean(String name, boolean value) {
        mEditor = mPrefs.edit();
        mEditor.putBoolean(name, value);

        if (!mEditor.commit()) Logger.e("Can't commit putBoolean(" + name + ", " + value + ")");
    }

    private boolean getBoolean(String pKey, boolean pDefValue) {
        return mPrefs.getBoolean(pKey, pDefValue);
    }

    private int getInt(String pKey, int pDefValue) {
        return mPrefs.getInt(pKey, pDefValue);
    }

    private String getString(String pKey, String pDefValue) {
        return mPrefs.getString(pKey, pDefValue);
    }

    private long getLong(String pKey, long pDefValue) {
        return mPrefs.getLong(pKey, pDefValue);
    }

    private void putInt(String name, int value) {
        mEditor = mPrefs.edit();
        mEditor.putInt(name, value);
        if (!mEditor.commit()) Logger.e("Can't commit putInt(" + name + ", " + value + ")");
    }

    public void setServerAddress(String  pAddress) {
        putString(SERVER_ADDR, pAddress);
    }

    public String getServerAddress() {
        return getString(SERVER_ADDR, CONST.DEFAULT_SERVER_ADDRESS);
    }

    public void setLocChangeThreshold(int pMeters){
        putInt(LOC_THRESHOLD, pMeters);
    }

    public int getLocChangeThreshold(){
        return getInt(LOC_THRESHOLD, CONST.DEFAULT_LOCATION_CHANGE_THRESHOLD);
    }

    public void setBatteryChangeThreshold(int pPercent){
        putInt(BAT_THRESHOLD, pPercent);
    }

    public int getBatteryChangeThreshold(){
        return getInt(BAT_THRESHOLD, CONST.DEFAULT_BATTERY_CHANGE_THRESHOLD);
    }

    public void setSendingInterval(int pSeconds){
        putInt(SEND_INTERVAL, pSeconds);
    }

    public int getSendingInterval(){
        return getInt(SEND_INTERVAL, CONST.DEFAULT_SENDING_DATA_INTERVAL);
    }
}

