package com.ankit.music;

import static android.content.ContentValues.TAG;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ImageButton list_control;
    private ImageButton btn_add_song;
    private ImageButton btn_heart;
    public int counter = 0;
    public static android.app.NotificationManager notificationManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String appTitle = getResources().getString(R.string.app_title);
        Globals.databaseHandler = new DatabaseHandler(this);
        Globals.musicHandler = new MusicHandler();
        Globals.player = new MediaPlayer();


        list_control = findViewById(R.id.i_btn_list_ctrl);
        Toolbar toolbar = findViewById(R.id.toolbar);
        Globals.songTitle = findViewById(R.id.lv_song_name);
        Globals.btn_play_pause = findViewById(R.id.btn_pause_play);
        Globals.tv_time_passed = findViewById(R.id.time_passed);
        Globals.tv_total_time = findViewById(R.id.total_time);
        Globals.seekBar = findViewById(R.id.seekbar);
        notificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        btn_heart = findViewById(R.id.btn_heart);
        btn_add_song = findViewById(R.id.btn_add_song);
        ArrayList lastSongData = Globals.databaseHandler.getLastSong();
        ImageButton btn_next = findViewById(R.id.btn_next);
        ImageButton btn_prev = findViewById(R.id.btn_previous);


        btn_heart.setOnClickListener(v -> {

        });

        btn_next.setOnClickListener(c -> {
            Globals.musicHandler.playNextSong();
            if (Globals.contentView == null){
                Intent intent = new Intent(MainActivity.this, MusicService.class);
                intent.setAction(Globals.PLAY);
                startForegroundService(intent);
            }
            else {
                android.app.NotificationManager notificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                Globals.pause = false;
                Globals.contentView.setImageViewResource(R.id.notif_play_pause, R.drawable.ic_pause_notif);
                Intent pauseIntent = new Intent(Globals.PAUSE);
                PendingIntent pause = PendingIntent.getService(getApplicationContext(), 22, pauseIntent, 0);
                Globals.contentView.setOnClickPendingIntent(R.id.notif_play_pause, pause);

                notificationManager.notify(1, Globals.builder.build());
            }
        });

        btn_prev.setOnClickListener(c -> {
            Globals.pause = false;
            if (Globals.contentView == null)
                return;
            Globals.contentView.setImageViewResource(R.id.notif_play_pause, R.drawable.ic_pause_notif);
            Intent pauseIntent = new Intent(Globals.PAUSE);
            PendingIntent pause = PendingIntent.getService(getApplicationContext(), 22, pauseIntent, 0);
            Globals.contentView.setOnClickPendingIntent(R.id.notif_play_pause, pause);

            notificationManager.notify(1, Globals.builder.build());
            Globals.musicHandler.playPreviousSong();
        });

        SpannableString title = new SpannableString(appTitle);
        title.setSpan(new StyleSpan(Typeface.ITALIC), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setSpan(new RelativeSizeSpan(1.5f), 0, 1, 0);
        toolbar.setTitleTextColor(getResources().getColor(R.color.light_grey));
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(getResources().getColor(R.color.raj_red));
        setSupportActionBar(toolbar);


        Handler mHandler = new Handler();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Globals.player != null && !Globals.pause) {
                    int songCurrentTime = Globals.player.getCurrentPosition();

                    Globals.seekBar.setProgress(songCurrentTime);
                    songCurrentTime = songCurrentTime / 1000;
                    Globals.tv_time_passed.setText(String.format("%02d:%02d", songCurrentTime / 60, songCurrentTime % 60));
                }
                mHandler.postDelayed(this, 1);
            }
        });


        // LISTENERS

        btn_add_song.setOnClickListener(c -> {
            Globals.pluscClicked = true;
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
            chooseFile.setType("*/*");
            chooseFile = Intent.createChooser(chooseFile, "Choose a file");
            startActivityForResult(chooseFile, 2222);
        });


        Globals.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (Globals.player != null && fromUser) {
                    Globals.player.seekTo(progress);
                    Globals.tv_time_passed.setText(String.format("%02d:%02d", (progress / 60000) % 60, progress / 1000 % 60));
                }
            }
        });

        Globals.songTitle.setSelected(true);

        Globals.btn_play_pause.setOnClickListener(c -> {
            if (Globals.contentView == null){
                return;
            }
            if (Globals.pause) {
                Globals.btn_play_pause.setImageResource(R.drawable.ic_pause);

                Globals.pause = false;
                Globals.contentView.setImageViewResource(R.id.notif_play_pause, R.drawable.ic_pause_notif);
                Intent pauseIntent = new Intent(Globals.PAUSE);
                PendingIntent pause = PendingIntent.getService(getApplicationContext(), 22, pauseIntent, 0);
                Globals.contentView.setOnClickPendingIntent(R.id.notif_play_pause, pause);

                notificationManager.notify(1, Globals.builder.build());
                Globals.player.start();
            } else {
                Globals.btn_play_pause.setImageResource(R.drawable.ic_play);
                Globals.pause = true;
                Globals.contentView.setImageViewResource(R.id.notif_play_pause, R.drawable.ic_play_notif);
                Intent playIntent = new Intent(Globals.PLAY);
                PendingIntent play = PendingIntent.getService(getApplicationContext(), 22, playIntent, 0);
                Globals.contentView.setOnClickPendingIntent(R.id.notif_play_pause, play);
                notificationManager.notify(1, Globals.builder.build());
                Globals.player.pause();
            }
        });


        list_control.setOnClickListener(c -> {

            switch (Globals.loop_type) {
                case "shuffle":
                    list_control.setImageResource(R.drawable.ic_loop);
                    Globals.loop_type = "loop_list";
                    Toast.makeText(MainActivity.this, "Looping list", Toast.LENGTH_SHORT).show();
                    break;
                case "loop_song":
                    list_control.setImageResource(R.drawable.ic_shuffle);
                    Globals.loop_type = "shuffle";
                    Toast.makeText(MainActivity.this, "List shuffled", Toast.LENGTH_SHORT).show();
                    break;
                case "loop_list":
                    list_control.setImageResource(R.drawable.ic_loop_song);
                    Globals.loop_type = "loop_song";
                    Toast.makeText(MainActivity.this, "Looping song", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
        ArrayList<ArrayList<Object>> songData = Globals.databaseHandler.getAllSong();

        Handler delayHandler = new Handler();
        runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                if (!(counter < songData.size()))
                    return;
                ArrayList<Object> songInfo = songData.get(counter);
                counter++;
                String song = (String) songInfo.get(2);
                String artistAndAlbum = songInfo.get(3) + " | " + songInfo.get(4);
                String filename = Globals.appBasePath + songInfo.get(5);

                Log.d("Tag", lastSongData.toString());

//                TextView containerTv = addSongContainer(song, artistAndAlbum, filename, false);

//                SongData songData = new SongData(containerTv, song, artistAndAlbum, (Integer) songInfo.get(0), filename);

                View view = addSongContainer(song, artistAndAlbum, filename, false);
                SongData songData = new SongData(view.findViewById(R.id.tv_song_info), song, artistAndAlbum, (Integer) songInfo.get(0), filename);

                view.setOnClickListener(v -> {
                    Log.d("song", songData.toString());
                    Globals.onStartSongData = songData;
                    Globals.musicHandler.playSong(songData);
                    Intent intent = new Intent(MainActivity.this, MusicService.class);
                    intent.setAction(Globals.PLAY);
                    intent.putExtra("song", song);
                    intent.putExtra("path", filename);
                    intent.putExtra("artistAlbum", artistAndAlbum);
                    startForegroundService(intent);
                    try {
                        NotificationManager.updateNotification(song, artistAndAlbum);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                });

                Globals.songDataArray.add(songData);
                Globals.songDataArrayClone.add(songData);

//                if (songData.songId == (int)(lastSongData.get(0))){
//                    int progress = (Integer) lastSongData.get(1);
//                    Globals.songDataArray.remove(songData);
//                    Globals.playedSongDataArray.add(songData);
//                    Globals.songTitle.setText(songData.song);
//                    songData.textView.setTextColor(Color.parseColor("#FF0051"));
//                    Globals.musicHandler.playSong(songData.songId, songData.filename, songData)
//                    Globals.player.pause();
//                    Globals.pause = true;
//                    Globals.player.seekTo(progress);
//                    Globals.seekBar.setProgress(progress);
//                    Globals.tv_time_passed.setText(String.format("%02d:%02d", (progress / 60000) % 60, progress / 1000 % 60));
//                }


                delayHandler.postDelayed(this, 50);

                }

        }
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the user doesn't pick a file just return
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 2222 || resultCode != RESULT_OK) {
            Globals.pluscClicked = false;
            return;
        }
        Uri uri = data.getData();
        String path = FileUtil.getPath(uri, this);
        File file = new File(path);
        String fileName = file.getName();
        String song = fileName.substring(0, fileName.lastIndexOf("."));
        String artistAlbum = "IDK | YouTube maybe";

        try {
            File directory = new File(Globals.appBasePath);
            directory.mkdirs();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        Log.d("path", path);
        Log.d("path", Globals.appBasePath + fileName);

        Globals.databaseHandler.addSong(fileName.substring(0, fileName.lastIndexOf(".")), "IDK", "YouTube maybe", fileName);
        try {
            FileUtils.copy(new FileInputStream(path), new FileOutputStream(Globals.appBasePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        addSong(fileName.substring(0, fileName.lastIndexOf(".")), "IDK", "YouTube maybe", fileName);
        View view = addSongContainer(song, artistAlbum, fileName, true);
        SongData songData = new SongData(view.findViewById(R.id.tv_song_info), song, artistAlbum, Globals.databaseHandler.getMaxSongId(), fileName);

        view.setOnClickListener(v -> {
            NotificationManager.updateNotification(song, artistAlbum);
            Globals.musicHandler.playSong(songData);
            Intent intent = new Intent(this, MusicService.class);
            intent.setAction(Globals.PLAY);
            intent.putExtra("song", song);
            intent.putExtra("path", fileName);
            intent.putExtra("artistAlbum", artistAlbum);
            startForegroundService(intent);
        });

        Globals.songDataArray.add(songData);
        Globals.songDataArrayClone.add(songData);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View addSongContainer(String song, String artistAlbum, String filename, Boolean addToFirst) {
        Log.d("path", filename);
        LinearLayout ll = findViewById(R.id.ll_song_list);
        View view = getLayoutInflater().inflate(R.layout.song_container_layout, null);
        if (addToFirst)
            ll.addView(view, 0);
        else
            ll.addView(view);

        
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//
//        StrictMode.setThreadPolicy(policy);
//
//        URL newurl = null;
//        try {
//            newurl = new URL("https://img.youtube.com/vi/p-EGZPWiWsQ/hqdefault.jpg");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        ImageView imageView = view.findViewById(R.id.rounded_user_image);
//        try {
//            imageView.setImageBitmap(BitmapFactory.decodeStream(newurl.openConnection() .getInputStream()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        TextView textView = view.findViewById(R.id.tv_song_info);
        textView.setText(song);
        ((TextView) view.findViewById(R.id.tv_artist_info)).setText(artistAlbum);
        float pixels = 60 * getResources().getDisplayMetrics().density;
        view.getLayoutParams().height = (int) pixels;
        return view;
    }

    public void addSong(String song, String artist, String album, String filename) {


//        RecyclerView.ViewHolder holder = Globals.recyclerView.findViewHolderForAdapterPosition(0);
//        RelativeLayout insertedRL = holder.itemView.findViewById(R.id.rl_parent);
//        ArrayList<Object> array_member = new ArrayList<>();
//        array_member.add(insertedRL);
//        array_member.add(filename);
//
//        Globals.songDataArrayClone.add(0, array_member);
//        Globals.songDataArray.add(0, array_member);
    }
}

//            String sName = "Tum Mile to jina aa gya";
//            String artistAndAlbum = "Arijit Singh | Youtube";
//
//            SpannableString songDetails = new SpannableString(sName + "\n" + artistAndAlbum);
//            songDetails.setSpan(new StyleSpan(Typeface.BOLD), 0, sName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            songDetails.setSpan(new RelativeSizeSpan(1.5f), 0, sName.length(), 0);
//
//            myListData.add(0, new SongInfo(songDetails, 1234, android.R.drawable.ic_dialog_email));
//
//            adapter.notifyItemInserted(0);
//
//            Globals.recyclerView.smoothScrollToPosition(0);
//
//            databaseHandler.addSong("Eak tum hi ho", "IDK", "YouTube maybe");
//


//            lv_song_info.setText(songDetails);
//            songsContainer.addView(songContainer);
//            float pixels = 65 * getResources().getDisplayMetrics().density;
//            songContainer.getLayoutParams().height = (int) pixels;


//                            mediaPlayer.stop();
//                            mediaPlayer.release();
//                            mediaPlayer = null;
//                            player = null;


//        try {
//            Log.d("File Path", path);
//
//            player.setDataSource(path);
//            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mediaPlayer) {
//                    mediaPlayer.start();
//                    int songDuration = mediaPlayer.getDuration();
//                    seekBar.setMax(songDuration);
//                    tv_total_time.setText(String.format("%02d:%02d", (songDuration/60000)%60, (songDuration/1000)%60));
//                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mediaPlayer) {
//                            int songCurrentTime = player.getCurrentPosition();
//                            seekBar.setProgress(songCurrentTime);
//                            songCurrentTime = songCurrentTime / 1000;
//                            tv_time_passed.setText(String.format("%02d:%02d", songCurrentTime/60, songCurrentTime%60));
//                            pause = true;
//                            btn_play_pause.setImageResource(R.drawable.ic_play);
//                        }
//                    });
//                }
//            });
//            player.prepare();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        player.start();
//    }