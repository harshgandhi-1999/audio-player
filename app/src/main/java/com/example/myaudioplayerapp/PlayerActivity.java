package com.example.myaudioplayerapp;

import static com.example.myaudioplayerapp.MainActivity.musicFiles;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.myaudioplayerapp.models.MusicFile;

import java.util.ArrayList;
import java.util.Locale;

public class PlayerActivity extends AppCompatActivity {

    private TextView songTitle,songArtistName,durationPlayed,durationTotal;
    private ImageView albumArt,prevBtn,nextBtn,shuffleBtn,repeatBtn,playBtn;
    private SeekBar seekBar;
    static ArrayList<MusicFile> songsList = new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;
    private Handler handler = new Handler();

    int position = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // initialize all the views
        initViews();

        getIntentMethod();

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
    }

    private void getIntentMethod() {
        position = getIntent().getIntExtra("position",-1);
        songsList = musicFiles;

        if(songsList!=null){
            MusicFile musicFile = songsList.get(position);
            songTitle.setText(musicFile.getTitle());
            songArtistName.setText(musicFile.getArtist());

            // setting up pause button on playing of song
            playBtn.setImageResource(R.drawable.ic_pause);

            // getting path to play song
            uri = Uri.parse(musicFile.getPath());
        }

        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this,uri);
        mediaPlayer.start();

        // set seekbar total duration
        seekBar.setMax(mediaPlayer.getDuration()/1000);
        durationTotal.setText(getFormattedTime(mediaPlayer.getDuration()));
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

    private String getFormattedTime(int millis){

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