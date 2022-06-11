package com.example.myaudioplayerapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.example.myaudioplayerapp.MainActivity.musicFiles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myaudioplayerapp.adapters.AlbumSongsAdapter;
import com.example.myaudioplayerapp.models.MusicFile;

import java.util.ArrayList;

public class AlbumDetailActivity extends AppCompatActivity {

    private RecyclerView albumSongsRecView;
    private ImageView albumPhoto;
    private String albumName;
    private ArrayList<MusicFile> albumSongs = new ArrayList<>();
    private AlbumSongsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);

        initViews();
        setAlbumName();
        setAlbumSongs();

        Toast.makeText(this, albumName, Toast.LENGTH_SHORT).show();
    }

    private void setAlbumName() {
        albumName = getIntent().getStringExtra("albumName");
    }

    private void initViews() {
        albumSongsRecView = findViewById(R.id.albumSongsRecView);
        albumPhoto = findViewById(R.id.albumPhoto);

        albumSongsRecView.setHasFixedSize(true);
        albumSongsRecView.setItemViewCacheSize(20);
    }

    private void setAlbumSongs() {

        for (int i=0;i<musicFiles.size();i++){
            if(albumName.equals(musicFiles.get(i).getAlbum())){
                albumSongs.add(musicFiles.get(i));
            }
        }

        byte[] image = getMusicImage(albumSongs.get(0).getPath());
        if(image!=null){
            Glide.with(this)
                    .load(image)
                    .into(albumPhoto);
        }else{
            Glide.with(this)
                    .load(R.drawable.default_album_art)
                    .into(albumPhoto);
        }

        for(int i=0;i<albumSongs.size();i++){
            Log.i(TAG, "setAlbumSongs: " + albumSongs.get(i).getTitle());
        }

        if(albumSongs.size()>=1){
            adapter = new AlbumSongsAdapter(this,albumSongs);
            albumSongsRecView.setAdapter(adapter);
            albumSongsRecView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        }

    }

    private byte[] getMusicImage(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] image = retriever.getEmbeddedPicture();
        retriever.release();

        return image;
    }
}