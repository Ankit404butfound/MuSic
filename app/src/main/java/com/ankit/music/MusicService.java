package com.ankit.music;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
        if (intent.getAction().matches(Globals.PLAY)) {
            if (intent.hasExtra("song")){
                String artistAlbum = intent.getStringExtra("artistAlbum");
                String song = intent.getStringExtra("song");
                Globals.contentView.setTextViewText(R.id.artistAlbum, artistAlbum);
                Globals.contentView.setTextViewText(R.id.song, song);
            }
            Globals.player.start();
            Globals.contentView.setImageViewResource(R.id.image, R.drawable.ic_pause_notif);
            Intent pauseIntent = new Intent(Globals.PAUSE);
            PendingIntent pause = PendingIntent.getService(getApplicationContext(), 22, pauseIntent, 0);
            Globals.contentView.setOnClickPendingIntent(R.id.image, pause);

            notificationManager.notify(1, Globals.builder.build());
        }

        if (intent.getAction().matches(Globals.PAUSE)) {
            Globals.player.pause();
            Globals.contentView.setImageViewResource(R.id.image, R.drawable.ic_play_notif);
            Intent playIntent = new Intent(Globals.PLAY);
            PendingIntent play = PendingIntent.getService(getApplicationContext(), 22, playIntent, 0);
            Globals.contentView.setOnClickPendingIntent(R.id.image, play);
            notificationManager.notify(1, Globals.builder.build());
        }

        return START_NOT_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

//        Notification.Builder builder = android.app.NotificationManager.createNotification(this, getPackageName());

        Globals.contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
        Globals.contentView.setImageViewResource(R.id.image, R.drawable.ic_play_notif);
        Globals.contentView.setTextViewText(R.id.song, "Custom notification");
        Globals.contentView.setTextViewText(R.id.artistAlbum, "This is a custom layout");

        Intent playIntent = new Intent(Globals.PLAY);

        PendingIntent play = PendingIntent.getService(this, 22, playIntent, 0);

        Globals.contentView.setOnClickPendingIntent(R.id.image, play);

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
        Globals.player.pause();
        Globals.player.reset();
        Globals.player = null;
        this.stopForeground(true);
        //do something you want
        //stop service
        this.stopSelf();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
