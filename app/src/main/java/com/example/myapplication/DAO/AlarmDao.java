package com.example.myapplication.DAO;
import android.content.ContentValues;
import android.content.Context;
import com.example.myapplication.Database.AlarmContract.AlarmEntry;
import com.example.myapplication.Database.AlarmDatabaseHelper;
import com.example.myapplication.Model.Alarm;
import java.util.List;
public class AlarmDao {
 private final AlarmDatabaseHelper db; public AlarmDao(Context c){db=new AlarmDatabaseHelper(c);}
 public long insertAlarm(Alarm a){return db.insertAlarm(values(a));} public int updateAlarm(Alarm a){return db.updateAlarm(values(a),a);} public int deleteAlarm(int id){return db.deleteAlarm(id);} public Alarm getAlarmbyId(int id){return db.getAlarmById(id);} public List<Alarm> getAllAlarms(){return db.getAllAlarms();}
 private ContentValues values(Alarm a){ContentValues v=new ContentValues();v.put(AlarmEntry.COLUMN_HOUR,a.getHour());v.put(AlarmEntry.COLUMN_MINUTE,a.getMinute());v.put(AlarmEntry.COLUMN_LABEL,a.getLabel());v.put(AlarmEntry.COLUMN_REPEAT_DAYS,a.getRepeatDays());v.put(AlarmEntry.COLUMN_ENABLED,a.getEnabled()?1:0);v.put(AlarmEntry.COLUMN_MUSIC_URI,a.getMusicUri());v.put(AlarmEntry.COLUMN_VOLUMNE,a.getVolume());v.put(AlarmEntry.COLUMN_RANDOM,a.getRandomMusic()?1:0);v.put(AlarmEntry.COLUMN_LOOP,a.getLoop()?1:0);v.put(AlarmEntry.COLUMN_VIBRATE,a.getVibrate()?1:0);v.put(AlarmEntry.COLUMN_MUSIC_ID,a.getMusicId());v.put(AlarmEntry.COLUMN_DISMISS_MODE,a.getDismissMode());return v;}
}
