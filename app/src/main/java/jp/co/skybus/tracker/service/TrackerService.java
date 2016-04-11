package jp.co.skybus.tracker.service;

import android.app.Notification;
import android.app.NotificationManager;
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

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.skybus.tracker.CONST;
import jp.co.skybus.tracker.MyApp;
import jp.co.skybus.tracker.R;
import jp.co.skybus.tracker.activity.MainActivity;
import jp.co.skybus.tracker.api.Api;
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

    private static final int NOTIFICATION_ID = 221;

    private Location mCurrentLocation;
    private TrackerBinder binder = new TrackerBinder();
    private Intent mBatteryStatus;
    private int mCurrentPeriod = 15000;

    private SendingTimerTask mTimerTask = new SendingTimerTask();
    private Timer mTimer = new Timer();

    private int mUsedSatellites;
    private String mIme;

    private LocationManager mLocationManager;

    private boolean isHasCachedData;
    private boolean isHasError;

    private Info mLastInfo = new Info();

    @Override
    public void onCreate() {
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.addGpsStatusListener(this);

        startUpdateLocation();

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.getDeviceId();
        mIme = telephonyManager.getDeviceId();
        startForeground(NOTIFICATION_ID, getNotification(false));
    }

    public void startUpdateLocation() {
        stopUpdateLocation();

        if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000,
                    PrefsHelper.getInstance().getLocChangeThreshold(), this);
        }

        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                    PrefsHelper.getInstance().getLocChangeThreshold(), this);
        }
    }

    private void stopUpdateLocation() {
        mLocationManager.removeUpdates(this);
    }

    private Notification getNotification(boolean pIsError) {
        return getNotification(pIsError, "");
    }

    private Notification getNotification(boolean isError, String pMessageIfError) {
        int titleId = isError ? R.string.error_notification_title : R.string.default_notification_title;
        String text = isError ? pMessageIfError : "";
        int colorId = isError ? R.color.color_icon_error : R.color.color_icon_default;
        int iconId = isError ? R.drawable.ic_gps_error : R.drawable.ic_gps;
        Notification notification;
        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(MyApp.getStringFromRes(titleId))
                .setContentText(text)
                .setSmallIcon(iconId);
        if (Build.VERSION.SDK_INT >= 21) builder.setColor(MyApp.getColorFromRes(colorId));

        if (Build.VERSION.SDK_INT < 16)
            notification = builder.getNotification();
        else
            notification = builder.build();
        return notification;
    }

    private void updateNotification(boolean pIsError) {
        updateNotification(pIsError, "");
    }

    private void updateNotification(boolean pIsError, String pMessageIfError) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, getNotification(pIsError, pMessageIfError));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startSendingTask();
        return START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        int mNeededPeriod = mCurrentLocation.getSpeed() < CONST.FAST_PERIOD_UPDATE_SPEED ?
                PrefsHelper.getInstance().getSendingInterval() : CONST.FAST_COLLECT_DATA_INTERVAL;
        if (mCurrentPeriod != mNeededPeriod) {
            mCurrentPeriod = mNeededPeriod;
            startSendingTask();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

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

    private void startSendingTask() {
        stopSendingTask();
        mTimerTask = new SendingTimerTask();
        mTimer.schedule(mTimerTask, mCurrentPeriod, mCurrentPeriod);
    }

    private void stopSendingTask() {
        mTimerTask.cancel();
    }

    public void restartUpdates() {
        startUpdateLocation();
        startSendingTask();
    }

    private void tryToSend(Info pInfo) {
        final List<Info> sendingList = new ArrayList<>();
        sendingList.add(pInfo);

        Api.sendData(sendingList, new Callback<DefaultResponseWrapper>() {
                    @Override
                    public void success(DefaultResponseWrapper defaultResponseWrapper, Response response) {
                        if (isHasCachedData) sendCachedData();
                        if (isHasError) {
                            updateNotification(false);
                            isHasError = false;
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DefaultResponseWrapper response = null;
                        String exceptionError = null;
                        try {
                            response = (DefaultResponseWrapper) error.getBody();
                            if (response != null && response.getCode() == CONST.FORBIDDEN_ERROR_CODE) {
                                stopUpdateLocation();
                                stopSendingTask();
                                openMainActivity();
                            } else {
                                Info.saveAll(sendingList);
                                isHasCachedData = true;
                            }
                        } catch (Exception e) {
                            exceptionError = error.getMessage();
                            Crashlytics.log(exceptionError);
                        }
                        String errorMessage;
                        if (response != null) {
                            errorMessage = response.getMessage();
                        } else if (!Utilities.isNetworkAvailable()) {
                            errorMessage = MyApp.getStringFromRes(R.string.network_error);
                        } else if (exceptionError != null) {
                            errorMessage = exceptionError;
                        } else errorMessage = MyApp.getStringFromRes(R.string.unknown_error);
                        isHasError = true;
                        updateNotification(true, errorMessage);
                    }
                }

        );
    }

    private void sendCachedData() {
        final List<Info> sendingList = new ArrayList<>();
        sendingList.addAll(Info.getAll());
        if (sendingList.isEmpty()) {
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

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private Info generateInfo() {
        Info info = new Info();
        info.setIme(mIme);
        info.setTime(System.currentTimeMillis());
        info.setLat(mCurrentLocation != null ? mCurrentLocation.getLatitude() : 0);
        info.setLng(mCurrentLocation != null ? mCurrentLocation.getLongitude() : 0);

        mBatteryStatus = getApplicationContext().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int status = mBatteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        int level = mBatteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = mBatteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPercent = level / (float) scale;
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        info.setCharge(isCharging);
        info.setBattery(batteryPercent);
        info.setSpeed(mCurrentLocation != null ? mCurrentLocation.getSpeed() : 0);
        info.setAccuracy(mCurrentLocation != null ? mCurrentLocation.getAccuracy() : 0);
        info.setProvider(mCurrentLocation != null ? mCurrentLocation.getProvider() : "none");

        if (mCurrentLocation == null ||
                mCurrentLocation.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
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
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
    }

    private class SendingTimerTask extends TimerTask {

        @Override
        public void run() {
            tryToSend(generateInfo());
        }
    }

}
