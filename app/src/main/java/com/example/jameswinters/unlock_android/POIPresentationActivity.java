package com.example.jameswinters.unlock_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class POIPresentationActivity extends AppCompatActivity {
    POI poi;
    sPOI spoi;
    hPOI hpoi;

    TextToSpeech tts;
    String str;
    String text;
    ImageView iv;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getSupportActionBar().hide();
        setContentView(R.layout.activity_poipresentation);

        iv = findViewById(R.id.poiMainImage);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        if (b != null) {
            poi = (POI) b.getSerializable("POI");
            this.setTitle(poi.getTitle());

            String imageString = poi.getMainImageLink();
            Picasso.get().load(imageString).into(iv);

        }



        //Button videoButton = findViewById(R.id.videobutton_poi);
        ImageButton videoImageButton = (ImageButton)findViewById(R.id.poipresentation_videoimagebutton);
       
        if (poi.getVideoLink()==null){
            videoImageButton.setVisibility(View.INVISIBLE);
        }
        else if (poi.getVideoLink()!=null) {
            videoImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(POIPresentationActivity.this, VideoActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("POI", poi);
                i.putExtras(b);
                startActivity(i);
            }
        });
        }
        ImageButton imageButton = findViewById(R.id.poipresentation_photobutton);
        if (poi.getImageLinks()==null){
            imageButton.setVisibility(View.INVISIBLE);
        }
        else if (poi.getImageLinks()!=null) {
            imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(POIPresentationActivity.this, ImageActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("POI", poi);
                i.putExtras(b);
                startActivity(i);
            }
        });
        }
        ImageButton audioButton = findViewById(R.id.poipresentation_audio);
        if (poi.getAudioLink()==null){
            audioButton.setVisibility(View.INVISIBLE);
        }
        else if (poi.getAudioLink()!=null) {
            audioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(POIPresentationActivity.this, AudioActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("POI", poi);
                    i.putExtras(b);
                    startActivity(i);
                }
            });
        }

            str = poi.getText();

        //
        //str = poi.getText();

        final TextView textView = findViewById(R.id.TEXT_STATUS_ID);
       final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        switch(sp.getString("textsize","textsmall")){
            case "textsmall":
                textView.setTextSize(15.0f);
                break;
            case "textmedium":
                textView.setTextSize(20.0f);
                break;
            case "textlarge":
                textView.setTextSize(25.0f);
                break;
            default:
                textView.setTextSize(15.0f);
                break;
        }
        if ((str.length()<20)||(str.equals(null))){
            textView.setText(str);
            text =str;

        }
        else if ((str.length()>20)){
        StorageReference txtRef = storage.getReferenceFromUrl(str);
        final long ONE_MEGABYTE = 1024 * 1024; // or to the maximum size of your text, but careful it crashes if it's too big
        txtRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                text = new String(bytes);
                textView.setText(text);
                textView.setTextColor(WHITE);
                //textView.setTextSize();
                //Toast.makeText(POIPresentationActivity.this, "" + textView.getTextSize(), Toast.LENGTH_SHORT).show();
            }
        }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }


        ImageButton textToSpeechButton = findViewById(R.id.poipresentation_tts);
        if(!sp.getBoolean("texttospeech",true)){
            textToSpeechButton.setVisibility(View.INVISIBLE);
        }

        textToSpeechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sp.getBoolean("audio",true)){
                    tts.shutdown();
                }
                ConvertTextToSpeech();
            }
        });
        ImageButton stopTextToSpeechButton = (ImageButton)findViewById(R.id.poipresentation_notts);
        if(!sp.getBoolean("texttospeech",true)){
            stopTextToSpeechButton.setVisibility(View.INVISIBLE);
        }

        stopTextToSpeechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts.stop();
            }
        });

        tts=new TextToSpeech(POIPresentationActivity.this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    int result=tts.setLanguage(Locale.UK);
                }
                else
                    Log.e("error", "Initilization Failed!");
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
    }

    @Override
    protected void onPause() {


        if(tts != null){

            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }

    public void onClick(View v){

        ConvertTextToSpeech();

    }

    private void ConvertTextToSpeech() {


        if(text==null||"".equals(text))
        {
            text = "Content not available";
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }else
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

}




