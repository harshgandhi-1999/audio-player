package com.example.myaudioplayerapp;

import static com.example.myaudioplayerapp.MainActivity.musicFiles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myaudioplayerapp.models.MusicFile;
import com.example.myaudioplayerapp.services.MusicPlayerService;

import java.util.ArrayList;
import java.util.Locale;

public class PlayerActivity extends AppCompatActivity {

    private static final String TAG = "PlayerActivity";
    private TextView songTitle,songArtistName,durationPlayed,durationTotal;
    private ImageView albumArt,prevBtn,nextBtn,shuffleBtn,repeatBtn,playBtn;
    private SeekBar seekBar;
    static ArrayList<MusicFile> songsList = new ArrayList<>();
    static Uri uri;
    private MediaPlayer mediaPlayer = null;
    private Handler handler = new Handler();

    // new vars
    private MusicPlayerService musicPlayerService;
    private Intent playIntent;
    private boolean musicBound=false;

    int position = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // initialize all the views
        initViews();

        // get intent data
        getIntentMethod();
        mediaPlayer = MusicPlayerService.getInstance().getMyMediaPlayer();

        // set seekbar change listener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null && fromUser){
                    mediaPlayer.seekTo(progress*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    // in milli seconds
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition/1000);
                    durationPlayed.setText(getFormattedTime(currentPosition));
                }
                handler.postDelayed(this,1000);
            }
        });

        playBtn.setOnClickListener(view -> {
            if(mediaPlayer!=null){
                // song already playing
                if(mediaPlayer.isPlaying()){
                    // change the image resource and pause the song
                    playBtn.setImageResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                }else{
                    playBtn.setImageResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }
            }
        });

        nextBtn.setOnClickListener(view ->{
            playNextSong();
        });

        prevBtn.setOnClickListener(view->{
            playPrevSong();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicPlayerService.class);
            bindService(playIntent, musicConnection, BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlayerService.MusicBinder binder = (MusicPlayerService.MusicBinder)service;
            //get service
            musicPlayerService = binder.getService();
            //pass list
            musicPlayerService.setList(songsList);
            musicBound = true;

            mediaPlayer = musicPlayerService.getMyMediaPlayer();

            Log.i(TAG, "onServiceConnected: MediaPlayer = " + mediaPlayer);
            playSong();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    private void getIntentMethod() {
        position = getIntent().getIntExtra("position",0);
        songsList = musicFiles;
        Log.i(TAG, "getIntentMethod: MediaPlayer = " + mediaPlayer);
    }


    private void playSong(){
        Log.i(TAG, "playSong: Mediaplayer = " + mediaPlayer);
        if(mediaPlayer!=null){
            MusicFile musicFile = songsList.get(position);
            Uri uri = Uri.parse(musicFile.getPath());

            songTitle.setText(musicFile.getTitle());
            songArtistName.setText(musicFile.getArtist());
            // set album art
            setAlbumArt(uri);

            musicPlayerService.setCurPosition(position);
            musicPlayerService.playSong();

            // set seekbar total duration
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            String dt = getFormattedTime(mediaPlayer.getDuration());
            durationTotal.setText(dt);
            Log.i(TAG, "playSong: durationTotal  = " + dt);

            // is not playing that means play btn is shown so we have to change it to paused
            if(!mediaPlayer.isPlaying()){
                playBtn.setImageResource(R.drawable.ic_pause);
            }
        }
    }

    private void playPrevSong() {
        Toast.makeText(this, "Playing previous song", Toast.LENGTH_SHORT).show();
        position = (position-1)%songsList.size();
        playSong();
    }

    private void playNextSong() {
        Toast.makeText(this, "Playing next song", Toast.LENGTH_SHORT).show();
        position = (position+1)%songsList.size();
        playSong();
    }

    private void setAlbumArt(Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        byte[] data = retriever.getEmbeddedPicture();

        if(data!=null){
            Glide.with(this)
                    .asBitmap()
                    .load(data)
                    .into(albumArt);
        }else{
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.default_album_art)
                    .into(albumArt);
        }
    }

    private void initViews() {
        songTitle = findViewById(R.id.songTitleTextView);
        songArtistName = findViewById(R.id.songArtistTextView);
        albumArt = findViewById(R.id.songAlbumArtImage);
        prevBtn = findViewById(R.id.prevBtnImage);
        nextBtn = findViewById(R.id.nextBtnImage);
        shuffleBtn = findViewById(R.id.shuffleBtnImage);
        repeatBtn = findViewById(R.id.repeatBtnImage);
        playBtn = findViewById(R.id.playBtnImage);
        seekBar = findViewById(R.id.playerSeekbar);
        durationPlayed = findViewById(R.id.durationPlayed);
        durationTotal = findViewById(R.id.durationTotal);
    }

    private static String getFormattedTime(int millis){

        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        return String.format(Locale.ENGLISH,"%02d", hours) +
                ":" +
                String.format(Locale.ENGLISH,"%02d", minutes) +
                ":" +
                String.format(Locale.ENGLISH,"%02d", seconds);
    }
}