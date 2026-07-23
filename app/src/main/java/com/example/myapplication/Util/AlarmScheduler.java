package com.example.myapplication.Util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.example.myapplication.Model.Alarm;
import com.example.myapplication.Receiver.AlarmReceiver;
import com.example.myapplication.Receiver.UpcomingAlarmReceiver;
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

        scheduleAt(alarmManager, calendar.getTimeInMillis(), pendingIntent);
        long reminder = calendar.getTimeInMillis() - 60_000L;
        if (reminder > System.currentTimeMillis()) {
            Intent upcoming = new Intent(context, UpcomingAlarmReceiver.class); upcoming.putExtra("ALARM_ID", alarm.getId()); upcoming.putExtra("LABEL", alarm.getLabel());
            PendingIntent notificationIntent = PendingIntent.getBroadcast(context, 200000 + alarm.getId(), upcoming, PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0));
            scheduleAt(alarmManager, reminder, notificationIntent);
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
        PendingIntent reminder = PendingIntent.getBroadcast(context, 200000 + alarmId, new Intent(context, UpcomingAlarmReceiver.class), PendingIntent.FLAG_NO_CREATE | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0));
        if (reminder != null) { alarmManager.cancel(reminder); reminder.cancel(); }
    }
    public static void snooze(Context context, Alarm alarm) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("ALARM_ID", alarm.getId());
        intent.putExtra("ALARM_OBJECT", alarm);
        intent.putExtra("SNOOZE", true);
        PendingIntent pending = PendingIntent.getBroadcast(context, 100000 + alarm.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0));
        long when = System.currentTimeMillis() + 5 * 60 * 1000L;
        scheduleAt(manager, when, pending);
    }
    private static void scheduleAt(AlarmManager manager, long time, PendingIntent pending) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !manager.canScheduleExactAlarms()) manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending);
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending);
            else manager.setExact(AlarmManager.RTC_WAKEUP, time, pending);
        } catch (SecurityException ignored) {
            manager.set(AlarmManager.RTC_WAKEUP, time, pending);
        }
    }
}
