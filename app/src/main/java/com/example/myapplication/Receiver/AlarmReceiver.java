package com.example.myapplication.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.example.myapplication.Service.AlarmService;
import com.example.myapplication.DAO.AlarmDao;
import com.example.myapplication.Model.Alarm;
import com.example.myapplication.Util.AlarmScheduler;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, AlarmService.class);
        serviceIntent.putExtras(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
        int id = intent.getIntExtra("ALARM_ID", -1);
        if (id >= 0) {
            Alarm alarm = new AlarmDao(context).getAlarmbyId(id);
            if (alarm.getEnabled() && alarm.getRepeatDays() != null && !alarm.getRepeatDays().isEmpty()) AlarmScheduler.schedule(context, alarm);
            else if (alarm.getEnabled()) { alarm.setEnabled(false); new AlarmDao(context).updateAlarm(alarm); }
        }
    }
}
