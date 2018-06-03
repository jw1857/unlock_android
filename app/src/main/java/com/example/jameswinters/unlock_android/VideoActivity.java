package com.example.jameswinters.unlock_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class VideoActivity extends AppCompatActivity {

    private POI poi;
    private sPOI spoi;
    private hPOI hpoi;
    private bPOI bpoi;
    
    private boolean is_sPOI = false;
    private boolean is_hPOI = false;
    private boolean is_POI = false;
    private boolean is_bPOI = false;
    String str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_video);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        if(b!=null){
            bpoi = (bPOI)b.get("bPOI");
            poi = (POI)b.getSerializable("POI");
            spoi = (sPOI)b.getSerializable("sPOI");
            hpoi = (hPOI)b.getSerializable("hPOI");
            if (!(poi == null)) {
                is_POI = true;
                is_sPOI = false;
                is_hPOI = false;
                is_bPOI = false;
            } else if (!(spoi == null)) {
                is_sPOI = true;
                is_POI = false;
                is_hPOI = false;
                is_bPOI = false;
            } else if (!(hpoi == null)) {
                is_hPOI = true;
                is_POI = false;
                is_sPOI = false;
                is_bPOI = false;
            } else if (bpoi != null) {
                is_hPOI = false;
                is_POI = false;
                is_sPOI = false;
                is_bPOI = true;
            }
     
        }


        if(is_POI){
             str = poi.getVideoLink();
        }
        else if(is_sPOI){
            str = spoi.getVideoLink();
        }
        else if(is_hPOI){
             str = hpoi.getVideoLink();
        }
        else if(is_bPOI){
            str = bpoi.getVideoLink();
        }


        Uri uri = Uri.parse(str);

        VideoView video = findViewById(R.id.video_view);
        video.setMediaController(new MediaController(this));
        video.setVideoURI(uri);
        video.requestFocus();

        MediaPlayer.OnPreparedListener PreparedListener = new MediaPlayer.OnPreparedListener(){

            @Override
            public void onPrepared(MediaPlayer m) {
                try {
                    MainActivity.muteAudio(VideoActivity.this,m);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        video.setOnPreparedListener(PreparedListener);
        autoPlay(video);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (is_POI) {
            Intent i = new Intent(VideoActivity.this, POIPresentationActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("POI", poi);
            i.putExtras(b);
            startActivity(i);
        }
        if (is_sPOI) {
            Intent i = new Intent(VideoActivity.this, sPOIPresentationActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("sPOI", spoi);
            i.putExtras(b);
            startActivity(i);
        }
        if (is_hPOI) {
            Intent i = new Intent(VideoActivity.this, hPOIPresentationActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("hPOI", hpoi);
            i.putExtras(b);
            startActivity(i);
        }
        if (is_bPOI) {
            Intent i = new Intent(VideoActivity.this, bPOIPresentationActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("bPOI", bpoi);
            i.putExtras(b);
            startActivity(i);
        }
    }

    public void autoPlay(VideoView vid) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean("autoplay", true)) {
            vid.start();
        }
        if (!sp.getBoolean("autoplay", true)) {
            vid.pause();
        }
    }
}
