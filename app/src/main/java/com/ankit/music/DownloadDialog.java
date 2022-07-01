package com.ankit.music;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class DownloadDialog extends Dialog {

    public Dialog d;
    public Button ok;
    public TextView txtVSongData;

    public DownloadDialog(Activity a) {
        super(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.download_dialog);

        ok = findViewById(R.id.btn_yes_and_OK);

    }


}
