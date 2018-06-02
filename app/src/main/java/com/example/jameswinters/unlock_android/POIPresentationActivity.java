package com.example.jameswinters.unlock_android;

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
    private boolean is_sPOI = false;
    private boolean is_hPOI = false;
    private boolean is_POI = false;
    private int MY_DATA_CHECK_CODE = 0;

    TextToSpeech tts;
    String str;
    String text;
    ImageView iv;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_poipresentation);

        iv = findViewById(R.id.poiMainImage);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        if (b != null) {
            poi = (POI) b.getSerializable("POI");

            String imageString = poi.getMainImageLink();
            Picasso.get().load(imageString).into(iv);

            spoi = (sPOI) b.getSerializable("sPOI");
            hpoi = (hPOI) b.getSerializable("hPOI");
            if (!(poi == null)) {
                is_POI = true;
                is_sPOI = false;
                is_hPOI = false;
            } else if (!(spoi == null)) {
                is_sPOI = true;
                is_POI = false;
                is_hPOI = false;
            } else if (!(hpoi == null)) {
                is_hPOI = true;
                is_POI = false;
                is_sPOI = false;
            }

        }



        Button videoButton = findViewById(R.id.videobutton_poi);
        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(POIPresentationActivity.this, VideoActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("POI", poi);
                i.putExtras(b);
                startActivity(i);
            }
        });
        Button imageButton = findViewById(R.id.imagebutton_poi);
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
        Button audioButton = findViewById(R.id.audiobutton_poi);
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

        if(is_POI){
            str = poi.getText();
        }
        else if(is_sPOI){
            str = spoi.getText();
        }
        else if(is_hPOI){
            str = hpoi.getText();
        }
        //
        //str = poi.getText();

        StorageReference txtRef = storage.getReferenceFromUrl(str);
        final TextView textView = findViewById(R.id.TEXT_STATUS_ID);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
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
                textView.setTextSize(20.0f);
                break;
        }
        final long ONE_MEGABYTE = 1024 * 1024; // or to the maximum size of your text, but careful it crashes if it's too big
        txtRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                text = new String(bytes);
                textView.setText(text);
                textView.setTextColor(WHITE);
                //textView.setTextSize();
                Toast.makeText(POIPresentationActivity.this, "" + textView.getTextSize(), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        Button textToSpeechButton = findViewById(R.id.button_texttospeech);
        textToSpeechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConvertTextToSpeech();
            }
        });
        Button stopTextToSpeechButton = findViewById(R.id.button_stoptexttospeech);
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
            tts.speak(text+"is saved", TextToSpeech.QUEUE_FLUSH, null);
    }

}




