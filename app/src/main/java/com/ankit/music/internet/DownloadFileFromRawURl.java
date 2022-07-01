package com.ankit.music.internet;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ankit.music.Globals;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadFileFromRawURl  extends AsyncTask<String, String, String> {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected String doInBackground(String... arg) {
        URL url;
        String filename = arg[1];
        int count = 0;
        int counter = 0;
        try {
            url = new URL(arg[0]);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        try {
            URLConnection conection = url.openConnection();
            conection.setRequestProperty("Accept-Encoding", "identity");

            InputStream input = new BufferedInputStream(url.openStream(),
                    8192);

            OutputStream output = new FileOutputStream(Globals.appBasePath + filename);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                counter++;
                int ret = Math.floorMod(counter, 3);
                ret++;
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
            return "Success";
        } catch (Exception ignored) {
        }

        return null;
    }
}
