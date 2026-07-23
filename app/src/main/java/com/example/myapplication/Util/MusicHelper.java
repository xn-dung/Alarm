package com.example.myapplication.Util;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import com.example.myapplication.R;
import java.util.Random;

public class MusicHelper {
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private Runnable stopRunnable;
    private static final int[] DEFAULT_ALARMS = {
            R.raw.alarm1,
            R.raw.alarm2,
            R.raw.alarm3,
            R.raw.alarm4,
            R.raw.alarm5

    };
    public MusicHelper(){
        handler = new Handler(Looper.getMainLooper());
        stopRunnable = () -> stop();
    }

    public void playFromResource(Context context, int resId){
        stop();
        try {
            mediaPlayer = MediaPlayer.create(context,resId,new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build(),AudioManager.AUDIO_SESSION_ID_GENERATE);
        } catch (Exception ignored) {
            mediaPlayer = null;
        }
        if(mediaPlayer!=null){
            mediaPlayer.start();
        }
    }
    public void playDefault(Context context){
        playFromResource(context,DEFAULT_ALARMS[0]);
    }
    public void playFromUri(Context context, String value){
        stop();
        try { mediaPlayer = MediaPlayer.create(context, Uri.parse(value), null, new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build(), AudioManager.AUDIO_SESSION_ID_GENERATE); } catch (Exception ignored) { mediaPlayer = null; }
        if (mediaPlayer != null) mediaPlayer.start();
    }
    public void playRandom(Context context){
        Random random = new Random();
        int index = random.nextInt(DEFAULT_ALARMS.length);
        playFromResource(context,DEFAULT_ALARMS[index]);
    }
    public void pause(){
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }
    public void resume(){
        if(mediaPlayer!=null){
            mediaPlayer.start();
        }
    }
    public void stop(){
        if(mediaPlayer != null){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    public void release(){
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    public void setLooping(boolean looping){
        if(mediaPlayer != null) {
            mediaPlayer.setLooping(looping);
        }
    }
    public void setVolume(float volume){
        if(mediaPlayer != null){
            volume = Math.max(0f,Math.min(volume,1f));
            mediaPlayer.setVolume(volume,volume);
        }
    }
    public boolean isPlaying(){
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }
    private void cancelTimer(){
        if(handler != null && stopRunnable != null){
            handler.removeCallbacks(stopRunnable);
        }
    }

}
