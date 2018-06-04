package com.example.jameswinters.unlock_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import static android.graphics.Color.WHITE;

public class sPOIPresentationActivity extends AppCompatActivity {
    sPOI spoi;
    ImageView iv;
    TextToSpeech tts;
    String str;
    String text;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getSupportActionBar().hide();
        setContentView(R.layout.activity_spoipresentation);

        iv = findViewById(R.id.spoiMainImage); // need to change
        Intent i = getIntent();
        Bundle b = i.getExtras();
        if (b != null) {
            spoi = (sPOI) b.getSerializable("sPOI");
            this.setTitle(spoi.getTitle());
            String imageString = spoi.getMainImageLink();
            Picasso.get()
                    .load(imageString)
                    .fit()
                    .into(iv);
        }

       ImageButton videoButton = findViewById(R.id.spoipresentation_videoimagebutton);
        if (spoi.getVideoLink()==null){
            videoButton.setVisibility(View.INVISIBLE);
        }
        else if (spoi.getVideoLink()!=null) {
            videoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(sPOIPresentationActivity.this, VideoActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("sPOI", spoi);
                    i.putExtras(b);
                    startActivity(i);
                }
            });
        }
        ImageButton imageButton = findViewById(R.id.spoipresentation_photobutton);
        if (spoi.getImageLinks()==null){
            imageButton.setVisibility(View.INVISIBLE);
        }
        else if (spoi.getImageLinks()!=null) {
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(sPOIPresentationActivity.this, ImageActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("sPOI", spoi);
                    i.putExtras(b);
                    startActivity(i);
                }
            });
        }
        ImageButton audioButton = findViewById(R.id.spoipresentation_audio);
        if (spoi.getAudioLink()==null){
            audioButton.setVisibility(View.INVISIBLE);
        }
        else if (spoi.getAudioLink()!=null) {
            audioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(sPOIPresentationActivity.this, AudioActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("sPOI", spoi);
                    i.putExtras(b);
                    startActivity(i);
                }
            });
        }
        str = spoi.getText();

        //
        //str = poi.getText();

        final TextView textView = findViewById(R.id.spoi_TEXT_STATUS_ID);
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
        if ((str.length()<30)||(str.equals(null))){
            textView.setText(str);
            text=str;

        }
        else if ((str.length()>30)){
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


        ImageButton textToSpeechButton = findViewById(R.id.spoipresentation_tts);
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

        ImageButton stopTextToSpeechButton = findViewById(R.id.spoipresentation_notts);
        if(!sp.getBoolean("texttospeech",true)){
            stopTextToSpeechButton.setVisibility(View.INVISIBLE);
        }

        stopTextToSpeechButton.setOnClickListener(new View.OnClickListener() {

        
            @Override
            public void onClick(View v) {
                tts.stop();
            }
        });


        tts=new TextToSpeech(sPOIPresentationActivity.this, new TextToSpeech.OnInitListener() {


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




