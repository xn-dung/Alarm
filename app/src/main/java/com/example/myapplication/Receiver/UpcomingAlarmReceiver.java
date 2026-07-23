package com.example.myapplication.Receiver;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.myapplication.R;
public class UpcomingAlarmReceiver extends BroadcastReceiver {
 public void onReceive(Context context, Intent intent){NotificationManager manager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);String channel="upcoming_alarm";if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)manager.createNotificationChannel(new NotificationChannel(channel,"Upcoming alarms",NotificationManager.IMPORTANCE_HIGH));String label=intent.getStringExtra("LABEL");manager.notify(intent.getIntExtra("ALARM_ID",0),new NotificationCompat.Builder(context,channel).setSmallIcon(R.drawable.ic_launcher_foreground).setContentTitle("Alarm in 1 minute").setContentText(label==null||label.isEmpty()?"Your alarm is about to ring":label).setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(true).build());}
}
