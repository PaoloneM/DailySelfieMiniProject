package com.paolone.dailyselfie;

import java.text.DateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DailySelfieAlarmLoggerReceiver extends BroadcastReceiver {

    private static final String TAG = "DailySelfieAlarmLoggerReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"Logging alarm at:" + DateFormat.getDateTimeInstance().format(new Date()));

    }
}