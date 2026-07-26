package com.example.myapplication.Database;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.myapplication.Model.RecentTimer;
import java.util.ArrayList;
import java.util.List;
public class TimerRecentDatabase extends SQLiteOpenHelper {
 public TimerRecentDatabase(Context context){
    super(context,"timer_recent.db",null,1);
}
 public void onCreate(SQLiteDatabase db){
    db.execSQL("CREATE TABLE recent_timers (seconds INTEGER PRIMARY KEY, name TEXT, used_at INTEGER)");
}
 public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){}
 public void save(long seconds,String name){
    SQLiteDatabase db=getWritableDatabase();
    ContentValues value=new ContentValues();
    value.put("seconds",seconds);value.put("name",name);
    value.put("used_at",System.currentTimeMillis());
    db.insertWithOnConflict("recent_timers",null,value,SQLiteDatabase.CONFLICT_REPLACE);
    Cursor cursor=db.query("recent_timers",new String[]{"seconds"},null,null,null,null,"used_at DESC");
    List<Long> stale=new ArrayList<>();
    int index=0;
    while(cursor.moveToNext()){
        if(index++>=5) stale.add(cursor.getLong(0));
    }cursor.close();
    for(Long item:stale)
        db.delete("recent_timers","seconds=?",new String[]{String.valueOf(item)});
    db.close();
}
 public List<RecentTimer> recent(){
    List<RecentTimer> result=new ArrayList<>();
    SQLiteDatabase db=getReadableDatabase();
    Cursor cursor=db.query("recent_timers",new String[]{"seconds","name"},null,null,null,null,"used_at DESC");
    while(cursor.moveToNext()){
        long s=cursor.getLong(0);
        String n=cursor.getString(1);
        result.add(new RecentTimer(s,n));
    }
    cursor.close();db.close();
    return result;
    }
}
