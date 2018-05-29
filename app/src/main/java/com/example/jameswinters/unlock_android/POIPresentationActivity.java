package com.example.jameswinters.unlock_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class POIPresentationActivity extends AppCompatActivity {
    private ArrayList<POI> POIList;
    private ArrayList<sPOI> sPOIList;
    private ArrayList<hPOI> hPOIList;
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
            POIList = (ArrayList<POI>) b.getSerializable("POIList");
            sPOIList = (ArrayList<sPOI>) b.getSerializable("sPOIList");
            hPOIList=(ArrayList<hPOI>) b.getSerializable("hPOIList");
            String imageString = poi.getMainImageLink();
            Picasso.with(this).load(imageString).into(iv);
        }
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
