package com.example.myaudioplayerapp.fragments;

import static com.example.myaudioplayerapp.MainActivity.musicFiles;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myaudioplayerapp.R;
import com.example.myaudioplayerapp.adapters.MusicAdapter;

public class SongFragment extends Fragment {

    private RecyclerView songsRecView;
    private MusicAdapter musicAdapter;

    public SongFragment() {
        // Required empty public constructor
    }

    private static SongFragment instance = null;

    public static SongFragment getInstance() {
        if(instance==null){
            instance = new SongFragment();
        }

        return instance;
    }


    public MusicAdapter getMusicAdapter(){
        return musicAdapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song, container, false);

        songsRecView = view.findViewById(R.id.songsRecView);
        songsRecView.setHasFixedSize(true);
        songsRecView.setItemViewCacheSize(20);

        if(musicFiles.size()>=1){
            musicAdapter = new MusicAdapter(getContext(),musicFiles);
            songsRecView.setAdapter(musicAdapter);
            songsRecView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        }


        return view;
    }
}