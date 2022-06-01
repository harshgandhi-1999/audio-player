package com.example.myaudioplayerapp.services;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myaudioplayerapp.R;
import com.example.myaudioplayerapp.models.MusicFile;

import java.util.ArrayList;

// service for playing song
public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener{
    private static final String TAG = "MusicPlayerService";
    private static MusicPlayerService instance = null;

    private MediaPlayer myMediaPlayer;
    private int curPosition = 0;
    private ArrayList<MusicFile> songs;
    private final int notification_id = 1;
    private final String CHANNEL_ID = "channel1";
    private final IBinder localBinder = new MusicBinder();
    private LayoutInflater layoutInflater;

    public MusicPlayerService() {
        Log.i(TAG, "MusicPlayerService: Constructor called");
    }

    public static MusicPlayerService getInstance(){
        if(instance==null){
            instance = new MusicPlayerService();
        }
        return instance;
    }

    public MediaPlayer getMyMediaPlayer() {
        return myMediaPlayer;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }
    @Override
    public boolean onUnbind(Intent intent){
    //    myMediaPlayer.stop();
        if(myMediaPlayer!=null){
            myMediaPlayer.release();
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: music player service onCreated called");
        myMediaPlayer = new MediaPlayer();
        curPosition = 0;
        initMusicPlayer();
        createNotificationChannel();
    }

    public void setCurPosition(int curPosition){
        this.curPosition = curPosition;
    }

    public void playSong(){
        myMediaPlayer.reset();
        MusicFile musicFile = songs.get(curPosition);
        Uri uri = Uri.parse(musicFile.getPath());

        try{
            myMediaPlayer.setDataSource(this,uri);
        }catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source = " + e.getLocalizedMessage());
        }

        myMediaPlayer.prepareAsync();
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.stop();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        myMediaPlayer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: SERVICE_DESTROYED");
        if(myMediaPlayer!=null){
            myMediaPlayer.release();
//            myMediaPlayer = null;
            Toast.makeText(this, "Music is stopped", Toast.LENGTH_SHORT).show();
            postNotification("Music is stopped");
        }
    }

    public int getDuration(){
        if(myMediaPlayer!=null){
            return myMediaPlayer.getDuration();
        }

        return 0;
    }

    public void initMusicPlayer(){
        myMediaPlayer.setAudioAttributes(
                new AudioAttributes
                        .Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build());

        myMediaPlayer.setOnPreparedListener(this);
        myMediaPlayer.setOnCompletionListener(this);
        myMediaPlayer.setOnErrorListener(this);
    }

    public void setList(ArrayList<MusicFile> songsList){
        songs = songsList;
    }

    private void createNotificationChannel(){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,name,importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void postNotification(String msg) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("My Music Player")
                .setContentText(msg)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Notification notification = builder.build();
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(TAG,notification_id,notification);
        //startForeground(notification_id,notification);
    }

    public class MusicBinder extends Binder{
        public MusicPlayerService getService(){
            return MusicPlayerService.this;
        }
    }
}