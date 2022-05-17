package com.example.myaudioplayerapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaudioplayerapp.R;
import com.example.myaudioplayerapp.models.MusicFile;

import java.util.ArrayList;

public class MusicAdpater extends RecyclerView.Adapter<MusicAdpater.MyViewHolder>{

    private Context mContext;
    private ArrayList<MusicFile> musicFiles;

    public MusicAdpater(Context mContext, ArrayList<MusicFile> musicFiles) {
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
}
