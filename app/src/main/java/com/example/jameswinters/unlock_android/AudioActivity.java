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
    private bPOI bpoi;
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
    private boolean is_bPOI = false;
    String audioLinkTest;
    String imageString;
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
           bpoi = (bPOI)b.getSerializable("bPOI");
           if(!(poi == null)){
               is_POI = true;
               is_sPOI = false;
               is_hPOI = false;
               is_bPOI=false;
           }
           else if(!(spoi == null)){
               is_sPOI = true;
               is_POI = false;
               is_hPOI = false;
               is_bPOI=false;
           }
           else if (!(hpoi == null)){
               is_hPOI = true;
               is_POI = false;
               is_sPOI = false;
               is_bPOI=false;
           }
           else if (bpoi!=null){
               is_hPOI = false;
               is_POI = false;
               is_sPOI = false;
               is_bPOI=true;
           }
          
       }
        final MediaPlayer mediaPlayer = new MediaPlayer();
        MainActivity.muteAudio(this,mediaPlayer);

        if(is_POI){
            audioLinkTest = poi.getAudioLink();
            imageString = poi.getMainImageLink();

        }
        else if(is_sPOI){
            audioLinkTest = spoi.getAudioLink();
            imageString = spoi.getMainImageLink();

        }
        else if(is_hPOI){
            audioLinkTest = hpoi.getAudioLink();
            imageString = hpoi.getMainImageLink();

        }
        else if (is_bPOI){
            audioLinkTest =bpoi.getAudioLink();
        }
        Picasso.get()
                .load(imageString)
                .fit()
                .into(iv);
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (is_POI) {
            Intent i = new Intent(AudioActivity.this, POIPresentationActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("POI", poi);
            i.putExtras(b);
            startActivity(i);
        }
        if (is_sPOI) {
            Intent i = new Intent(AudioActivity.this, sPOIPresentationActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("sPOI", spoi);
            i.putExtras(b);
            startActivity(i);
        }
        if (is_hPOI) {
            Intent i = new Intent(AudioActivity.this, hPOIPresentationActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("hPOI", hpoi);
            i.putExtras(b);
            startActivity(i);
        }
        if (is_bPOI) {
            Intent i = new Intent(AudioActivity.this, bPOIPresentationActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("bPOI", bpoi);
            i.putExtras(b);
            startActivity(i);
        }
    }
}