package com.ankit.music;

import android.text.SpannableString;
import android.widget.TextView;

public class SongData {
    public TextView textView;
    public String song;
    public String artistAlbum;
    public int songId;
    public String filename;
    public SongData(TextView textView, String song, String artistAlbum, int songId, String filename) {
        this.textView = textView;
        this.song = song;
        this.artistAlbum = artistAlbum;
        this.songId = songId;
        this.filename = filename;
    }
}
