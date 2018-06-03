package com.example.jameswinters.unlock_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class sPOIPresentationActivity extends AppCompatActivity {
    sPOI spoi;
    ImageView iv;
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
            Picasso.get().load(imageString).into(iv);
        }
        ImageButton videoButton = findViewById(R.id.spoipresentation_videoimagebutton);
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
        ImageButton imageButton = findViewById(R.id.spoipresentation_photobutton);
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
        ImageButton audioButton = findViewById(R.id.spoipresentation_audio);
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

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
    }


}
