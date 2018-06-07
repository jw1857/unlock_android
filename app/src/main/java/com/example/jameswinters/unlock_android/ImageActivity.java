package com.example.jameswinters.unlock_android;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

// ImageActivity displays images to the screen. and implements scrolling
// between the images.

public class ImageActivity extends AppCompatActivity{

    // POI object declaration. During any PresentationActivity,
    // the correct type of POI object (bPOI/hPOI/sPOI/POI) is passed
    // to this activity through a bundle.
    // The value of this POI object is set to one of the following four objects.
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

            // The following code determines type of POI (POI/sPOI/hPOI/bPOI)
            // and sets the relevant boolean values.
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

        // Get arrayList of url links of images from Firebase to be displayed
        if (is_POI) {
            imageLinkArray = poi.getImageLinks();
        } else if (is_sPOI) {
            imageLinkArray = spoi.getImageLinks();
        } else if (is_hPOI) {
            imageLinkArray = hpoi.getImageLinks();
        } else if (is_bPOI) {
            imageLinkArray = bpoi.getImageLinks();
        }

        // Number of images for POI/sPOI/hPOI/bPOI
        numImages = imageLinkArray.size();

        // Display images using ViewPager
        // Declare String array for image urls and convert arrayList<String>
        String[] imageUriStringArray = new String[numImages];
        imageUriStringArray = imageLinkArray.toArray(imageUriStringArray);
        ViewPager viewPager = findViewById(R.id.view_pager);

        // ViewPager takes a String, not an ArrayList<String> as argument so conversion necessary
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, imageUriStringArray);
        viewPager.setAdapter(adapter);
    }

    @Override
    // If back is pressed in ImageActivity, go to the correct PresentationActivity
    public void onBackPressed() {
        super.onBackPressed();
        // If the ImageActivity was for a POI
        if (is_POI) {
            Intent i = new Intent(ImageActivity.this, POIPresentationActivity.class);
            Bundle b = new Bundle();

            // Send the same POI back to POIPresentationActivity so that activity knows what
            // content to display
            b.putSerializable("POI", poi);
            i.putExtras(b);
            startActivity(i);
        }

        // If the ImageActivity was for a sPOI
        if (is_sPOI) {
            Intent i = new Intent(ImageActivity.this, sPOIPresentationActivity.class);
            Bundle b = new Bundle();

            // Send the same sPOI back to sPOIPresentationActivity so that activity knows what
            // content to display
            b.putSerializable("sPOI", spoi);
            i.putExtras(b);
            startActivity(i);
        }

        // If the ImageActivity was for a hPOI
        if (is_hPOI) {
            Intent i = new Intent(ImageActivity.this, hPOIPresentationActivity.class);
            Bundle b = new Bundle();

            // Send the same hPOI back to hPOIPresentationActivity so that activity knows what
            // content to display
            b.putSerializable("hPOI", hpoi);
            i.putExtras(b);
            startActivity(i);
        }

        // If the ImageActivity was for a bPOI
        if (is_bPOI) {
            Intent i = new Intent(ImageActivity.this, bPOIPresentationActivity.class);
            Bundle b = new Bundle();

            // Send the same hPOI back to bPOIPresentationActivity so that activity knows what
            // content to display
            b.putSerializable("bPOI", bpoi);
            i.putExtras(b);
            startActivity(i);
        }
    }
}
