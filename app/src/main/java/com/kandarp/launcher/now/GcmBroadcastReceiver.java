package com.kandarp.launcher.now;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmBroadcastReceiver extends BroadcastReceiver {
    static final String TAG = "KandarpNow_Service";
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        mContext = context.getApplicationContext();
        String messageType = gcm.getMessageType(intent);
        if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            // sendNotification(intent.getExtras().toString());
            Log.e(TAG, "Message type: Error");
        } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                .equals(messageType)) {
            Log.e(TAG, "Message type: Deleted");
            // sendNotification(intent.getExtras().toString());
        } else {
            Log.i(TAG, "Message type: Successful");
            Bundle extras = intent.getExtras();
            if (extras.getString("message") != null) {
                String sender = extras.getString("sender");
                String message = extras.getString("message");
                if (!message.equals("")) {
                    sendNotification(sender, message);
                } else {
                    Log.i(TAG, "Tickle message received");
                }
            }
        }
        setResultCode(Activity.RESULT_OK);
    }

    // Put the GCM message into a notification and post it.
    private void sendNotification(String sender, String message) {
        NotificationManager notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        int timestamp = (int) System.currentTimeMillis();
        String title = "" + sender;
        Notification notification;
        Intent notificationIntent = new Intent(mContext, ReceivedMessages.class);
        notificationIntent.putExtra("sender", sender);
        notificationIntent.putExtra("message", message);
        PendingIntent intent = PendingIntent.getActivity(mContext, 0,
                notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                mContext);
        builder.setTicker("Kandarp")
                .setWhen(System.currentTimeMillis()).setContentTitle(title)
                .setContentText(message).setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true).setContentIntent(intent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            builder.setStyle(new NotificationCompat.BigTextStyle(builder)
                    .bigText(message));
            notification = builder.build();
        } else {
            notification = builder.build();
        }
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(timestamp, notification);
    }
}
