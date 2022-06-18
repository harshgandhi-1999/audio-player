package com.example.myaudioplayerapp.services;
import static com.example.myaudioplayerapp.notifications.ApplicationClass.ACTION_NEXT;
import static com.example.myaudioplayerapp.notifications.ApplicationClass.ACTION_PLAY;
import static com.example.myaudioplayerapp.notifications.ApplicationClass.ACTION_PREVIOUS;
import static com.example.myaudioplayerapp.notifications.ApplicationClass.CHANNEL_ID_1;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myaudioplayerapp.PlayerActivity;
import com.example.myaudioplayerapp.R;
import com.example.myaudioplayerapp.models.MusicFile;
import com.example.myaudioplayerapp.receivers.NotificationReceiver;
import com.example.myaudioplayerapp.utils.Utility;

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
    private final IBinder localBinder = new MusicBinder();
    MediaSessionCompat mediaSessionCompat;

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
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(),"My Audio");
        initMusicPlayer();
        //createNotificationChannel();
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
//            postNotification("Music is stopped");
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

    public void postNotification(int playPauseButton) {

        Intent intent = new Intent(this, PlayerActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this,0,intent,0);

        Intent prevIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PREVIOUS);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this,0,prevIntent,PendingIntent.FLAG_UPDATE_CURRENT);


        Intent nextIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this,0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);


        Intent playPauseIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PLAY);
        PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(this,0,playPauseIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        // setting up image in notification
        byte[] picture = Utility.getMusicImage(songs.get(curPosition).getPath());
        Bitmap thumb;

        if(picture!=null){
            thumb = BitmapFactory.decodeByteArray(picture,0,picture.length);
        }else{
            thumb = BitmapFactory.decodeResource(getResources(),R.drawable.default_album_art);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID_1)
                .setSmallIcon(R.drawable.notification_icon)
                .setLargeIcon(thumb)
                .setContentTitle(songs.get(curPosition).getTitle())
                .setContentText(songs.get(curPosition).getArtist())
                .addAction(R.drawable.ic_skip_prev,"PREVIOUS",prevPendingIntent)
                .addAction(playPauseButton,"PAUSE",playPausePendingIntent)
                .addAction(R.drawable.ic_skip_next,"NEXT",nextPendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken())
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true);

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