package jp.co.skybus.tracker;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.skybus.tracker.helper.Logger;
import jp.co.skybus.tracker.helper.Utilities;
import jp.co.skybus.tracker.model.Info;
import jp.co.skybus.tracker.service.TrackerService;

public class MainActivity extends AppCompatActivity {

    private TextView mImeiTv;
    private TextView mTimestampTv;
    private TextView mDateTimeTv;
    private TextView mLatTv;
    private TextView mLngTv;
    private TextView mSatTv, mSatUsedTv;
    private TextView mSpeedTv;
    private TextView mBatteryTv;
    private TextView mChargingTv;
    private TextView mUpdateTv;
    private TextView mProviderTv;
    private Timer mTimer = new Timer();
    private AddInfoTimerTask mTimerTask = new AddInfoTimerTask();

    private Info mLastInfo;

    private int mCurrentPeriod = CONST.FAST_COLLECT_DATA_INTERVAL;

    private long lastTimeStamp;

    private ServiceConnection mServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent serviceIntent = new Intent(this, TrackerService.class);

        if (!Utilities.isServiceRunning()) startService(serviceIntent);

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Logger.d("MainActivity onServiceConnected");
                mLastInfo = ((TrackerService.TrackerBinder) iBinder).getService().getLastInfo();
                startUpdate();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Logger.d("MainActivity onServiceDisconnected");
            }
        };
        bindService(serviceIntent, mServiceConnection, BIND_AUTO_CREATE);

        initFields();
    }

    private void startUpdate(){
        mTimerTask.cancel();
        mTimerTask = new AddInfoTimerTask();
        Logger.d(String.valueOf(mCurrentPeriod));
        mTimer.schedule(mTimerTask, 0, CONST.FAST_COLLECT_DATA_INTERVAL);
    }

    private void initFields(){
        mImeiTv = (TextView) findViewById(R.id.imei_tv);
        mTimestampTv = (TextView) findViewById(R.id.timestamp_tv);
        mDateTimeTv = (TextView) findViewById(R.id.datetime_tv);
        mLatTv = (TextView) findViewById(R.id.latitude_tv);
        mLngTv = (TextView) findViewById(R.id.longitude_tv);
        mSatTv = (TextView) findViewById(R.id.satellites_tv);
        mSatUsedTv = (TextView) findViewById(R.id.satellites_used_tv);
        mSpeedTv = (TextView) findViewById(R.id.speed_tv);
        mBatteryTv = (TextView) findViewById(R.id.battery_tv);
        mChargingTv = (TextView) findViewById(R.id.charging_tv);
        mUpdateTv = (TextView) findViewById(R.id.updatetime_tv);
        mProviderTv = (TextView) findViewById(R.id.provider_tv);
    }

    private void updateUI() {
        mLatTv.setText(String.valueOf(mLastInfo.getLat()));
        mLngTv.setText(String.valueOf(mLastInfo.getLng()));
        mTimestampTv.setText(String.valueOf(mLastInfo.getTime() / 1000));
        mDateTimeTv.setText(getDateTime(mLastInfo.getTime()));
        mProviderTv.setText(mLastInfo.getProvider());
        mSpeedTv.setText(String.valueOf((int) (mLastInfo.getSpeed()*3.6f)));
        mBatteryTv.setText(String.valueOf(mLastInfo.getBattery()));
        mChargingTv.setText(String.valueOf(mLastInfo.isCharge()));
        mUpdateTv.setText(String.valueOf((mLastInfo.getTime()/1000) - lastTimeStamp));
        lastTimeStamp = mLastInfo.getTime()/1000;
        mSatUsedTv.setText(String.valueOf(mLastInfo.getSat()));
    }

    private String getDateTime(long pTimestamp){
        try{
            DateFormat sdf = new SimpleDateFormat("dd.MM.yyyy kk:mm:ss");
            Date netDate = (new Date(pTimestamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(mServiceConnection);
        super.onDestroy();
    }

    private class AddInfoTimerTask extends TimerTask {

        @Override
        public void run() {
            updateUI();
        }
    }
}
