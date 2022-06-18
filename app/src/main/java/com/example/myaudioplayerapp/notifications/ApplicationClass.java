package com.example.myaudioplayerapp.notifications;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class ApplicationClass extends Application {
    public static final String CHANNEL_ID_1 = "channel1";
    public static final String CHANNEL_ID_2 = "channel2";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PLAY = "action_play";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel(){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            int importance = NotificationManager.IMPORTANCE_HIGH;


            NotificationChannel channel1 = new NotificationChannel(CHANNEL_ID_1,"Channel(1)",importance);
            channel1.setDescription("Channel 1 Desc...");

            NotificationChannel channel2 = new NotificationChannel(CHANNEL_ID_2,"Channel(2)",importance);
            channel2.setDescription("Channel 2 Desc...");



            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);
        }
    }
}
