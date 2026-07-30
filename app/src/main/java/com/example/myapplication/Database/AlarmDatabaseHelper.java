package com.example.myapplication.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.Model.Alarm;
import com.example.myapplication.Model.SavedMusic;

import java.util.ArrayList;
import java.util.List;

public class AlarmDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "alarm.db";
    private static final int DATABASE_VERSION = 3;

    public AlarmDatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_TABLE =
                "CREATE TABLE " + AlarmContract.AlarmEntry.TABLE_NAME + " (" +
                        AlarmContract.AlarmEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        AlarmContract.AlarmEntry.COLUMN_HOUR + " INTEGER NOT NULL, " +
                        AlarmContract.AlarmEntry.COLUMN_MINUTE + " INTEGER NOT NULL, " +
                        AlarmContract.AlarmEntry.COLUMN_LABEL + " TEXT, " +
                        AlarmContract.AlarmEntry.COLUMN_REPEAT_DAYS + " INTEGER, " +
                        AlarmContract.AlarmEntry.COLUMN_ENABLED + " INTEGER, " +
                        AlarmContract.AlarmEntry.COLUMN_MUSIC_URI +" TEXT, "+
                        AlarmContract.AlarmEntry.COLUMN_VOLUMNE + " INTEGER, "+
                        AlarmContract.AlarmEntry.COLUMN_RANDOM + " INTEGER, "+
                        AlarmContract.AlarmEntry.COLUMN_LOOP + " INTEGER, " +
                        AlarmContract.AlarmEntry.COLUMN_VIBRATE + " INTEGER, " +
                        AlarmContract.AlarmEntry.COLUMN_MUSIC_ID + " INTEGER DEFAULT 0, " +
                        AlarmContract.AlarmEntry.COLUMN_DISMISS_MODE + " INTEGER DEFAULT 0" +");";
        db.execSQL(CREATE_TABLE);
        createSavedMusicTable(db);
    }
    // Used
    public long insertAlarm(ContentValues values){
        SQLiteDatabase db = this.getWritableDatabase();

        long rowId = db.insert(AlarmContract.AlarmEntry.TABLE_NAME,null,values);

        db.close();
        return rowId;
    }

    //Used
    public int deleteAlarm(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        int rowDeleted = db.delete(AlarmContract.AlarmEntry.TABLE_NAME,
                AlarmContract.AlarmEntry._ID + " =?",
                new String[]{String.valueOf(id)});

        db.close();
        return rowDeleted;
    }
    //Used
    public int updateAlarm(ContentValues values, Alarm alarm){
        SQLiteDatabase db = this.getWritableDatabase();

        int rowsUpdated = db.update(
                AlarmContract.AlarmEntry.TABLE_NAME,
                values,
                AlarmContract.AlarmEntry._ID + "=?",
                new String[]{String.valueOf(alarm.getId())}
        );

        db.close();

        return rowsUpdated;
    }
    public Alarm getAlarmById(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                AlarmContract.AlarmEntry.TABLE_NAME,
                null,
                AlarmContract.AlarmEntry._ID + "=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        Alarm alarm = new Alarm();
        if (cursor.moveToFirst()) {
            alarm = cursorToAlarm(cursor);
        }

        cursor.close();
        db.close();

        return alarm;
    }
    public List<Alarm> getAllAlarms(){
        List<Alarm> alarmList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                AlarmContract.AlarmEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                AlarmContract.AlarmEntry.COLUMN_HOUR + " ASC, " +
                        AlarmContract.AlarmEntry.COLUMN_MINUTE + " ASC"
        );

        if (cursor.moveToFirst()) {

            do {
                Alarm alarm = new Alarm();
                alarm = cursorToAlarm(cursor);
                alarmList.add(alarm);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return alarmList;
    }

    public long insertSavedMusic(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        long rowId = db.insertWithOnConflict(
                AlarmContract.SavedMusicEntry.TABLE_NAME,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );
        db.close();
        return rowId;
    }

    public List<SavedMusic> getAllSavedMusic() {
        List<SavedMusic> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                AlarmContract.SavedMusicEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                AlarmContract.SavedMusicEntry.COLUMN_ADDED_AT + " DESC"
        );
        while (cursor.moveToNext()) {
            result.add(new SavedMusic(
                    cursor.getInt(cursor.getColumnIndexOrThrow(AlarmContract.SavedMusicEntry._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(AlarmContract.SavedMusicEntry.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(AlarmContract.SavedMusicEntry.COLUMN_URI))
            ));
        }
        cursor.close();
        db.close();
        return result;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        if (oldVersion < 2) db.execSQL("ALTER TABLE " + AlarmContract.AlarmEntry.TABLE_NAME + " ADD COLUMN " + AlarmContract.AlarmEntry.COLUMN_DISMISS_MODE + " INTEGER DEFAULT 0");
        if (oldVersion < 3) {
            createSavedMusicTable(db);
            migrateExistingMusic(db);
        }
    }

    private void createSavedMusicTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + AlarmContract.SavedMusicEntry.TABLE_NAME + " (" +
                        AlarmContract.SavedMusicEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        AlarmContract.SavedMusicEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                        AlarmContract.SavedMusicEntry.COLUMN_URI + " TEXT NOT NULL UNIQUE, " +
                        AlarmContract.SavedMusicEntry.COLUMN_ADDED_AT + " INTEGER NOT NULL)"
        );
    }

    private void migrateExistingMusic(SQLiteDatabase db) {
        db.execSQL(
                "INSERT OR IGNORE INTO " + AlarmContract.SavedMusicEntry.TABLE_NAME + " (" +
                        AlarmContract.SavedMusicEntry.COLUMN_NAME + ", " +
                        AlarmContract.SavedMusicEntry.COLUMN_URI + ", " +
                        AlarmContract.SavedMusicEntry.COLUMN_ADDED_AT + ") " +
                        "SELECT 'Device audio ' || " + AlarmContract.AlarmEntry._ID + ", " +
                        AlarmContract.AlarmEntry.COLUMN_MUSIC_URI + ", " +
                        System.currentTimeMillis() + " FROM " +
                        AlarmContract.AlarmEntry.TABLE_NAME + " WHERE " +
                        AlarmContract.AlarmEntry.COLUMN_MUSIC_URI + " IS NOT NULL AND TRIM(" +
                        AlarmContract.AlarmEntry.COLUMN_MUSIC_URI + ") != ''"
        );
    }

    private Alarm cursorToAlarm(Cursor cursor){
        Alarm alarm = new Alarm();

        int idIdx = cursor.getColumnIndexOrThrow(
                AlarmContract.AlarmEntry._ID
        );
        int hourIdx = cursor.getColumnIndexOrThrow(
                AlarmContract.AlarmEntry.COLUMN_HOUR
        );
        int minuteIdx = cursor.getColumnIndexOrThrow(
                AlarmContract.AlarmEntry.COLUMN_MINUTE
        );
        int labelIdx = cursor.getColumnIndexOrThrow(
                AlarmContract.AlarmEntry.COLUMN_LABEL
        );
        int enabledIdx = cursor.getColumnIndexOrThrow(
                AlarmContract.AlarmEntry.COLUMN_ENABLED
        );
        int repeatDaysIdx = cursor.getColumnIndexOrThrow(
                AlarmContract.AlarmEntry.COLUMN_REPEAT_DAYS
        );
        int musicUriIdx = cursor.getColumnIndexOrThrow(
                AlarmContract.AlarmEntry.COLUMN_MUSIC_URI
        );
        int volumneIdx = cursor.getColumnIndexOrThrow(
                AlarmContract.AlarmEntry.COLUMN_VOLUMNE
        );
        int randomMusicIdx = cursor.getColumnIndexOrThrow(
                AlarmContract.AlarmEntry.COLUMN_RANDOM
        );
        int vibrateIdx = cursor.getColumnIndexOrThrow(
                AlarmContract.AlarmEntry.COLUMN_VIBRATE
        );
        int loopIdx = cursor.getColumnIndexOrThrow(
                AlarmContract.AlarmEntry.COLUMN_LOOP
        );
        int musicIdx = cursor.getColumnIndexOrThrow(
                AlarmContract.AlarmEntry.COLUMN_MUSIC_ID
        );
        int dismissModeIdx = cursor.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_DISMISS_MODE);
        alarm.setId(cursor.getInt(idIdx));
        alarm.setHour(cursor.getInt(hourIdx));
        alarm.setMinute(cursor.getInt(minuteIdx));
        alarm.setLabel(cursor.getString(labelIdx));
        alarm.setEnabled(cursor.getInt(enabledIdx) == 1);
        alarm.setRepeatDays(cursor.getString(repeatDaysIdx));
        alarm.setMusicUri(cursor.getString(musicUriIdx));
        alarm.setVolume(cursor.getInt(volumneIdx));
        alarm.setRandomMusic(cursor.getInt(randomMusicIdx) == 1);
        alarm.setVibrate(cursor.getInt(vibrateIdx) == 1);
        alarm.setLoop(cursor.getInt(loopIdx) == 1);
        alarm.setMusicId(cursor.getInt(musicIdx));
        alarm.setDismissMode(cursor.getInt(dismissModeIdx));
        return alarm;
    }

}
