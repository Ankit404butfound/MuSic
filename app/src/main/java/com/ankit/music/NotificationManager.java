package com.ankit.music;

public class NotificationManager {


    public static void updateNotification(String song, String artistAlbum){
        Globals.contentView.setTextViewText(R.id.artistAlbum, artistAlbum);
        Globals.contentView.setTextViewText(R.id.song, song);
        MainActivity.notificationManager.notify(1, Globals.builder.build());

    }

}
