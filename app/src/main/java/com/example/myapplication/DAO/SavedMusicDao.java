package com.example.myapplication.DAO;

import android.content.ContentValues;
import android.content.Context;

import com.example.myapplication.Database.AlarmContract.SavedMusicEntry;
import com.example.myapplication.Database.AlarmDatabaseHelper;
import com.example.myapplication.Model.SavedMusic;

import java.util.List;

public class SavedMusicDao {
    private final AlarmDatabaseHelper db;

    public SavedMusicDao(Context context) {
        db = new AlarmDatabaseHelper(context);
    }

    public long save(String name, String uri) {
        ContentValues values = new ContentValues();
        values.put(SavedMusicEntry.COLUMN_NAME, name);
        values.put(SavedMusicEntry.COLUMN_URI, uri);
        values.put(SavedMusicEntry.COLUMN_ADDED_AT, System.currentTimeMillis());
        return db.insertSavedMusic(values);
    }

    public List<SavedMusic> getAll() {
        return db.getAllSavedMusic();
    }
}
