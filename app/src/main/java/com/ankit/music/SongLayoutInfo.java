package com.ankit.music;

import android.text.SpannableString;
import android.widget.TextView;

public class SongLayoutInfo{
    private TextView textView;
    private String song;
    private String artistAlbum;
    private int songId;
    private int imgId;
    private String filename;
    public SongLayoutInfo(TextView textView, String song, String artistAlbum, int songId, String filename) {
        this.textView = textView;
        this.song = song;
        this.artistAlbum = artistAlbum;
        this.songId = songId;
        this.filename = filename;
    }
}
