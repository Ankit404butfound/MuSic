package com.ankit.music;


import static com.ankit.music.Globals.playedSongDataArray;
import static com.ankit.music.Globals.playedSongOffset;
import static com.ankit.music.Globals.player;
import static com.ankit.music.Globals.songDataArray;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Random;

public class MusicHandler {

    public void playSong(int songId, String path, String songName, String artistAlbum) {
        Globals.currentSongid = songId;
        try {
            player.stop();
            player.release();
            player = null;
            Globals.songTitle.setText(songName);
            Globals.seekBar.setProgress(0);
            Globals.tv_total_time.setText("00:00");
            Globals.pause = false;
            player = new MediaPlayer();
            player.setDataSource(path);

            Globals.btn_play_pause.setImageResource(R.drawable.ic_pause);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                int songDuration = mediaPlayer.getDuration();
                Globals.seekBar.setMax(songDuration);
                Globals.tv_total_time.setText(String.format("%02d:%02d", (songDuration / 60000) % 60, (songDuration / 1000) % 60));
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        if (Globals.loop_type == "loop_song") {
                            Globals.seekBar.setProgress(0);
                            Globals.tv_time_passed.setText("00:00");
                            playSong(songId, path, songName, artistAlbum);
                        } else {
                            playNextSong();


//                                int songCurrentTime = player.getCurrentPosition();
//                                Globals.seekBar.setProgress(songCurrentTime);
//                                songCurrentTime = songCurrentTime / 1000;
//                                Globals.tv_time_passed.setText(String.format("%02d:%02d", songCurrentTime / 60, songCurrentTime % 60));
//                                Globals.pause = true;
//                                Globals.btn_play_pause.setImageResource(R.drawable.ic_play);
                        }
                    }
                });
            }
        });
        try {
            player.prepare();
        } catch (IOException ioException) {
            ioException.printStackTrace();

            player.start();
        }
    }


    public void playSong(SongData songData){
        updateColorsOfLastTv();
        if (Globals.songDataArray.contains(songData)){
            Globals.songDataArray.remove(songData);
        }
        else{
            Globals.playedSongDataArray.remove(songData);
        }
        Globals.playedSongDataArray.add(songData);
//        NotificationManager.updateNotification(songData.song, songData.artistAlbum);
        songData.textView.setTextColor(Color.parseColor("#FF0051"));
        playSong(songData.songId, songData.filename, songData.song, songData.artistAlbum);
    }

    public void updateColorsOfLastTv(){
        if (Globals.playedSongDataArray.size() > 0) {
            TextView txt_v = Globals.playedSongDataArray.get(Globals.playedSongDataArray.size() - Globals.playedSongOffset).textView;
            txt_v.setTextColor(Color.parseColor("#BCBCBC"));
        }
    }

    public void playNextSong() {

        int index = 0;
        Log.d("taggg", Globals.songDataArray.toString());
        updateColorsOfLastTv();
        Globals.playedSongOffset = 1;
        int length = Globals.songDataArray.size();
        Log.d("Sizee", String.valueOf(Globals.songDataArrayClone.size()));
        if (length < 1) {
            if (Globals.songDataArrayClone.size() > 0) {
                Log.d("Inside", String.valueOf(length));
                SongData lastSongData = Globals.playedSongDataArray.get(Globals.playedSongDataArray.size() - Globals.playedSongOffset);
                Globals.songDataArray.addAll(Globals.songDataArrayClone);
                Globals.playedSongDataArray.clear();
                playedSongDataArray.add(lastSongData);
                songDataArray.remove(lastSongData);
                playNextSong();
            }
            else {
                return;
            }
        }

        else {
            Log.d("Loop_type", Globals.loop_type);
            if (Globals.loop_type.matches("shuffle")) {
                Random random = new Random();
                index = random.nextInt(length);
                Log.d("num", String.valueOf(index));
            } else {
                index = 0;
            }

            SongData nextSongData = Globals.songDataArray.get(index);
            nextSongData.textView.setTextColor(Color.parseColor("#FF0051"));
            Log.d("Data", nextSongData.textView.getText().toString());
            Globals.playedSongDataArray.add(nextSongData);
            Globals.songDataArray.remove(nextSongData);
            try {
                NotificationManager.updateNotification(nextSongData.song, nextSongData.artistAlbum);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Globals.onStartSongData = nextSongData;
            playSong(nextSongData.songId, nextSongData.filename, nextSongData.song, nextSongData.artistAlbum);


        }
    }

    public void playPreviousSong(){

        int size  = playedSongDataArray.size();
        if (Globals.playedSongOffset < size) {
            updateColorsOfLastTv();
            Globals.playedSongOffset++;
            SongData songData = Globals.playedSongDataArray.get(size-Globals.playedSongOffset);
            songData.textView.setTextColor(Color.parseColor("#FF0051"));
            try {
                NotificationManager.updateNotification(songData.song, songData.artistAlbum);
            } catch (Exception e) {
                e.printStackTrace();
            }
            playSong(songData.songId, songData.filename, songData.song, songData.artistAlbum);
        }
        else{
            Globals.player.seekTo(0);
        }
    }

}
