package com.kandarp.launcher.now.geofence_reminders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

public class GeofenceSampleReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (TextUtils.equals(action, GeolocationService.ACTION_GEOFENCES_ERROR)) {
            Log.d(GeofenceActivity.TAG, "Error adding geofences");
        } else if (TextUtils.equals(action,
                GeolocationService.ACTION_GEOFENCES_SUCCESS)) {
            Log.d(GeofenceActivity.TAG, "Success adding geofences");
        }

    }
}
