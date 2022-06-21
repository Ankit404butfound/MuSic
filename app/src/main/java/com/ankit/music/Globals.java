package com.ankit.music;

import android.app.Notification;
import android.media.MediaPlayer;
import android.os.Environment;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class Globals {
    public static final int ID_SERVICE = 101;
    public static final String FORWARD = "com.ankit.music.action.FORWARD";
    public static final String BACK = "com.ankit.music.action.BACK";
    public static final String PLAY = "com.ankit.music.action.PLAY";
    public static final String PAUSE = "com.ankit.music.action.PAUSE";

    public static NotificationManager notificationManager;
    public static ArrayList<SongData> songDataArray = new ArrayList<>();
    public static ArrayList<SongData> songDataArrayClone = new ArrayList<>();
    public static ArrayList<SongData> playedSongDataArray = new ArrayList<>();
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
    public static String loop_type = "shuffle";
    public static int selectedIndex;
    public static Notification.Builder builder;
    public static RemoteViews contentView;
    public static String appBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MuSicData/";
}
