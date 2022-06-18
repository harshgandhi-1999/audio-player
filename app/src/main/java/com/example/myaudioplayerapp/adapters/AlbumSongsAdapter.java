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
import com.example.myaudioplayerapp.utils.Utility;

import java.util.ArrayList;

public class AlbumSongsAdapter extends RecyclerView.Adapter<AlbumSongsAdapter.MyViewHolder> {
    private Context context;
    public static ArrayList<MusicFile> albumSongs;

    public AlbumSongsAdapter(Context context, ArrayList<MusicFile> albumSongs) {
        this.context = context;
        AlbumSongsAdapter.albumSongs = albumSongs;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_item,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.musicNameText.setText(albumSongs.get(position).getTitle());
        holder.artistNameText.setText(albumSongs.get(position).getArtist());
        byte[] image = Utility.getMusicImage(albumSongs.get(position).getPath());
        if(image!=null){
            Glide.with(context)
                    .load(image)
                    .into(holder.musicImage);
        }else{
            Glide.with(context)
                    .load(R.drawable.default_album_art)
                    .into(holder.musicImage);
        }

        holder.itemView.setOnClickListener(view -> {
            // TODO: u have to open player activity to play song but position is not the adapter position
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra("sender","albumDetails");
            intent.putExtra("position",position);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return albumSongs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
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
