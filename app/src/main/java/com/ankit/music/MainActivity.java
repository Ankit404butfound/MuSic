package com.ankit.music;

import static android.content.ContentValues.TAG;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
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


/* TODO:
    2. Edit Song
    3. Search
    4. Favourites
*/

public class MainActivity extends AppCompatActivity {
    private ImageButton list_control;
    private ImageButton btn_add_song;
    private ImageButton btn_heart;
    private ScrollView sv_main;
    private ScrollView sv_search;
    private Boolean isSearchViewVisible = false;
    private Runnable searcher;
    private Handler search_handler;
    private android.widget.SearchView mSearchView;
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
        Globals.ll = findViewById(R.id.ll_song_list);

        list_control = findViewById(R.id.i_btn_list_ctrl);
        sv_main = findViewById(R.id.sv_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        Globals.songTitle = findViewById(R.id.lv_song_name);
        Globals.btn_play_pause = findViewById(R.id.btn_pause_play);
        Globals.tv_time_passed = findViewById(R.id.time_passed);
        Globals.tv_total_time = findViewById(R.id.total_time);
        Globals.seekBar = findViewById(R.id.seekbar);
        notificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        btn_heart = findViewById(R.id.btn_heart);
//        btn_add_song = findViewById(R.id.btn_add_song);
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

        toolbar.setOverflowIcon(getDrawable(R.drawable.ic_iconmonstr_plus));
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
                mHandler.postDelayed(this, 0);
            }
        });


        // LISTENERS

//        btn_add_song.setOnClickListener(c -> {
//            Globals.pluscClicked = true;
//            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
//            chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
//            chooseFile.setType("*/*");
//            chooseFile = Intent.createChooser(chooseFile, "Choose a file");
//            startActivityForResult(chooseFile, 2222);
//        });


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
                Log.d("Indisde", "populator");
                if (!(counter < songData.size())) {
                    delayHandler.removeCallbacks(this);
                    return;
                }
                ArrayList<Object> songInfo = songData.get(counter);
                counter++;
                String song = (String) songInfo.get(2);
                String artistAndAlbum = songInfo.get(3) + " | " + songInfo.get(4);
                String filename = Globals.appBasePath + songInfo.get(5);

                Log.d("Tag", lastSongData.toString());

                View view = addSongContainer(song, artistAndAlbum, filename, false);
                SongData songData = new SongData(view.findViewById(R.id.tv_song_info), song, artistAndAlbum, (Integer) songInfo.get(0), filename);

                ImageButton i_btn_song_config = view.findViewById(R.id.i_btn_song_config);
                i_btn_song_config.setOnClickListener(v ->{
                    showMenu(songData, i_btn_song_config, view);
                });

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


                delayHandler.postDelayed(this, 50);

                }

        }
        );
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.add_from_sd) {
            if (checkSelfPermission(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 22);

            }
            Globals.pluscClicked = true;
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
            chooseFile.setType("*/*");
            chooseFile = Intent.createChooser(chooseFile, "Choose a file");
            startActivityForResult(chooseFile, 2222);
        }

        if (id == R.id.add_from_net){
            Intent intent = new Intent(MainActivity.this, AddSong.class);
            startActivityForResult(intent, 2223);
        }

        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the user doesn't pick a file just return
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("code", String.valueOf(requestCode));
        if (requestCode == 2223){
            addNewDownloadedSong(data.getStringExtra("filename"), data.getStringExtra("song"), data.getStringExtra("imgPath"));
            return;
        }
        if (requestCode != 2222 || resultCode != RESULT_OK) {
            Globals.pluscClicked = false;
            return;
        }
        Uri uri = data.getData();
        String path = FileUtil.getPath(uri, this);
        File file = new File(path);
        String fileName = file.getName();
        String song = fileName.substring(0, fileName.lastIndexOf("."));
        String artistAlbum = "Unknown | Unknown";

        try {
            File directory = new File(Globals.appBasePath);
            directory.mkdirs();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        Log.d("path", path);
        Log.d("path", Globals.appBasePath + fileName);

        Globals.databaseHandler.addSong(fileName.substring(0, fileName.lastIndexOf(".")), "Unknown", "Unknown", fileName);
        try {
            FileUtils.copy(new FileInputStream(path), new FileOutputStream(Globals.appBasePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        addSong(fileName.substring(0, fileName.lastIndexOf(".")), "IDK", "YouTube maybe", fileName);
        View view = addSongContainer(song, artistAlbum, Globals.appBasePath+fileName, true);
        SongData songData = new SongData(view.findViewById(R.id.tv_song_info), song, artistAlbum, Globals.databaseHandler.getMaxSongId(), Globals.appBasePath+fileName);

        ImageButton i_btn_song_config = view.findViewById(R.id.i_btn_song_config);
        i_btn_song_config.setOnClickListener(v ->{
            showMenu(songData, i_btn_song_config, view);
        });

        view.setOnClickListener(v -> {
            Log.d("song", songData.toString());
            Globals.onStartSongData = songData;
            Globals.musicHandler.playSong(songData);
            Intent intent = new Intent(this, MusicService.class);
            intent.setAction(Globals.PLAY);
            intent.putExtra("song", song);
            intent.putExtra("path", Globals.appBasePath+fileName);
            intent.putExtra("artistAlbum", artistAlbum);
            startForegroundService(intent);
            try {
                NotificationManager.updateNotification(song, artistAlbum);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        });

        Globals.songDataArray.add(songData);
        Globals.songDataArrayClone.add(songData);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View addSongContainer(String song, String artistAlbum, String filename, Boolean addToFirst) {
        Log.d("path", filename);
        View view;
        LinearLayout ll;
        ll = findViewById(R.id.ll_song_list);
        view = getLayoutInflater().inflate(R.layout.song_container_layout, null);

        if (addToFirst)
            ll.addView(view, 0);
        else
            ll.addView(view);

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

    public void showMenu(SongData songData, View view, View parentView){
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);//View will be an anchor for PopupMenu
        popupMenu.inflate(R.menu.song_config_menu);
        Menu menu = popupMenu.getMenu();
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.item_delete){
                LinearLayout ll = findViewById(R.id.ll_song_list);
                ll.removeView(parentView);
                Globals.songDataArray.remove(songData);
                Globals.songDataArrayClone.remove(songData);
                Globals.playedSongDataArray.remove(songData);
                Globals.databaseHandler.deleteSong(songData.songId);
            }
            return true;
        });
        popupMenu.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addNewDownloadedSong(String filename, String song, String imgPath){
        Log.d("datassss", filename+song+imgPath);
        Globals.databaseHandler.addSong(filename.substring(0, filename.lastIndexOf(".")), "Unknown", "Unknown", filename);
        filename = Globals.appBasePath + filename;
        View view = addSongContainer(song, "Unknown | Unknown", filename, true);
        SongData songData = new SongData(view.findViewById(R.id.tv_song_info), song, "Unknown | Unknown", (Integer) Globals.databaseHandler.getMaxSongId(), filename);

        ImageButton i_btn_song_config = view.findViewById(R.id.i_btn_song_config);
        i_btn_song_config.setOnClickListener(v ->{
            showMenu(songData, i_btn_song_config, view);
        });

        String finalFilename = filename;
        view.setOnClickListener(v -> {
            Log.d("song", songData.toString());
            Globals.onStartSongData = songData;
            Globals.musicHandler.playSong(songData);
            Intent intent = new Intent(MainActivity.this, MusicService.class);
            intent.setAction(Globals.PLAY);
            intent.putExtra("song", song);
            intent.putExtra("path", finalFilename);
            intent.putExtra("artistAlbum", "Unknown | Unknown");
            startForegroundService(intent);
            try {
                NotificationManager.updateNotification(song, "Unknown | Unknown");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        });

        Globals.songDataArray.add(0, songData);
        Globals.songDataArrayClone.add(0, songData);
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