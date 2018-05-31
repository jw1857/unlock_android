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
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Handler handler = new Handler();
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
            POIList = (ArrayList<POI>) b.getSerializable("POIList");
            sPOIList = (ArrayList<sPOI>) b.getSerializable("sPOIList");
            hPOIList=(ArrayList<hPOI>) b.getSerializable("hPOIList");
        }

        /*audioLinkArray = poi.getAudioLinks();
        numAudios = audioLinkArray.size();
        String imagelink = imageArray.get(0);
        Uri uri = Uri.parse(imagelink); */
        audioLinkTest = poi.getAudioLink();
        uri = Uri.parse(audioLinkTest);
        Button audioButton = findViewById(R.id.playbutton);
        audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //MediaPlayer mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource(getApplicationContext(), uri);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch(Exception e){

                }
            }
        });
        /*try {
                MediaPlayer mediaPlayer = new MediaPlayer();
                MediaController mediaController = new MediaController(this);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(getApplicationContext(), uri);
                mediaPlayer.prepare();
            } catch(Exception e){

            }*/
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        //mediaController.show();
        return false;
    }

    public void start(){
       // mediaPlayer.start();
    }

    public void pause(){
       // mediaPlayer.pause();
    }
}
