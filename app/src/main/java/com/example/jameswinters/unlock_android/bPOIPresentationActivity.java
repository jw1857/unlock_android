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

public class bPOIPresentationActivity extends AppCompatActivity {

    bPOI bpoi;
    TextToSpeech tts;
    String str;
    String text;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_bpoipresentation);

        iv = findViewById(R.id.bpoiMainImage); // need to change
        Intent i = getIntent();
        Bundle b = i.getExtras();
        if (b != null) {
            bpoi = (bPOI) b.getSerializable("bPOI");
            this.setTitle(bpoi.getTitle());
            String imageString = bpoi.getMainImageLink();
           Picasso.get().load(imageString).into(iv);
        }
        Button videoButton = findViewById(R.id.videobutton_bpoi);
        if (bpoi.getVideoLink()==null){
            videoButton.setVisibility(View.INVISIBLE);
        }
        else if (bpoi.getVideoLink()!=null) {
            videoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(bPOIPresentationActivity.this, VideoActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("bPOI", bpoi);
                    i.putExtras(b);
                    startActivity(i);
                }
            });
        }
        Button imageButton = findViewById(R.id.imagebutton_bpoi);
        if (bpoi.getImageLinks()==null){
            imageButton.setVisibility(View.INVISIBLE);
        }
        else if (bpoi.getImageLinks()!=null) {
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(bPOIPresentationActivity.this, ImageActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("bPOI", bpoi);
                    i.putExtras(b);
                    startActivity(i);
                }
            });
        }
        Button audioButton = findViewById(R.id.audiobutton_bpoi);
        if (bpoi.getAudioLink()==null){
            audioButton.setVisibility(View.INVISIBLE);
        }
        else if (bpoi.getAudioLink()!=null) {
            audioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(bPOIPresentationActivity.this, AudioActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("bPOI", bpoi);
                    i.putExtras(b);
                    startActivity(i);
                }
            });
        }
        str = bpoi.getText();

        //
        //str = poi.getText();

        final TextView textView = findViewById(R.id.TEXT_STATUS_ID_bpoi);
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
            text = str;
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


        Button textToSpeechButton = findViewById(R.id.button_texttospeech_bpoi);
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
        Button stopTextToSpeechButton = findViewById(R.id.button_stoptexttospeech_bpoi);
        if(!sp.getBoolean("texttospeech",true)){
            stopTextToSpeechButton.setVisibility(View.INVISIBLE);
        }

        stopTextToSpeechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts.stop();
            }
        });

        tts=new TextToSpeech(bPOIPresentationActivity.this, new TextToSpeech.OnInitListener() {

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





