package com.jsync.appsdeaddiction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Created by jaseem on 5/5/19.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, CheckAppsBackground.class));
        }else{
            context.startService(new Intent(context, CheckAppsBackground.class));
        }
    }
}
