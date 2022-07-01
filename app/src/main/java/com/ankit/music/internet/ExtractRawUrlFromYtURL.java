package com.ankit.music.internet;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ExtractRawUrlFromYtURL  extends AsyncTask<String, String, String> {
    String BASE_URL = "https://api.vevioz.com/convert?url=";

    @Override
    protected String doInBackground(String... args) {
        String line;
        String rawUrl = null;
        String song = null;
        String imgPath = null;
        String ytUrl = args[0];
        URL url;
        try {
            url = new URL(BASE_URL+ytUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() != 200) {
                return null;
            }
            else {
                InputStream inputStream = conn.getInputStream();
                if (inputStream == null) {
                    return null;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                while ((line = reader.readLine()) != null){
                    if (line.contains("/download/")) {
                        rawUrl = line.split(" ")[1].replace("href=\"", "").replace("\"", "");
                        rawUrl = rawUrl.replace("\"", "");
                        Log.d("URL", rawUrl);
                    }

                    if (line.contains("text-lg text-teal-600 font-bold m-2 text-center")){
                        song = line.replace("<h2 class=\"text-lg text-teal-600 font-bold m-2 text-center\">", "");
                        song = song.replace("</h2>", "");
                    }

                    if (line.contains("img") && line.contains("src")){
                        imgPath = line.split("src=\"")[1].replace("\">", "");
                    }
                }

                return rawUrl + "\n" + song + "\n" + imgPath;
            }

        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
