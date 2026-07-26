package com.example.myapplication.Util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.myapplication.Receiver.TimerReceiver;

public class TimerScheduler {
    private static final int REQUEST_CODE = 310000;

    public static void schedule(Context context, long delayMillis) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pending = pendingIntent(context, PendingIntent.FLAG_UPDATE_CURRENT);
        long time = System.currentTimeMillis() + delayMillis;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !manager.canScheduleExactAlarms()) manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending);
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending);
            else manager.setExact(AlarmManager.RTC_WAKEUP, time, pending);
        } catch (SecurityException ignored) {
            manager.set(AlarmManager.RTC_WAKEUP, time, pending);
        }
    }

    public static void cancel(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pending = pendingIntent(context, PendingIntent.FLAG_NO_CREATE);
        if (pending != null) {
            manager.cancel(pending);
            pending.cancel();
        }
    }

    private static PendingIntent pendingIntent(Context context, int flag) {
        int immutable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0;
        return PendingIntent.getBroadcast(context, REQUEST_CODE, new Intent(context, TimerReceiver.class), flag | immutable);
    }
}
