package com.example.jameswinters.unlock_android;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;


// AudioActivity contains audio controls of an audio track to be
// played depending on the sPOI/hPOI/bPOI/POI.

public class AudioActivity extends AppCompatActivity {
    // POI object declaration. During any PresentationActivity,
    // the correct type of POI object (bPOI/hPOI/sPOI/POI) is passed
    // to this activity through a bundle.

    private POI poi;
    private sPOI spoi;
    private hPOI hpoi;
    private bPOI bpoi;

    // Booleans
    public boolean isPlaying;
    public boolean isPaused;
    private boolean is_sPOI = false;
    private boolean is_hPOI = false;
    private boolean is_POI = false;
    private boolean is_bPOI = false;

    public int length; // Used for pausing
    String audioLinkTest; // Firebase url of audiotrack
    String imageString; // Firebase url of background image
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

           // The following code determines type of POI (POI/sPOI/hPOI/bPOI)
           // and sets the relevant boolean values.

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

        // Get main image of bPOI/hPOI/sPOI/POI to display in background
        // Get correct audio track
        if(is_POI){
            // Get URL link (Firebase) of correct audio track
            audioLinkTest = poi.getAudioLink();
            // Get URL link (Firebase) of correct background image
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

        // Put correct picture in background
        Picasso.get()
                .load(imageString)
                .fit()
                .into(iv);
        uri = Uri.parse(audioLinkTest);

        // Create audio play button and set onclicklistener to play audio track
        ImageButton audioPlayButton = findViewById(R.id.audioactivity_play);
        audioPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // When Play is pressed
            public void onClick(View v) {
                try {
                    // If audio is not playing and is not paused, play audio track
                    if (!isPlaying && !isPaused) {
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.setDataSource(getApplicationContext(), uri);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        isPlaying = true; // Set booleans to keep track of state of mediaPlayer
                        isPaused = false;
                    }
                    // If audio is paused, go to the time where audio was paused and play from there
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

        // Create stop button
        ImageButton audioStopButton = findViewById(R.id.audioactivity_stop);
        audioStopButton.setOnClickListener(new View.OnClickListener(){
            @Override
            // If stop is pressed
            public void onClick(View v){
                mediaPlayer.reset();
                isPlaying = false;
                isPaused = false;
            }
        });

        // Create pause button
        ImageButton audioPauseButton = findViewById(R.id.audioactivity_pause);
        audioPauseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            // If pause is pressed
            public void onClick(View v){
                mediaPlayer.pause();
                length = mediaPlayer.getCurrentPosition();
                isPaused = true;
            }
        });
    }

    @Override
    // If back is pressed in AudioActivity, go to the correct PresentationActivity
    public void onBackPressed() {
        super.onBackPressed();
        // If the AudioActivity was for a POI
        if (is_POI) {
            Intent i = new Intent(AudioActivity.this, POIPresentationActivity.class);
            Bundle b = new Bundle();

            // Send the same POI back to POIPresentationActivity so that activity knows what
            // content to display
            b.putSerializable("POI", poi);
            i.putExtras(b);
            startActivity(i);
        }

        // If the AudioActivity was for an sPOI
        if (is_sPOI) {
            Intent i = new Intent(AudioActivity.this, sPOIPresentationActivity.class);
            Bundle b = new Bundle();

            // Send the same sPOI back to sPOIPresentationActivity so that activity knows what
            // content to display
            b.putSerializable("sPOI", spoi);
            i.putExtras(b);
            startActivity(i);
        }

        // If the AudioActivity was for a hPOI
        if (is_hPOI) {
            Intent i = new Intent(AudioActivity.this, hPOIPresentationActivity.class);
            Bundle b = new Bundle();

            // Send the same hPOI back to hPOIPresentationActivity so that activity knows what
            // content to display
            b.putSerializable("hPOI", hpoi);
            i.putExtras(b);
            startActivity(i);
        }
        // If the AudioActivity was for a bPOI
        if (is_bPOI) {
            Intent i = new Intent(AudioActivity.this, bPOIPresentationActivity.class);
            Bundle b = new Bundle();

            // Send the same bPOI back to bPOIPresentationActivity so that activity knows what
            // content to display
            b.putSerializable("bPOI", bpoi);
            i.putExtras(b);
            startActivity(i);
        }
    }
}