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
    private ArrayList<POI> POIList;
    private ArrayList<sPOI> sPOIList;
    POI poi;
    private ArrayList<hPOI> hPOIList;
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
            POIList = (ArrayList<POI>) b.getSerializable("POIList");
            sPOIList = (ArrayList<sPOI>) b.getSerializable("sPOIList");
            hPOIList=(ArrayList<hPOI>) b.getSerializable("hPOIList");
            String imageString = poi.getMainImageLink();
            Picasso.get().load(imageString).into(iv);
        }
        Button videoButton = findViewById(R.id.videobutton);
        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(POIPresentationActivity.this, VideoActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("POI", poi);
                b.putSerializable("POIList", POIList);
                b.putSerializable("sPOIList", sPOIList);
                b.putSerializable("hPOIList", hPOIList);
                i.putExtras(b);
                startActivity(i);
            }
        });
        Button imageButton = findViewById(R.id.imagebutton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(POIPresentationActivity.this, ImageActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("POI", poi);
                b.putSerializable("POIList", POIList);
                b.putSerializable("sPOIList", sPOIList);
                b.putSerializable("hPOIList", hPOIList);
                i.putExtras(b);
                startActivity(i);
            }
        });
        Button audioButton = findViewById(R.id.audiobutton);
        audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(POIPresentationActivity.this, AudioActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("POI", poi);
                b.putSerializable("POIList", POIList);
                b.putSerializable("sPOIList", sPOIList);
                b.putSerializable("hPOIList", hPOIList);
                i.putExtras(b);
                startActivity(i);
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MapsActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("POIList", POIList);
        b.putSerializable("sPOIList", sPOIList);
        b.putSerializable("hPOIList", hPOIList);
        i.putExtras(b);
        startActivity(i);
    }


}
