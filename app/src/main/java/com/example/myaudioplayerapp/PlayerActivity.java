package com.example.myaudioplayerapp;

import static com.example.myaudioplayerapp.MainActivity.currentMusicPLayMode;
import static com.example.myaudioplayerapp.MainActivity.musicFiles;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myaudioplayerapp.models.MusicFile;
import com.example.myaudioplayerapp.services.MusicPlayerService;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity{

    private static final String TAG = "PlayerActivity";
    private TextView songTitle,songArtistName,durationPlayed,durationTotal;
    private ImageView albumArt,prevBtn,nextBtn,playBtn,backBtn,musicPlayModeBtn,favoriteBtn;
    private SeekBar seekBar;
    static ArrayList<MusicFile> songsList = new ArrayList<>();
    private MediaPlayer mediaPlayer = null;
    private Handler handler = new Handler();

    // new vars
    private MusicPlayerService musicPlayerService;
    private Intent playIntent;
    private boolean musicBound=false;
    private Runnable runnable;

    int position = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // initialize all the views
        initViews();

        // get intent data
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

        nextBtn.setOnClickListener(view -> playNextSong());

        prevBtn.setOnClickListener(view-> playPrevSong());

        backBtn.setOnClickListener(view -> onBackBtnPressed());

        musicPlayModeBtn.setOnClickListener(view -> handleMusicPlayModeChange());

        favoriteBtn.setOnClickListener(view -> handleMarkFavorite());
    }

    private void handleMarkFavorite() {
        // toggle favorite
        Toast.makeText(this,"Marked Favortie",Toast.LENGTH_SHORT).show();
    }

    private void handleMusicPlayModeChange() {
        switch (currentMusicPLayMode){
            case REPEAT:
                currentMusicPLayMode = MusicPlayMode.REPEAT_ONE;
                musicPlayModeBtn.setImageResource(R.drawable.ic_repeat_one);
                break;
            case REPEAT_ONE:
                currentMusicPLayMode = MusicPlayMode.SHUFFLE;
                musicPlayModeBtn.setImageResource(R.drawable.ic_shuffle);
                break;
            case SHUFFLE:
                currentMusicPLayMode = MusicPlayMode.REPEAT;
                musicPlayModeBtn.setImageResource(R.drawable.ic_repeat);
                break;
            default:
                break;
        }
    }

    private void onBackBtnPressed() {
        super.onBackPressed();
    }

    private void playCycle(){

        if(mediaPlayer!=null){
            try {
                // can generate illegal state exception for mediaPlayer
                int currentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(currentPosition/1000);
                durationPlayed.setText(getFormattedTime(currentPosition));
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if(mediaPlayer!=null){
                            //in milli seconds
                            playCycle();
                            Log.i(TAG, "run: " + currentPosition);
                        }
                    }
                };
                handler.postDelayed(runnable,1000);
            }catch (Exception e){
                handler.removeCallbacks(runnable);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicPlayerService.class);
            musicBound = bindService(playIntent, musicConnection, BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(playIntent);
        handler.removeCallbacks(runnable);
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
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                // set initial position
                int currentPosition = mp.getCurrentPosition();
                seekBar.setProgress(currentPosition/1000);
                durationPlayed.setText(getFormattedTime(currentPosition));

                // set total duration
                seekBar.setMax(mp.getDuration()/1000);
                durationTotal.setText(getFormattedTime(mp.getDuration()));

                // is not playing that means play btn is shown so we have to change it to paused
                if(mp.isPlaying()){
                    playBtn.setImageResource(R.drawable.ic_pause);
                }else{
                    playBtn.setImageResource(R.drawable.ic_play);
                }

                // post the notification
                musicPlayerService.postNotification("Music is playing");


                playCycle();
            });

            mediaPlayer.setOnCompletionListener(mp -> playNextSong());

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
        }
    }

    private void playPrevSong() {
        switch (currentMusicPLayMode){
            case REPEAT:
                if(position==0){
                    position = songsList.size()-1;
                }else{
                    position = position-1;
                }
                break;
            case SHUFFLE:
                position = getRandomPosition(songsList.size());
                break;
            default:
                break;
        }
        playSong();
    }

    private void playNextSong() {
        switch (currentMusicPLayMode){
            case REPEAT:
                position = (position+1)%songsList.size();
                break;
            case SHUFFLE:
                position = getRandomPosition(songsList.size());
                break;
            default:
                break;
        }

        playSong();
    }

    private void setAlbumArt(Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        byte[] data = retriever.getEmbeddedPicture();
        Bitmap bitmap;

        if(data!=null){
            bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
            imageAnimation(this,albumArt,bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    assert palette != null;
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    ImageView imageGradient = findViewById(R.id.imageViewGradient);
                    RelativeLayout layout = findViewById(R.id.playerContainer);
                    imageGradient.setBackgroundResource(R.drawable.gradient_bg);
                    layout.setBackgroundResource(R.drawable.player_bg);
                    GradientDrawable gradientDrawable;
                    if (swatch != null) {

                        gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), 0x00000000});
                        imageGradient.setBackground(gradientDrawable);

                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), swatch.getRgb()});
                        layout.setBackground(gradientDrawableBg);

                        songTitle.setTextColor(swatch.getTitleTextColor());
                        songArtistName.setTextColor(swatch.getBodyTextColor());
                    } else {

                        gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0xff000000, 0x00000000});
                        imageGradient.setBackground(gradientDrawable);

                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0xff000000, 0xff000000});
                        layout.setBackground(gradientDrawableBg);

                        songTitle.setTextColor(Color.WHITE);
                        songArtistName.setTextColor(getColor(R.color.grey));
                    }
                }
            });

        }else{
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.default_album_art)
                    .into(albumArt);

            ImageView imageGradient = findViewById(R.id.imageViewGradient);
            RelativeLayout layout = findViewById(R.id.playerContainer);
            imageGradient.setBackgroundResource(R.drawable.gradient_bg);
            layout.setBackgroundResource(R.drawable.player_bg);
            songTitle.setTextColor(Color.WHITE);
            songArtistName.setTextColor(getColor(R.color.grey));
        }
    }

    private void initViews() {
        songTitle = findViewById(R.id.songTitleTextView);
        songArtistName = findViewById(R.id.songArtistTextView);
        albumArt = findViewById(R.id.songAlbumArtImage);
        prevBtn = findViewById(R.id.prevBtnImage);
        nextBtn = findViewById(R.id.nextBtnImage);
        playBtn = findViewById(R.id.playBtnImage);
        seekBar = findViewById(R.id.playerSeekbar);
        durationPlayed = findViewById(R.id.durationPlayed);
        durationTotal = findViewById(R.id.durationTotal);
        backBtn = findViewById(R.id.backBtn);
        musicPlayModeBtn = findViewById(R.id.musicPlayModeImage);
        favoriteBtn = findViewById(R.id.favoriteBtnImage);

        // set music play mode
        setMusicPlayModeIcon();
    }

    private void setMusicPlayModeIcon() {
        switch (currentMusicPLayMode){
            case REPEAT:
                musicPlayModeBtn.setImageResource(R.drawable.ic_repeat);
                break;
            case REPEAT_ONE:
                musicPlayModeBtn.setImageResource(R.drawable.ic_repeat_one);
                break;
            case SHUFFLE:
                musicPlayModeBtn.setImageResource(R.drawable.ic_shuffle);
            default:
                break;
        }
    }

    private static int getRandomPosition(int size){
        Random random = new Random();
        return random.nextInt(size);
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

    private void imageAnimation(Context context,ImageView imageView,Bitmap bitmap){
        Animation animOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
        Animation animIn = AnimationUtils.loadAnimation(context,R.anim.fade_in);

        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);
    }
}