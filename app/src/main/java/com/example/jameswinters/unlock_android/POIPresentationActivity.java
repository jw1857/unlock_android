package com.example.jameswinters.unlock_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class POIPresentationActivity extends AppCompatActivity {

    ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poipresentation);
        POI poi;
        iv = findViewById(R.id.poiMainImage);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        if (b != null) {
            poi = (POI) b.getSerializable("POI");
            String imageString = poi.getMainImageLink();
            Picasso.with(this).load(imageString).into(iv);
        }
    }
}
