package jp.co.skybus.tracker;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.BatteryManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener {

    private static final float MIN_DISTANCE = 1;

    private TextView mImeiTv;
    private TextView mTimestampTv;
    private TextView mDateTimeTv;
    private TextView mLatTv;
    private TextView mLngTv;
    private TextView mSatTv;
    private TextView mSpeedTv;
    private TextView mBatteryTv;
    private TextView mChargingTv;
    private TextView mUpdateTv;
    private TextView mProviderTv;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private Intent mBatteryStatus;
    private IntentFilter ifilter;
    private Timer mTimer = new Timer();
    private TrackerTimerTask mTimerTask;

    private long lastTimeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFields();
        setData();
        buildGoogleApiClient();
        createLocationRequest();
        connect();

        ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mTimerTask = new TrackerTimerTask();
        mTimer.schedule(mTimerTask, 0, 3000);
    }

    private void initFields(){
        mImeiTv = (TextView) findViewById(R.id.imei_tv);
        mTimestampTv = (TextView) findViewById(R.id.timestamp_tv);
        mDateTimeTv = (TextView) findViewById(R.id.datetime_tv);
        mLatTv = (TextView) findViewById(R.id.latitude_tv);
        mLngTv = (TextView) findViewById(R.id.longitude_tv);
        mSatTv = (TextView) findViewById(R.id.satellites_tv);
        mSpeedTv = (TextView) findViewById(R.id.speed_tv);
        mBatteryTv = (TextView) findViewById(R.id.battery_tv);
        mChargingTv = (TextView) findViewById(R.id.charging_tv);
        mUpdateTv = (TextView) findViewById(R.id.updatetime_tv);
        mProviderTv = (TextView) findViewById(R.id.provider_tv);

    }

    private void setData(){
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.getDeviceId();
        mImeiTv.setText(telephonyManager.getDeviceId());
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API).build();
    }

    public void connect() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(500);
        mLocationRequest.setFastestInterval(500);
        //mLocationRequest.setSmallestDisplacement(MIN_DISTANCE);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        updateUI();
    }

    private void updateUI() {
        mLatTv.setText(String.valueOf(mCurrentLocation.getLatitude()));
        mLngTv.setText(String.valueOf(mCurrentLocation.getLongitude()));
        mTimestampTv.setText(String.valueOf(mCurrentLocation.getTime()));
        mDateTimeTv.setText(getDateTime(mCurrentLocation.getTime()));
        mProviderTv.setText(mCurrentLocation.getProvider());
        mSpeedTv.setText(String.valueOf((int) mCurrentLocation.getSpeed()));

        mBatteryStatus = getApplicationContext().registerReceiver(null, ifilter);
        int status = mBatteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        int level = mBatteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = mBatteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float)scale;
        int batteryPercentage = (int) (batteryPct * 100);

        mBatteryTv.setText(String.valueOf(batteryPercentage));
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        mChargingTv.setText(isCharging+"");
        mUpdateTv.setText(String.valueOf((mCurrentLocation.getTime() - lastTimeStamp)/1000));
        lastTimeStamp = mCurrentLocation.getTime();
        //mSatTv.setText(mCurrentLocation.getExtras());
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

    private void showToast(){
        String toast = mCurrentLocation != null ? mCurrentLocation.toString(): "null";
        Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
    }

    private class TrackerTimerTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast();
                }
            });
        }
    }
}
