package com.ankit.music;

import android.app.Notification;
import android.media.MediaPlayer;
import android.os.Environment;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Globals {
    public static ArrayList<SongLayoutInfo> songDataArray = new ArrayList<>();
    public static ArrayList<SongLayoutInfo> songDataArrayClone = new ArrayList<>();
    public static ArrayList<SongLayoutInfo> playedSongDataArray = new ArrayList<>();
    public static TextView songTitle;
    public static DatabaseHandler databaseHandler;
    public static MusicHandler musicHandler;
    public static MediaPlayer player;
    public static Boolean pause = false;
    public static Boolean pluscClicked = false;
    public static TextView tv_time_passed;
    public static TextView tv_total_time;
    public static SeekBar seekBar;
    public static ImageButton btn_play_pause;
    public static TextView last_song_tv;
    public static TextView previous_artist_tv;
    public static ListView recyclerView;
    public static String loop_type = "shuffle";
    public static int selectedIndex;
    public static Notification.Builder builder;
    public static RemoteViews contentView;
    public static String appBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MuSicData/";
}
