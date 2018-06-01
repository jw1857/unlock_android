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
   
    private ArrayList<String> imageLinkArray;
    private boolean is_sPOI = false;
    private boolean is_hPOI = false;
    private boolean is_POI = false;

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

            spoi = (sPOI)b.getSerializable("sPOI");
            hpoi = (hPOI)b.getSerializable("hPOI");
            if(!(poi == null)){
                is_POI = true;
                is_sPOI = false;
                is_hPOI = false;
            }
            else if(!(spoi == null)){
                is_sPOI = true;
                is_POI = false;
                is_hPOI = false;
            }
            else if (!(hpoi == null)){
                is_hPOI = true;
                is_POI = false;
                is_sPOI = false;
            }
            

        }

        //imageLinkArray = poi.getImageLinks();
        if(is_POI){
            imageLinkArray = poi.getImageLinks();
        }
        else if(is_sPOI){
            imageLinkArray = spoi.getImageLinks();
        }
        else if(is_hPOI){
            imageLinkArray = hpoi.getImageLinks();
        }


        numImages = imageLinkArray.size();

        String[] imageUriStringArray = new String[numImages];
        imageUriStringArray = imageLinkArray.toArray(imageUriStringArray);
        ViewPager viewPager = findViewById(R.id.view_pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, imageUriStringArray);
        viewPager.setAdapter(adapter);
        
    }
}
