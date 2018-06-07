package com.example.jameswinters.unlock_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.speech.tts.TextToSpeech;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import static android.graphics.Color.WHITE;


// POIPresentationActivity contains the content for a POI,
// and clickable icons which lead user to activity containing content for that POI
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
        setContentView(R.layout.activity_poipresentation);

        // Get imageView for background image
        iv = findViewById(R.id.poiMainImage);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        if (b != null) {

            // Get POI from previous activity
            poi = (POI) b.getSerializable("POI");
            this.setTitle(poi.getTitle());

            // Get main image of POI and put into ImageView
            String imageString = poi.getMainImageLink();
            Picasso.get()
                    .load(imageString)
                    .fit()
                    .into(iv);
        }

        // Make video button
        ImageButton videoImageButton = (ImageButton)findViewById(R.id.poipresentation_videoimagebutton);
        if (poi.getVideoLink()==null){
            // If no video for POI, do not display video icon
            videoImageButton.setVisibility(View.INVISIBLE);
        }

        // If POI has a video, set on click listener so when pressed program takes user
        // to VideoActivity
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

        // Make image button
        ImageButton imageButton = findViewById(R.id.poipresentation_photobutton);
        if (poi.getImageLinks()==null){
            // If no images for POI, do not display image icon
            imageButton.setVisibility(View.INVISIBLE);
        }
        // If POI has an image, set on click listener so when pressed program takes user
        // to ImageActivity.
        else if (poi.getImageLinks()!=null) {
            imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(POIPresentationActivity.this, ImageActivity.class);
                Bundle b = new Bundle();

                // Pass POI through to ImageActivity so it knows what POI
                b.putSerializable("POI", poi);
                i.putExtras(b);
                startActivity(i);
            }
        });
        }

        // Make audio button
        ImageButton audioButton = findViewById(R.id.poipresentation_audio);
        if (poi.getAudioLink()==null){
            // If no audio for POI, do not display audio icon
            audioButton.setVisibility(View.INVISIBLE);
        }

        // If POI has an audio file, set on click listener so when pressed program takes user
        // to ImageActivity
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

        // Get text content from POI
        str = poi.getText();
        final TextView textView = findViewById(R.id.TEXT_STATUS_ID);

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
            text =str;
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
                // Handle any errors
            }
        });
    }

        // Make text-to-speech button
        ImageButton textToSpeechButton = findViewById(R.id.poipresentation_tts);
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
        ImageButton stopTextToSpeechButton = (ImageButton)findViewById(R.id.poipresentation_notts);
        if(!sp.getBoolean("texttospeech",true)){
            // If text-to-speech turned off, display no icon
            stopTextToSpeechButton.setVisibility(View.INVISIBLE);
        }

        // Set on click listener for stop text-to-speech
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
                    Log.e("error", "Initialization Failed!");
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
        if(text==null||"".equals(text)) {
            text = "Content not available";
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }else
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}




