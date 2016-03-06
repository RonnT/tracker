package jp.co.skybus.tracker.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import jp.co.skybus.tracker.helper.Logger;

/**
 * Created by Roman T. on 06.03.2016.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d("BootReceiver: onReceive()");
        context.startService(new Intent(context, TrackerService.class));
    }
}
