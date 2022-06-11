package com.example.myaudioplayerapp.fragments;

import static com.example.myaudioplayerapp.MainActivity.albums;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myaudioplayerapp.R;
import com.example.myaudioplayerapp.adapters.AlbumAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlbumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumFragment extends Fragment {

    private RecyclerView albumsRecView;
    private AlbumAdapter albumAdapter;


    public AlbumFragment() {
        // Required empty public constructor
    }


    public static AlbumFragment newInstance() {
        return new AlbumFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        albumsRecView = view.findViewById(R.id.albumsRecView);
        albumsRecView.setHasFixedSize(true);
        albumsRecView.setItemViewCacheSize(20);

        if(albums.size()>=1){
            albumAdapter = new AlbumAdapter(getContext(),albums);
            albumsRecView.setAdapter(albumAdapter);
            albumsRecView.setLayoutManager(new GridLayoutManager(getContext(),2));
        }

        return view;
    }
}