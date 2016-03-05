package jp.co.skybus.tracker;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.skybus.tracker.helper.PrefsHelper;
import jp.co.skybus.tracker.helper.Utilities;
import jp.co.skybus.tracker.model.Info;

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

    private String mImei;
    private boolean mIsCharging;
    private int mBatPercentage;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private Intent mBatteryStatus;
    private IntentFilter ifilter;
    private Timer mTimer = new Timer();
    private AddInfoTimerTask mTimerTask;
    private SendDataTimerTask mDataTimerTask;

    private long lastTimeStamp;

    private List<Info> mInfoList = new ArrayList<>();

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
        mTimerTask = new AddInfoTimerTask();
        mTimer.schedule(mTimerTask, 0, (3000));

        mDataTimerTask = new SendDataTimerTask();
        mTimer.schedule(mDataTimerTask, 0, (PrefsHelper.getInstance().getSendingInterval()*1000));
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
        mImei = telephonyManager.getDeviceId();
        mImeiTv.setText(mImei);
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
        mTimestampTv.setText(String.valueOf(mCurrentLocation.getTime()/1000));
        mDateTimeTv.setText(getDateTime(mCurrentLocation.getTime()));
        mProviderTv.setText(mCurrentLocation.getProvider());
        mSpeedTv.setText(String.valueOf((int) mCurrentLocation.getSpeed()));

        mBatteryStatus = getApplicationContext().registerReceiver(null, ifilter);
        int status = mBatteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        int level = mBatteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = mBatteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float)scale;
        mBatPercentage = (int) (batteryPct * 100);

        mBatteryTv.setText(String.valueOf(mBatPercentage));
        mIsCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        mChargingTv.setText(String.valueOf(mIsCharging));
        mUpdateTv.setText(String.valueOf((mCurrentLocation.getTime()/1000) - lastTimeStamp));
        lastTimeStamp = mCurrentLocation.getTime()/1000;
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

    private Info generateInfo(){
        Info info = new Info();
        info.setImei(mImei);
        info.setTimestamp(mCurrentLocation != null ? mCurrentLocation.getTime()/1000 :
                Utilities.getCurrentTimestamp());
        info.setLat(mCurrentLocation != null ? mCurrentLocation.getLatitude() : 0);
        info.setLng(mCurrentLocation != null ? mCurrentLocation.getLongitude() : 0);
        info.setCharging(mIsCharging);
        info.setBattery(mBatPercentage);
        info.setSpeed(mCurrentLocation != null ? mCurrentLocation.getSpeed() : 0);
        return info;
    }

    private void showToast(){
        Toast.makeText(MainActivity.this, String.valueOf(mInfoList.size()), Toast.LENGTH_SHORT).show();
    }

    private class SendDataTimerTask extends TimerTask{

        @Override
        public void run() {
            List<Info> tempList = new ArrayList<>();
            tempList.addAll(mInfoList);
            mInfoList.removeAll(tempList);
            Info.saveAll(tempList);
        }
    }

    private class AddInfoTimerTask extends TimerTask {

        @Override
        public void run() {
            mInfoList.add(generateInfo());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast();
                }
            });
        }
    }
}
