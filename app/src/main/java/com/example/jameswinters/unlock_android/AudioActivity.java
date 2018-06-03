package com.example.jameswinters.unlock_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;



public class AudioActivity extends AppCompatActivity {
    private POI poi;
    private sPOI spoi;
    private hPOI hpoi;
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
    private boolean is_sPOI = false;
    private boolean is_hPOI = false;
    private boolean is_POI = false;
    String audioLinkTest;
    ImageView iv;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_audio);
        iv = findViewById(R.id.audioMainImage);
        Intent i = getIntent();
        Bundle b = i.getExtras();
       
       if(b!=null){

           poi = (POI)b.getSerializable("POI");
           spoi = (sPOI)b.getSerializable("sPOI");
           hpoi = (hPOI)b.getSerializable("hPOI");
           if(!(poi == null)){
               is_POI = true;
               is_sPOI = false;
               is_hPOI = false;
           }
           else if(!(spoi == null)){
               is_sPOI = true;
               is_POI = false;
               is_hPOI = false;
           }
           else if (!(hpoi == null)){
               is_hPOI = true;
               is_POI = false;
               is_sPOI = false;
           }
          
       }
        final MediaPlayer mediaPlayer = new MediaPlayer();
        MainActivity.muteAudio(this,mediaPlayer);

        if(is_POI){
            audioLinkTest = poi.getAudioLink();
            String imageString = poi.getMainImageLink();
            Picasso.get().load(imageString).into(iv);
        }
        else if(is_sPOI){
            audioLinkTest = spoi.getAudioLink();
            String imageString = spoi.getMainImageLink();
            Picasso.get().load(imageString).into(iv);
        }
        else if(is_hPOI){
            audioLinkTest = hpoi.getAudioLink();
            String imageString = hpoi.getMainImageLink();
            Picasso.get().load(imageString).into(iv);
        }
        uri = Uri.parse(audioLinkTest);
        ImageButton audioPlayButton = findViewById(R.id.audioactivity_play);
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

        ImageButton audioStopButton = findViewById(R.id.audioactivity_stop);
        audioStopButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mediaPlayer.reset();
                isPlaying = false;
                isPaused = false;
            }
        });

        ImageButton audioPauseButton = findViewById(R.id.audioactivity_pause);
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