package jp.co.skybus.tracker;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import jp.co.skybus.tracker.api.Api;
import jp.co.skybus.tracker.helper.PrefsHelper;
import jp.co.skybus.tracker.service.TrackerService;

/**
 * Created by Roman T. on 07.03.2016.
 */
public class SettingsActivity extends AppCompatActivity {

    private EditText mServerEt, mLocEt, mTimeEt;
    private ServiceConnection mServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(MyApp.getStringFromRes(R.string.action_settings));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initFields();
        fillFields();
    }

    private void initFields(){
        mServerEt = (EditText)findViewById(R.id.server_address_et);
        mLocEt = (EditText)findViewById(R.id.loc_threshold_et);
        mTimeEt = (EditText)findViewById(R.id.time_interval_et);
    }

    private void fillFields(){
        mServerEt.setText(PrefsHelper.getInstance().getServerAddress());
        mLocEt.setText(String.valueOf(PrefsHelper.getInstance().getLocChangeThreshold()));
        mTimeEt.setText(String.valueOf(PrefsHelper.getInstance().getSendingInterval()/1000));
    }

    public void saveData(View v){
        PrefsHelper.getInstance().setServerAddress(mServerEt.getText().toString());
        PrefsHelper.getInstance().setLocChangeThreshold(Integer.parseInt(mLocEt.getText().toString()));
        PrefsHelper.getInstance().setSendingInterval(Integer.parseInt(mTimeEt.getText().toString()));

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                ((TrackerService.TrackerBinder) iBinder).getService().addUpdateRequests();
                Api.refreshApiUrl();
                Toast.makeText(SettingsActivity.this, "Settings saved", Toast.LENGTH_LONG).show();
                unbindService(mServiceConnection);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };

        bindService(new Intent(this, TrackerService.class),mServiceConnection, BIND_AUTO_CREATE);

        Toast.makeText(this, "Settings saved", Toast.LENGTH_LONG).show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
