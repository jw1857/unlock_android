package com.example.jameswinters.unlock_android;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.ArrayList;

public class VideoActivity extends AppCompatActivity {
    POI poi;
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
            poi = (POI)b.getSerializable("POI");
        }

        String str = poi.getVideoLink();
        Uri uri = Uri.parse(str);
        VideoView video = findViewById(R.id.video_view);
        video.setMediaController(new MediaController(this));
        video.setVideoURI(uri);
        video.requestFocus();
        video.start();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(VideoActivity.this, POIPresentationActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("POI", poi);
        i.putExtras(b);
        startActivity(i);
    }
}
