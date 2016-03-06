package jp.co.skybus.tracker.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

import jp.co.skybus.tracker.R;
import jp.co.skybus.tracker.helper.Logger;

/**
 * Created by Roman T. on 06.03.2016.
 */
public class TrackerService extends Service {

    private Location mCurrentLocation;
    private TrackerBinder binder = new TrackerBinder();

    private int mCurrentPeriod = 1000;

    private AddInfoTimerTask mTimerTask = new AddInfoTimerTask();
    private Timer mTimer = new Timer();

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
    public void onCreate() {
        Logger.d("Service: onCreate");
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_gps);
        Notification notification;
        if (Build.VERSION.SDK_INT < 16)
            notification = builder.getNotification();
        else
            notification = builder.build();
        startForeground(221, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d("Service: onStartCommand");
        startUpdate();
        return START_STICKY;
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

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    public class TrackerBinder extends Binder {
        public TrackerService getService() {
            return TrackerService.this;
        }
    }

    private void startUpdate(){
        mTimerTask.cancel();
        mTimerTask = new AddInfoTimerTask();
        Logger.d("TrackerService: measurementPeriod - " + String.valueOf(mCurrentPeriod));
        mTimer.schedule(mTimerTask, mCurrentPeriod, mCurrentPeriod);
    }

    private class AddInfoTimerTask extends TimerTask {

        @Override
        public void run() {
            Logger.d("TrackerService measurement timer task");
            //mInfoList.add(generateInfo());
            /*runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast();
                }
            });*/
        }
    }

}
