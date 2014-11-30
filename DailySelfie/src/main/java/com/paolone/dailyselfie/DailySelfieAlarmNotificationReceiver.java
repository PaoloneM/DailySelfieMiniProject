package com.paolone.dailyselfie;

import java.text.DateFormat;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.paolone.dailyselfie.DailySelfieMainActivity;
import com.paolone.dailyselfie.R;

public class DailySelfieAlarmNotificationReceiver extends BroadcastReceiver {
    // Notification ID to allow for future updates
    private static final int MY_NOTIFICATION_ID = 1;
    private static final String TAG = "DailySelfieAlarmNotificationReceiver";

    // Notification Text Elements
    private CharSequence tickerText = "It's selfie time";
    private CharSequence contentTitle = "Daily Selfie App";
    private CharSequence contentText = "Take you picture now!";

    // Notification Action Elements
    private Intent mNotificationIntent;
    private PendingIntent mContentIntent;

    // Notification Sound and Vibration on Arrival
    private Uri soundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    private long[] mVibratePattern = { 0, 200, 200, 300 };

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "DailySelfieAlarmNotificationReceiver.onReceive entered");

        tickerText = context.getString(R.string.alarm_ticker_text);
        contentTitle = context.getString(R.string.alarm_title_text);
        contentText = context.getString(R.string.alarm_body_text);
        mNotificationIntent = new Intent(context, DailySelfieMainActivity.class);
        mContentIntent = PendingIntent.getActivity(context, 0,
                mNotificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

        Notification.Builder notificationBuilder = new Notification.Builder(
                context).setTicker(tickerText)
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setAutoCancel(true).setContentTitle(contentTitle)
                .setContentText(contentText).setContentIntent(mContentIntent)
                .setSound(soundURI).setVibrate(mVibratePattern);

        // Pass the Notification to the NotificationManager:
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MY_NOTIFICATION_ID,
                notificationBuilder.build());

        Log.i(TAG,"Sending notification at:" + DateFormat.getDateTimeInstance().format(new Date()));

    }
}
