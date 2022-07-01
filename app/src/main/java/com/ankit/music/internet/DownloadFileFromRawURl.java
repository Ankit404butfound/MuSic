package com.ankit.music.internet;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.ankit.music.Globals;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class DownloadFileFromRawURl  extends AsyncTask<String, String, String> {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected String doInBackground(String... arg) {
        URL url;
        String filename = arg[1];
        int count = 0;
        int counter = 0;
        Log.d("URL", arg[0] + arg[1]);
        try {
            url = new URL(arg[0]);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        try {
            URLConnection conection = url.openConnection();
            conection.setRequestProperty("Accept-Encoding", "identity");

            String lenghtOfFile = conection.getHeaderField("Content-Length");
            Log.e("length", conection.getHeaderFields().toString());

            // download the file
            InputStream input = new BufferedInputStream(url.openStream(),
                    8192);

            // Output stream
            OutputStream output = new FileOutputStream(Globals.appBasePath + filename);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                counter++;
                // publishing the progress....
                // After this onProgressUpdate will be called
                int ret = Math.floorMod(counter, 3);
                ret++;
                Log.d("ret", String.valueOf(ret));


                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();
            return "Success";
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return null;
    }

    /**
     * Updating progress bar
     **/
//    protected void onProgressUpdate(String... progress) {
//        // setting progress percentage
//        pDialog.setProgress(Integer.parseInt(progress[0]));
//    }
}
