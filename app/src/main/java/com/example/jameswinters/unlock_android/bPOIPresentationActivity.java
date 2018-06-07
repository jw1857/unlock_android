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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import static android.graphics.Color.WHITE;


// bPOIPresentationActivity contains the relevant content for a particular business,
// and clickable icons which lead user to activity containing relevant content for that business
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
        setContentView(R.layout.activity_bpoipresentation);

        // Get imageView for background image
        iv = findViewById(R.id.bpoiMainImage);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        if (b != null) {
            // Get bPOI from previous activity
            bpoi = (bPOI) b.getSerializable("bPOI");
            this.setTitle(bpoi.getTitle());

            // Get main image of bPOI and put into ImageView
            String imageString = bpoi.getMainImageLink();
            Picasso.get()
                    .load(imageString)
                    .fit()
                    .into(iv);
        }

        // Make video button
        ImageButton videoButton = findViewById(R.id.bpoipresentation_videoimagebutton);
        if (bpoi.getVideoLink()==null){
            // If no video for bPOI, do not display video icon
            videoButton.setVisibility(View.INVISIBLE);
        }

        // If bPOI has a video, set on click listener so when pressed program takes user
        // to VideoActivity
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

        // Make image button
        ImageButton imageButton = findViewById(R.id.bpoipresentation_photobutton);
        if (bpoi.getImageLinks()==null){
            // If no images for bPOI, do not display image icon
            imageButton.setVisibility(View.INVISIBLE);
        }

        // If bPOI has an image, set on click listener so when pressed program takes user
        // to ImageActivity.
        else if (bpoi.getImageLinks()!=null) {
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(bPOIPresentationActivity.this, ImageActivity.class);
                    Bundle b = new Bundle();

                    // Pass bPOI through to ImageActivity so it knows what bPOI
                    b.putSerializable("bPOI", bpoi);
                    i.putExtras(b);
                    startActivity(i);
                }
            });
        }

        // Make audio button
        ImageButton audioButton = findViewById(R.id.bpoipresentation_audio);
        if (bpoi.getAudioLink()==null){
            // If no audio for bPOI, do not display audio icon
            audioButton.setVisibility(View.INVISIBLE);
        }

        // If bPOI has an audio file, set on click listener so when pressed program takes user
        // to ImageActivity
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

        // Get text content from bPOI
        str = bpoi.getText();
        final TextView textView = findViewById(R.id.bpoi_TEXT_STATUS_ID);

        // Get text size setting from settings and change text size
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
        // Display text to textView
        if ((str.length()<30)||(str.equals(null))){
            textView.setText(str);
            text = str;
        }

        // If text is longer than textView can handle, implement scrolling
        else if ((str.length()>30)){
            StorageReference txtRef = storage.getReferenceFromUrl(str);
            final long ONE_MEGABYTE = 1024 * 1024;
            txtRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                                   @Override
                                                                   public void onSuccess(byte[] bytes) {
               text = new String(bytes);
               textView.setText(text);
               textView.setTextColor(WHITE);
                                                                   }
                                                               }
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        }

        // Make text-to-speech button
        ImageButton textToSpeechButton = findViewById(R.id.bpoipresentation_tts);
        if(!sp.getBoolean("texttospeech",true)){
            // If text-to-speech turned off, display no icon
            textToSpeechButton.setVisibility(View.INVISIBLE);
        }

        // Set on click listener for text-to-speech button
        textToSpeechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sp.getBoolean("audio",true)){
                    tts.shutdown();
                }
                ConvertTextToSpeech();
            }
        });

        // Make stop text-to-speech button
        ImageButton stopTextToSpeechButton = findViewById(R.id.bpoipresentation_notts);
        if(!sp.getBoolean("texttospeech",true)){
            stopTextToSpeechButton.setVisibility(View.INVISIBLE);
        }

        // Set on click listener for stop text-to-speech
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
                    Log.e("error", "Initialization Failed!");
            }
        });

    }

    // Go back to MapsActivity if back is pressed
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
        if(text==null||"".equals(text)){
            text = "Content not available";
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }else
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}





