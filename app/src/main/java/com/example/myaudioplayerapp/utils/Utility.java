package com.example.myaudioplayerapp.utils;

import android.media.MediaMetadataRetriever;

import java.util.Locale;

public class Utility {
    public static String getFormattedTime(int millis){
        int hours = millis / (1000 * 60 * 60);
        int minutes = (millis % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = ((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000;

        return String.format(Locale.ENGLISH,"%02d", hours) +
                ":" +
                String.format(Locale.ENGLISH,"%02d", minutes) +
                ":" +
                String.format(Locale.ENGLISH,"%02d", seconds);
    }

    public static byte[] getMusicImage(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] image = retriever.getEmbeddedPicture();
        retriever.release();

        return image;
    }
}
