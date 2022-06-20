package com.ankit.music;

import static android.content.ContentValues.TAG;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ImageButton list_control;
    private ImageButton btn_add_song;
    public int counter = 0;

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
//        Globals.recyclerView = findViewById(R.id.rv_song_list);
        btn_add_song = findViewById(R.id.btn_add_song);
        ImageButton btn_next = findViewById(R.id.btn_next);

        btn_next.setOnClickListener(c -> {
            Globals.musicHandler.playNextSong();
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
            if (Globals.pause) {
                Globals.btn_play_pause.setImageResource(R.drawable.ic_pause);
                Globals.pause = false;
                Globals.player.start();
            } else {
                Globals.btn_play_pause.setImageResource(R.drawable.ic_play);
                Globals.pause = true;
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
                String artistAndAlbum = (String) songInfo.get(3) + " | " + (String) songInfo.get(4);
                String filename = Globals.appBasePath + songInfo.get(5);

                Log.d(TAG, filename);

                TextView containerTv = addSongContainer(song, artistAndAlbum, filename, false);

                SongLayoutInfo songLayoutInfo = new SongLayoutInfo(containerTv, song, artistAndAlbum, (Integer) songInfo.get(0), filename);
                Globals.songDataArray.add(songLayoutInfo);
                Globals.songDataArrayClone.add(songLayoutInfo);


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
        addSong(fileName.substring(0, fileName.lastIndexOf(".")), "IDK", "YouTube maybe", fileName);
        TextView textView = addSongContainer(fileName.substring(0, fileName.lastIndexOf(".")), "IDK | YouTube maybe", fileName, true);
        SongLayoutInfo songLayoutInfo = new SongLayoutInfo(textView, fileName.substring(0, fileName.lastIndexOf(".")), "IDK | YouTube maybe", Globals.databaseHandler.getMaxSongId(), fileName);
        Globals.songDataArray.add(songLayoutInfo);
        Globals.songDataArrayClone.add(songLayoutInfo);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public TextView addSongContainer(String song, String artistAlbum, String filename, Boolean addToFirst) {
        Log.d("path", filename);
        LinearLayout ll = findViewById(R.id.ll_song_list);
        View view = getLayoutInflater().inflate(R.layout.song_container_layout, null);
        if (addToFirst)
            ll.addView(view, 0);
        else
            ll.addView(view);
        TextView textView = view.findViewById(R.id.tv_song_info);
        textView.setText(song);
        ((TextView) view.findViewById(R.id.tv_artist_info)).setText(artistAlbum);
        float pixels = 60 * getResources().getDisplayMetrics().density;
        view.getLayoutParams().height = (int) pixels;
        view.setOnClickListener(v -> {
//            Globals.musicHandler.playSong(filename)
            Intent intent = new Intent(this, MusicService.class);
            intent.setAction(filename);
            startForegroundService(intent);
        });
        return textView;
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