package com.example.myapplication.Database;
import android.provider.BaseColumns;


public class AlarmContract {
    private AlarmContract(){}
    public static class AlarmEntry implements BaseColumns {
        public static final String TABLE_NAME = "alarms";
        public static final String COLUMN_HOUR = "hour";
        public static final String COLUMN_MINUTE = "minute";
        public static final String COLUMN_LABEL = "label";
        public static final String COLUMN_REPEAT_DAYS = "repeat_days";
        public static final String COLUMN_ENABLED = "enabled";
        public static final String COLUMN_MUSIC_URI = "music_uri";
        public static final String COLUMN_VOLUMNE = "volume";
        public static final String COLUMN_RANDOM = "random_music";
        public static final String COLUMN_LOOP = "loop_alarm";
        public static final String COLUMN_VIBRATE = "vibrate";
        public static final String COLUMN_MUSIC_ID = "music_id";
        public static final String COLUMN_DISMISS_MODE = "dismiss_mode";
    }
}
