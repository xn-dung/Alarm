package com.example.myapplication.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.example.myapplication.Activities.AlarmActivity;
import com.example.myapplication.Model.Alarm;
import com.example.myapplication.R;
import com.example.myapplication.Util.MusicHelper;

public class AlarmService extends Service {
    private MusicHelper musicHelper;
    private static final String CHANNEL_ID = "AlarmServiceChannel";
    private static final int NOTIFICATION_ID = 410000;

    @Override
    public void onCreate() {
        super.onCreate();
        musicHelper = new MusicHelper();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Alarm alarm = (Alarm) intent.getSerializableExtra("ALARM_OBJECT");
        createNotificationChannel();
        
        Intent fullScreenIntent = new Intent(this, AlarmActivity.class);
        fullScreenIntent.putExtra("ALARM_OBJECT", alarm);
        fullScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0));

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Alarm Ringing")
                .setContentText(alarm != null ? alarm.getLabel() : "Time to wake up!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(fullScreenPendingIntent)
                .setDeleteIntent(fullScreenPendingIntent)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setOngoing(true)
                .setAutoCancel(false)
                .build();

        startForeground(NOTIFICATION_ID, notification);
        
        if (alarm != null) {
            if (alarm.getRandomMusic()) {
                musicHelper.playRandom(this);
            } else if (alarm.getMusicUri() != null && !alarm.getMusicUri().isEmpty()) {
                musicHelper.playFromUri(this, alarm.getMusicUri());
                if (!musicHelper.isPlaying()) {
                    musicHelper.playRandom(this);
                }
            } else {
                int[] sounds = {R.raw.alarm1, R.raw.alarm2, R.raw.alarm3, R.raw.alarm4, R.raw.alarm5};
                musicHelper.playFromResource(this, sounds[Math.max(0, Math.min(alarm.getMusicId(), 4))]);
            }
            musicHelper.setLooping(alarm.getLoop());
            musicHelper.setVolume(alarm.getVolume() / 100f);
            if (alarm.getVibrate()) {
                android.os.Vibrator vibrator = (android.os.Vibrator) getSystemService(VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) vibrator.vibrate(android.os.VibrationEffect.createWaveform(new long[]{0, 500, 500}, 0));
                else vibrator.vibrate(new long[]{0, 500, 500}, 0);
            }
        } else {
            musicHelper.playDefault(this);
        }

        return START_NOT_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID, "Alarm Service Channel", NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        musicHelper.stop();
        ((android.os.Vibrator) getSystemService(VIBRATOR_SERVICE)).cancel();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE);
        } else {
            stopForeground(true);
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
