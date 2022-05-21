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
import com.example.myaudioplayerapp.PlayerActivity;
import com.example.myaudioplayerapp.R;
import com.example.myaudioplayerapp.models.MusicFile;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder>{

    private Context mContext;
    private ArrayList<MusicFile> musicFiles;

    public MusicAdapter(Context mContext, ArrayList<MusicFile> musicFiles) {
        this.mContext = mContext;
        this.musicFiles = musicFiles;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_item, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.musicNameText.setText(musicFiles.get(position).getTitle());
        holder.artistNameText.setText(musicFiles.get(position).getArtist());

        byte[] image = getMusicImage(musicFiles.get(position).getPath());
        if(image!=null){
            Glide.with(mContext)
                    .asBitmap()
                    .load(image)
                    .into(holder.musicImage);
        }

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, PlayerActivity.class);
            intent.putExtra("position",holder.getAdapterPosition());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return musicFiles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView musicImage;
        private TextView musicNameText;
        private TextView artistNameText;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            musicImage  = itemView.findViewById(R.id.musicImage);
            musicNameText = itemView.findViewById(R.id.musicNameText);
            artistNameText = itemView.findViewById(R.id.artistNameText);
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
