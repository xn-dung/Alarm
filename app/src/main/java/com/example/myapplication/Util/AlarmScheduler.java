package com.example.myapplication.Util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.example.myapplication.Model.Alarm;
import com.example.myapplication.Receiver.AlarmReceiver;
import java.util.Calendar;

public class AlarmScheduler {
    public static void schedule(Context context, Alarm alarm) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("ALARM_ID", alarm.getId());
        intent.putExtra("ALARM_OBJECT", alarm);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, alarm.getId(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        calendar.set(Calendar.MINUTE, alarm.getMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        String repeat = alarm.getRepeatDays();
        if (repeat != null && !repeat.isEmpty()) {
            for (int i = 0; i < 7; i++) {
                int day = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7;
                if (repeat.contains(String.valueOf(day))) break;
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    public static void cancel(Context context, int alarmId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, alarmId, intent,
                PendingIntent.FLAG_NO_CREATE | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }
}
