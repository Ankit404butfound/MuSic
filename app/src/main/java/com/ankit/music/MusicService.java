package com.ankit.music;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class MusicService extends Service {

    IBinder binder;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("tag", "onStartCommand"+intent.getAction().toString());
        android.app.NotificationManager notificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        NotificationManager.updateNotification(this, intent, notificationManager);
//        if (Globals.contentView == null){
//            createNotification();
//        }
        if (intent.getAction().matches(Globals.PLAY)) {
            if (intent.hasExtra("song")){
                String artistAlbum = intent.getStringExtra("artistAlbum");
                String song = intent.getStringExtra("song");
                Globals.contentView.setTextViewText(R.id.artistAlbum, artistAlbum);
                Globals.contentView.setTextViewText(R.id.song, song);
            }
            Globals.player.start();
            Globals.btn_play_pause.setImageResource(R.drawable.ic_pause);
            Globals.pause = false;
            Globals.contentView.setImageViewResource(R.id.notif_play_pause, R.drawable.ic_pause_notif);
            Intent pauseIntent = new Intent(Globals.PAUSE);
            PendingIntent pause = PendingIntent.getService(getApplicationContext(), 22, pauseIntent, 0);
            Globals.contentView.setOnClickPendingIntent(R.id.notif_play_pause, pause);

            notificationManager.notify(1, Globals.builder.build());
        }

        else if (intent.getAction().matches(Globals.PAUSE)) {
            Globals.player.pause();
            Globals.btn_play_pause.setImageResource(R.drawable.ic_play);
            Globals.pause = true;
            Globals.contentView.setImageViewResource(R.id.notif_play_pause, R.drawable.ic_play_notif);
            Intent playIntent = new Intent(Globals.PLAY);
            PendingIntent play = PendingIntent.getService(getApplicationContext(), 22, playIntent, 0);
            Globals.contentView.setOnClickPendingIntent(R.id.notif_play_pause, play);
            notificationManager.notify(1, Globals.builder.build());
        }

        else if (intent.getAction().matches(Globals.FORWARD)) {
            Globals.musicHandler.playNextSong();
        }

        else if (intent.getAction().matches(Globals.BACK)) {
            Globals.musicHandler.playPreviousSong();
        }

        else if (intent.getAction().matches(Globals.CLOSE)){
            Globals.databaseHandler.saveLastSong(Globals.currentSongid, Globals.player.getCurrentPosition());
            ExitActivity.exitApplication(getApplicationContext());
        }

        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("onCreate", "OnCreate");

//        Notification.Builder builder = android.app.NotificationManager.createNotification(this, getPackageName());
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            createNotificationChannel("channelID", "channelName");
        }

        Globals.contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
        Globals.contentView.setImageViewResource(R.id.notif_play_pause, R.drawable.ic_play_notif);
        Globals.contentView.setTextViewText(R.id.song, Globals.onStartSongData.song);
        Globals.contentView.setTextViewText(R.id.artistAlbum, Globals.onStartSongData.artistAlbum);

        Intent playIntent = new Intent(Globals.PLAY);
        PendingIntent play = PendingIntent.getService(this, 22, playIntent, 0);
        Globals.contentView.setOnClickPendingIntent(R.id.notif_play_pause, play);

        Intent nextIntent = new Intent(Globals.FORWARD);
        PendingIntent next = PendingIntent.getService(getApplicationContext(), 23, nextIntent, 0);
        Globals.contentView.setOnClickPendingIntent(R.id.notif_next, next);

        Intent previousIntent = new Intent(Globals.BACK);
        PendingIntent previous = PendingIntent.getService(getApplicationContext(), 24, previousIntent, 0);
        Globals.contentView.setOnClickPendingIntent(R.id.notif_prev, previous);

        Intent closeIntent = new Intent(getApplicationContext(), MusicService.class);
        closeIntent.setAction(Globals.CLOSE);
        PendingIntent close = PendingIntent.getService(getApplicationContext(), 25, closeIntent, 0);
        Globals.contentView.setOnClickPendingIntent(R.id.close, close);


        Globals.builder =
                new Notification.Builder(this, "channelID")
                        .setSmallIcon(R.drawable.asccime)
                        .setVibrate(new long[]{-1})
                        .setOnlyAlertOnce(true)
                        .setContent(Globals.contentView);
        startForeground(1, Globals.builder.build());

    }

    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        Log.d("tag", "onBind");
        return binder;
    }

    @Override
    public void onDestroy() {
        Log.d("tag", "onDestroy");
        Log.d("ID", String.valueOf(Globals.currentSongid));
        Globals.databaseHandler.saveLastSong(Globals.currentSongid, Globals.player.getCurrentPosition());
        Globals.player.pause();
        Globals.player.reset();
        Globals.player = null;
        this.stopForeground(true);
        //do something you want
        //stop service
        this.stopSelf();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId ,String channelName){
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, android.app.NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        android.app.NotificationManager manager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(chan);
        return channelId;
    }

}
