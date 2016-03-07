package jp.co.skybus.tracker.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.skybus.tracker.CONST;
import jp.co.skybus.tracker.R;
import jp.co.skybus.tracker.api.Api;
import jp.co.skybus.tracker.helper.Logger;
import jp.co.skybus.tracker.helper.PrefsHelper;
import jp.co.skybus.tracker.helper.Utilities;
import jp.co.skybus.tracker.model.DefaultResponseWrapper;
import jp.co.skybus.tracker.model.Info;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Roman T. on 06.03.2016.
 */
public class TrackerService extends Service implements LocationListener, GpsStatus.Listener {

    private Location mCurrentLocation;
    private TrackerBinder binder = new TrackerBinder();
    private Intent mBatteryStatus;
    private int mCurrentPeriod = 1000;

    private AddInfoTimerTask mTimerTask = new AddInfoTimerTask();
    private Timer mTimer = new Timer();

    private int mUsedSatellites;
    private String mIme;

    private LocationManager mLocationManager;

    private boolean isHasCachedData;

    private Info mLastInfo = new Info();

    @Override
    public void onCreate() {
        Logger.d("Service: onCreate");

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.addGpsStatusListener(this);

        addUpdateRequests();

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.getDeviceId();
        mIme = telephonyManager.getDeviceId();

        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_gps);
        Notification notification;
        if (Build.VERSION.SDK_INT < 16)
            notification = builder.getNotification();
        else
            notification = builder.build();
        startForeground(221, notification);
    }

    public void addUpdateRequests() {
        Logger.d("Tracker service: addUpdateRequests()");
        mLocationManager.removeUpdates(this);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000,
                PrefsHelper.getInstance().getLocChangeThreshold(), this);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                PrefsHelper.getInstance().getLocChangeThreshold(), this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d("Service: onStartCommand");
        startUpdate();
        return START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        int mNeededPeriod = mCurrentLocation.getSpeed() < CONST.FAST_PERIOD_UPDATE_SPEED ?
                PrefsHelper.getInstance().getSendingInterval() : CONST.FAST_COLLECT_DATA_INTERVAL;
        if (mCurrentPeriod != mNeededPeriod) {
            mCurrentPeriod = mNeededPeriod;
            startUpdate();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}

    @Override
    public void onGpsStatusChanged(int i) {
        GpsStatus status = mLocationManager.getGpsStatus(null);
        Iterable<GpsSatellite> sats = status.getSatellites();
        int countInUse = 0;
        if (sats != null) {
            for (GpsSatellite gpsSatellite : sats) {
                if (gpsSatellite.usedInFix()) {
                    countInUse++;
                }
            }
        }
        mUsedSatellites = countInUse;
    }

    public class TrackerBinder extends Binder {
        public TrackerService getService() {
            return TrackerService.this;
        }
    }

    private void startUpdate(){
        mTimerTask.cancel();
        mTimerTask = new AddInfoTimerTask();
        Logger.d("TrackerService: send_info_period - " + String.valueOf(mCurrentPeriod));
        mTimer.schedule(mTimerTask, mCurrentPeriod, mCurrentPeriod);
    }

    private void tryToSend(Info pInfo){
        final List<Info> sendingList = new ArrayList<>();
        sendingList.add(pInfo);

        Api.sendData(sendingList, new Callback<DefaultResponseWrapper>() {
            @Override
            public void success(DefaultResponseWrapper defaultResponseWrapper, Response response) {
                Logger.d("Retrofit success");
                if (isHasCachedData) sendCachedData();
            }

            @Override
            public void failure(RetrofitError error) {
                Logger.d("Retrofit failure");
                Info.saveAll(sendingList);
                isHasCachedData = true;
            }
        });
    }

    private void sendCachedData(){
        final List<Info> sendingList = new ArrayList<>();
        sendingList.addAll(Info.getAll());
        if (sendingList.isEmpty()){
            isHasCachedData = false;
            return;
        }
        Api.sendData(sendingList, new Callback<DefaultResponseWrapper>() {
            @Override
            public void success(DefaultResponseWrapper defaultResponseWrapper, Response response) {
                Info.deleteAll(sendingList);
                if (Info.getAll().isEmpty()) isHasCachedData = false;
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    private Info generateInfo() {
        Info info = new Info();
        info.setIme(mIme);
        info.setTime(mCurrentLocation != null ? mCurrentLocation.getTime() :
                Utilities.getCurrentTimestamp());
        info.setLat(mCurrentLocation != null ? mCurrentLocation.getLatitude() : 0);
        info.setLng(mCurrentLocation != null ? mCurrentLocation.getLongitude() : 0);

        mBatteryStatus = getApplicationContext().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int status = mBatteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        int level = mBatteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = mBatteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPercent = level / (float)scale;
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        info.setCharge(isCharging);
        info.setBattery(batteryPercent);
        info.setSpeed(mCurrentLocation != null ? mCurrentLocation.getSpeed() : 0);
        info.setAccuracy(mCurrentLocation != null ? mCurrentLocation.getAccuracy() : 0);
        info.setProvider(mCurrentLocation != null ? mCurrentLocation.getProvider() : "none");

        if (mCurrentLocation == null ||
                mCurrentLocation.getProvider().equals(LocationManager.NETWORK_PROVIDER)){
            info.setSat(0);
        } else info.setSat(mUsedSatellites);
        mLastInfo = info;
        return info;
    }

    public Info getLastInfo() {
        return mLastInfo;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Logger.d("Service: onBind");
        return binder;
    }

    public boolean onUnbind(Intent intent) {
        Logger.d("Service: onUnbind");
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d("Service: onDestroy");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Logger.d("Service: onTaskRemoved");
    }

    private class AddInfoTimerTask extends TimerTask {

        @Override
        public void run() {
            Logger.d("TrackerService: try to send");
            tryToSend(generateInfo());
        }
    }

}
