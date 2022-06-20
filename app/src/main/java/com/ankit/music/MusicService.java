package com.ankit.music;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
    private static final int ID_SERVICE = 101;
    IBinder binder;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("tag", "onStartCommand");
        if (intent.getAction().matches("change")) {
            Globals.contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
            Globals.contentView.setImageViewResource(R.id.image, R.drawable.ic_play);
            Globals.contentView.setTextViewText(R.id.title, "Custom notification");
            Globals.contentView.setTextViewText(R.id.text, "Playing");


            Globals.builder = new Notification.Builder(this, "channelID")
                    .setSmallIcon(R.drawable.asccime)
                    .setContent(Globals.contentView);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, Globals.builder.build());
        }

        return START_NOT_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        Intent intent = new Intent("com.ankit.music.action.PLAY");
        PendingIntent pi = PendingIntent.getBroadcast(this, 100, intent, 0);

        Globals.contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
        Globals.contentView.setImageViewResource(R.id.image, R.drawable.ic_play);
        Globals.contentView.setTextViewText(R.id.title, "Custom notification");
        Globals.contentView.setTextViewText(R.id.text, "This is a custom layout");

        Intent pauseIntent = new Intent(getApplicationContext(), MusicService.class);
        Intent dismissIntent = new Intent(getApplicationContext(), MusicService.class);

        pauseIntent.setAction("change");
        dismissIntent.setAction("difhvuidf");

        PendingIntent pause = PendingIntent.getService(getApplicationContext(), 22, pauseIntent, 0);
        PendingIntent dismiss = PendingIntent.getService(getApplicationContext(), 23, dismissIntent, 0);

        Globals.contentView.setOnClickPendingIntent(R.id.image, pause);

        Globals.builder =
                new Notification.Builder(this, "channelID")
                        .setSmallIcon(R.drawable.asccime)
                        .setContent(Globals.contentView);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = 1;
//        createChannel(notificationManager);

// Notification ID cannot be 0.
        startForeground(1, Globals.builder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        Log.d("tag", "onBind");
        return binder;
    }


    public void createChannel(NotificationManager notificationManager){
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationChannel channel = new NotificationChannel("channelID","name", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Hello! This is a notification.");
        notificationManager.createNotificationChannel(channel);
    }
}
