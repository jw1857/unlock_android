package com.example.jameswinters.unlock_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class POIPresentationActivity extends AppCompatActivity {
    POI poi;
    ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poipresentation);

        iv = findViewById(R.id.poiMainImage);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        if (b != null) {
            poi = (POI) b.getSerializable("POI");
            String imageString = poi.getMainImageLink();
            Picasso.get().load(imageString).into(iv);
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

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
    }


}
