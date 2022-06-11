package com.example.myaudioplayerapp.adapters;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myaudioplayerapp.R;
import com.example.myaudioplayerapp.models.MusicFile;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<MusicFile> musicFiles;

    public AlbumAdapter(Context context, ArrayList<MusicFile> musicFiles) {
        this.context = context;
        this.musicFiles = musicFiles;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.album_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.albumName.setText(musicFiles.get(position).getAlbum());

        byte[] image = getMusicImage(musicFiles.get(position).getPath());
        if(image!=null){
            Glide.with(context)
                    .asBitmap()
                    .load(image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.albumArt);
        }
    }

    @Override
    public int getItemCount() {
        return musicFiles.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView albumArt;
        private TextView albumName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            albumArt = itemView.findViewById(R.id.albumArtImageView);
            albumName = itemView.findViewById(R.id.albumNameTextView);
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
