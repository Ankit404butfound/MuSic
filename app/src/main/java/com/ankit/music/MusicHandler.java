package com.ankit.music;


import static com.ankit.music.Globals.player;
import static com.ankit.music.Globals.recyclerView;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class MusicHandler {
    public void playSong(String path){
        try {
            player.stop();
            player.release();
            player = null;
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
                    Globals.tv_total_time.setText(String.format("%02d:%02d", (songDuration/60000)%60, (songDuration/1000)%60));
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            if (Globals.loop_type == "loop_song"){
                                Globals.seekBar.setProgress(0);
                                Globals.tv_time_passed.setText("00:00");
                                playSong(path);
                            }
                            else {
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

    public void updateColorsOfLastTv(){
        if (Globals.playedSongDataArray.size() > 0) {
//            RelativeLayout last_rv = (RelativeLayout) Globals.playedSongDataArray.get(Globals.playedSongDataArray.size() - 1).get(0);
//            TextView txt_v = last_rv.findViewById(R.id.tv_song_info);
//            txt_v.setTextColor(Color.parseColor("#BCBCBC"));
        }
    }

    public void playNextSong() {
        Log.d("taggg", Globals.songDataArray.toString());
        if (Globals.playedSongDataArray.size() != 0) {
            updateColorsOfLastTv();
        }
        if (Globals.loop_type.matches("shuffle")) {
            int length = Globals.songDataArray.size();
            Log.d("Sizee", String.valueOf(Globals.songDataArrayClone.size()));
            if (length < 1) {
                if (Globals.songDataArrayClone.size() > 0) {
                    Log.d("Inside", String.valueOf(length));
                    Globals.songDataArray.addAll(Globals.songDataArrayClone);
                    Globals.playedSongDataArray.clear();
                    playNextSong();
                }
                else {
                    return;
                }
            }
            else {
                Random random = new Random();
                int num = random.nextInt(length);
                Log.d("num", String.valueOf(num));

//                ArrayList nextSong = Globals.songDataArray.get(num);
//                Log.d("tag"+num, Globals.songDataArray.toString());
//                Globals.songDataArray.remove(num);
//                Log.d("tag", Globals.songDataArray.toString());
//                Globals.playedSongDataArray.add(nextSong);
//                Log.d("tag", Globals.playedSongDataArray.toString());
//                RelativeLayout nextrv = (RelativeLayout) nextSong.get(0);
//                TextView txt_v = nextrv.findViewById(R.id.tv_song_info);
//                txt_v.setTextColor(Color.parseColor("#FF0062"));
//                playSong((String) nextSong.get(1));

            }

        }
    }

}
