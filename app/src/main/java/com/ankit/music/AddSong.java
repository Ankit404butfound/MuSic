package com.ankit.music;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ankit.music.internet.DownloadFileFromRawURl;
import com.ankit.music.internet.ExtractRawUrlFromYtURL;
import com.ankit.music.internet.ExtractYtUrlFromLyrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddSong extends AppCompatActivity {
    private TextView status;
    private String userInput;
    private String song = null;
    private String imgPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song);

        ImageButton btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> finish());

        status = findViewById(R.id.status);

        TextView edt_search = findViewById(R.id.edt_search);

        Button btn_search = findViewById(R.id.btn_search);
        btn_search.setOnClickListener(v -> {
            status.setVisibility(View.VISIBLE);
            status.setText("Searching...");
            userInput = edt_search.getText().toString();
            Pattern pattern = Pattern.compile(
                    "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                    Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(userInput);
            if (matcher.matches()){

                YtUrlToRaw(userInput, matcher.group(0)+".mp3");
            }

            else if(userInput.matches("http(s)?://(www\\.)?.*")){
                ArrayList<String> data = new ArrayList<>(Arrays.asList(userInput.split("/")));
                RawToFile(userInput, data.get(data.size()-1));
            }
            else {
                lyricsToYtUrl(userInput);
                song = userInput;
            }
        });
    }

    public void lyricsToYtUrl(String lyrics){
        new ExtractYtUrlFromLyrics(){
            String vid = null;
            @Override
            public void onPostExecute(String result) {

                if (result == null){
                    status.setText("Something went wrong1!!!");
                    return;
                }

                status.setText("Extracting... "+result);
                YtUrlToRaw(result, lyrics+".mp3");


            }
        }.execute(lyrics);
    }

    public void YtUrlToRaw(String ytUrl, String filename){
        new ExtractRawUrlFromYtURL(){
            @Override
            public void onPostExecute(String result) {

                if (result == null){
                    status.setText("Something went wrong2!!!");
                    return;
                }

                status.setText("Converting to mp3... ");
                if (song == null)
                    song = result.split("\n")[1];
                imgPath = result.split("\n")[2];
                RawToFile(result.split("\n")[0], filename);

            }
        }.execute(ytUrl);
    }

    public void RawToFile(String url, final String filename){
        status.setText("Downloading");
        new DownloadFileFromRawURl(){
            @Override
            public void onProgressUpdate(String... progress){
            }

            @Override
            public void onPostExecute(String result) {

                if (result == null) {
                    status.setText("Something went wrong3!!!");
                    return;
                }

                status.setText("Download completed!!!");
                Intent resultIntent = new Intent(); //String filename, String song, String imgPath
                resultIntent.putExtra("filename", filename);
                resultIntent.putExtra("song", song);
                resultIntent.putExtra("imgPath", imgPath);
                setResult(2223, resultIntent);
                finish();

            }
        }.execute(url, filename);
    }

}