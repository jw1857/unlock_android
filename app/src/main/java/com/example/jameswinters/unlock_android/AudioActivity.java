package com.example.jameswinters.unlock_android;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;

import java.io.IOException;
import java.util.ArrayList;



public class AudioActivity extends AppCompatActivity {
    POI poi;
    private ArrayList<POI> POIList;
    private ArrayList<sPOI> sPOIList;
    private ArrayList<hPOI> hPOIList;
    private ArrayList<String> audioLinkArray;
    //private MediaController mediaController;
    //private MediaPlayer mediaPlayer = new MediaPlayer();
    private Handler handler = new Handler();
    public boolean isPlaying;
    public boolean isPaused;
    public int length;
    String audioLinkTest;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        if(b!=null){
            poi = (POI)b.getSerializable("POI");
        }
        final MediaPlayer mediaPlayer = new MediaPlayer();
        /*audioLinkArray = poi.getAudioLinks();
        numAudios = audioLinkArray.size();
        String imagelink = imageArray.get(0);
        Uri uri = Uri.parse(imagelink); */
        audioLinkTest = poi.getAudioLink();
        uri = Uri.parse(audioLinkTest);
        Button audioPlayButton = findViewById(R.id.playbutton);
        audioPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!isPlaying && !isPaused) {
                        //MediaPlayer mediaPlayer = new MediaPlayer();
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.setDataSource(getApplicationContext(), uri);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        isPlaying = true;
                        isPaused = false;
                    }

                    else if (isPaused){
                        mediaPlayer.seekTo(length);
                        mediaPlayer.start();
                        isPlaying = true;
                        isPaused = false;
                    }
                    } catch(Exception e){

                    }

            }
        });

        Button audioStopButton = findViewById(R.id.stopbutton);
        audioStopButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mediaPlayer.reset();
                isPlaying = false;
                isPaused = false;
            }
        });

        Button audioPauseButton = findViewById(R.id.pausebutton);
        audioPauseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mediaPlayer.pause();
                length = mediaPlayer.getCurrentPosition();
                isPaused = true;

            }
        });
    }
}