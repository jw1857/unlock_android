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

public class bPOIPresentationActivity extends AppCompatActivity {

    bPOI bpoi;

    ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poipresentation);

        iv = findViewById(R.id.bpoiMainImage); // need to change
        Intent i = getIntent();
        Bundle b = i.getExtras();
        if (b != null) {
            bpoi = (bPOI) b.getSerializable("bPOI");
            String imageString = bpoi.getMainImageLink();
//            Picasso.get().load(imageString).into(iv);
        }
        Button videoButton = findViewById(R.id.videobutton_bpoi);
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
        Button imageButton = findViewById(R.id.imagebutton_bpoi);
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
        Button audioButton = findViewById(R.id.audiobutton_bpoi);
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

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MapsActivity.class);
        Bundle b = new Bundle();
        i.putExtras(b);
        startActivity(i);
    }


}
