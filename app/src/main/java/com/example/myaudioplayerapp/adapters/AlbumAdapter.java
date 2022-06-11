package com.example.myaudioplayerapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myaudioplayerapp.AlbumDetailActivity;
import com.example.myaudioplayerapp.R;
import com.example.myaudioplayerapp.models.MusicFile;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<MusicFile> albumFiles;

    public AlbumAdapter(Context context, ArrayList<MusicFile> albumFiles) {
        this.context = context;
        this.albumFiles = albumFiles;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.album_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.albumName.setText(albumFiles.get(position).getAlbum());

        byte[] image = getMusicImage(albumFiles.get(position).getPath());
        if(image!=null){
            Glide.with(context)
                    .load(image)
                    .into(holder.albumArt);
        }else{
            Glide.with(context)
                    .load(R.drawable.default_album_art)
                    .into(holder.albumArt);
        }

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, AlbumDetailActivity.class);
            intent.putExtra("albumName",albumFiles.get(position).getAlbum());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
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
