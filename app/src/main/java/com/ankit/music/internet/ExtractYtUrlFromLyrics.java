package com.ankit.music.internet;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ExtractYtUrlFromLyrics extends AsyncTask<String, String, String> {
    String BASE_URL = "https://pywhatkit.herokuapp.com/playonyt?topic=";

    @Override
    protected String doInBackground(String... arg) {
        URL url;
        int count = 0;
        try {
            url = new URL(BASE_URL+arg[0]);
            Log.d("URL", url.toString());
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
                ArrayList<String> lst = new ArrayList<String>();
                return reader.readLine();
            }

        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
