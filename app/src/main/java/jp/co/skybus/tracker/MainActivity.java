package jp.co.skybus.tracker;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
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
    private TextView mSatUsedTv;
    private TextView mSpeedTv;
    private TextView mBatteryTv;
    private TextView mChargingTv;
    private TextView mUpdateTv;
    private TextView mProviderTv;
    private Timer mTimer = new Timer();
    private AddInfoTimerTask mTimerTask = new AddInfoTimerTask();
    private TrackerService mService;
    private Info mLastInfo;
    private boolean isBinded;
    private Intent mServiceIntent;

    private int mCurrentPeriod = CONST.FAST_COLLECT_DATA_INTERVAL;

    private long lastTimeStamp;

    private ServiceConnection mServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mServiceIntent = new Intent(this, TrackerService.class);

        if (!Utilities.isServiceRunning()) startService(mServiceIntent);

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Logger.d("MainActivity onServiceConnected");
                mService = ((TrackerService.TrackerBinder) iBinder).getService();
                startUpdate();
                isBinded = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Logger.d("MainActivity onServiceDisconnected");
                isBinded = false;
                mTimerTask.cancel();
            }
        };
        initFields();
    }

    private void startUpdate(){
        mTimerTask.cancel();
        mTimerTask = new AddInfoTimerTask();
        Logger.d(String.valueOf(mCurrentPeriod));
        mTimer.schedule(mTimerTask, 0, 500);
    }

    private void initFields(){
        mImeiTv = (TextView) findViewById(R.id.imei_tv);
        mTimestampTv = (TextView) findViewById(R.id.timestamp_tv);
        mDateTimeTv = (TextView) findViewById(R.id.datetime_tv);
        mLatTv = (TextView) findViewById(R.id.latitude_tv);
        mLngTv = (TextView) findViewById(R.id.longitude_tv);
        mSatUsedTv = (TextView) findViewById(R.id.satellites_used_tv);
        mSpeedTv = (TextView) findViewById(R.id.speed_tv);
        mBatteryTv = (TextView) findViewById(R.id.battery_tv);
        mChargingTv = (TextView) findViewById(R.id.charging_tv);
        mUpdateTv = (TextView) findViewById(R.id.updatetime_tv);
        mProviderTv = (TextView) findViewById(R.id.provider_tv);
    }

    private void updateUI() {
        mImeiTv.setText(mLastInfo.getIme());
        mLatTv.setText(String.valueOf(mLastInfo.getLat()));
        mLngTv.setText(String.valueOf(mLastInfo.getLng()));
        mTimestampTv.setText(String.valueOf(mLastInfo.getTime() / 1000));
        mDateTimeTv.setText(getDateTime(mLastInfo.getTime()));
        mProviderTv.setText(mLastInfo.getProvider());
        mSpeedTv.setText(String.valueOf((int) (mLastInfo.getSpeed() * 3.6f)));
        mBatteryTv.setText(String.valueOf(mLastInfo.getBattery()));
        mChargingTv.setText(String.valueOf(mLastInfo.isCharge()));
        if (lastTimeStamp != mLastInfo.getTime()){
            mUpdateTv.setText(String.valueOf(((mLastInfo.getTime()) - lastTimeStamp)/1000));
            lastTimeStamp = mLastInfo.getTime();
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(mServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isBinded) {
            unbindService(mServiceConnection);
            mTimerTask.cancel();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class AddInfoTimerTask extends TimerTask {

        @Override
        public void run() {
            mLastInfo = mService.getLastInfo();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateUI();
                }
            });
        }
    }
}
