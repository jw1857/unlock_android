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
    POI poi;
    private ArrayList<String> imageLinkArray;

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
        if(b!=null){
            poi = (POI)b.getSerializable("POI");
        }

        imageLinkArray = poi.getImageLinks();
        numImages = imageLinkArray.size();
        String[] imageUriStringArray = new String[numImages];
        imageUriStringArray = imageLinkArray.toArray(imageUriStringArray);
        ViewPager viewPager = findViewById(R.id.view_pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, imageUriStringArray);
        viewPager.setAdapter(adapter);
        
    }
}
