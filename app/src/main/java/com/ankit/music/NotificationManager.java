package com.ankit.music;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;

public class NotificationManager {


    public static void updateNotification(String song, String artistAlbum){
        Globals.contentView.setTextViewText(R.id.artistAlbum, artistAlbum);
        Globals.contentView.setTextViewText(R.id.song, song);
        MainActivity.notificationManager.notify(1, Globals.builder.build());
    }
}
