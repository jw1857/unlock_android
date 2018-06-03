package com.example.jameswinters.unlock_android;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity{

    private POI poi;
    private sPOI spoi;
    private hPOI hpoi;
    private bPOI bpoi;
   
    private ArrayList<String> imageLinkArray;
    private boolean is_sPOI = false;
    private boolean is_hPOI = false;
    private boolean is_POI = false;
    private boolean is_bPOI=false;

    private int numImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_image);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        if (b != null) {

            poi = (POI) b.getSerializable("POI");
            bpoi = (bPOI) b.getSerializable("bPOI");
            spoi = (sPOI) b.getSerializable("sPOI");
            hpoi = (hPOI) b.getSerializable("hPOI");
            if (!(poi == null)) {
                is_POI = true;
                is_sPOI = false;
                is_hPOI = false;
                is_bPOI = false;
            } else if (!(spoi == null)) {
                is_sPOI = true;
                is_POI = false;
                is_hPOI = false;
                is_bPOI = false;
            } else if (!(hpoi == null)) {
                is_hPOI = true;
                is_POI = false;
                is_sPOI = false;
                is_bPOI = false;
            } else if (bpoi != null) {
                is_hPOI = false;
                is_POI = false;
                is_sPOI = false;
                is_bPOI = true;
            }


        }

        //imageLinkArray = poi.getImageLinks();
        if (is_POI) {
            imageLinkArray = poi.getImageLinks();
        } else if (is_sPOI) {
            imageLinkArray = spoi.getImageLinks();
        } else if (is_hPOI) {
            imageLinkArray = hpoi.getImageLinks();
        } else if (is_bPOI) {
            imageLinkArray = bpoi.getImageLinks();
        }


        numImages = imageLinkArray.size();

        String[] imageUriStringArray = new String[numImages];
        imageUriStringArray = imageLinkArray.toArray(imageUriStringArray);
        ViewPager viewPager = findViewById(R.id.view_pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, imageUriStringArray);
        viewPager.setAdapter(adapter);
        
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (is_POI) {
            Intent i = new Intent(ImageActivity.this, POIPresentationActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("POI", poi);
            i.putExtras(b);
            startActivity(i);
        }
        if (is_sPOI) {
            Intent i = new Intent(ImageActivity.this, sPOIPresentationActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("sPOI", spoi);
            i.putExtras(b);
            startActivity(i);
        }
        if (is_hPOI) {
            Intent i = new Intent(ImageActivity.this, hPOIPresentationActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("hPOI", hpoi);
            i.putExtras(b);
            startActivity(i);
        }
        if (is_bPOI) {
            Intent i = new Intent(ImageActivity.this, bPOIPresentationActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("bPOI", bpoi);
            i.putExtras(b);
            startActivity(i);
        }
    }
}
