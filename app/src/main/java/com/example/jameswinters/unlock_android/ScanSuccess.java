package com.example.jameswinters.unlock_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;


public class ScanSuccess extends AppCompatActivity {
    String location = "null";
    private ArrayList<POI> POIList;
    private ArrayList<sPOI> sPOIList;
    private ArrayList<hPOI> hPOIList;
    private ArrayList<POI> POIListCompare;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int failCount = 0;
        POIXMLParser parser = new POIXMLParser(this);
        POIListCompare = parser.getPOIList();
        Intent intent = getIntent();
        boolean is_hPOI =false;
        Bundle b = intent.getExtras();
        final MediaPlayer mediaPlayerSuccess = MediaPlayer.create(this, R.raw.unlock_success);
        final MediaPlayer mediaPlayerFailure = MediaPlayer.create(this, R.raw.unlock_failure);


        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout,
                (ViewGroup) findViewById(R.id.custom_toast_container));


        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);




        setContentView(R.layout.activity_scan_success);
        TextView tv = findViewById(R.id.scan_text);
        if (b!=null) {
            location = b.getString("Location");
            POIList = (ArrayList<POI>)b.getSerializable("POIList");
            sPOIList =  (ArrayList<sPOI>)b.getSerializable("sPOIList");
            hPOIList = (ArrayList<hPOI>)b.getSerializable("hPOIList");
        }
        else location = "invalid";
        for (hPOI h : hPOIList){
            if ((location.equals(h.getTitle()))&&(!h.getVisibility())){
                h.setVisibility(true);
                Intent i= new Intent(ScanSuccess.this, MapsActivity.class);
                Bundle bun = new Bundle();
                mediaPlayerSuccess.start();
                Toast.makeText(this, "Hidden location discovered!", Toast.LENGTH_SHORT).show();
                bun.putSerializable("POIList",POIList);
                bun.putSerializable("sPOIList",sPOIList);
                bun.putSerializable("hPOIList",hPOIList);
                is_hPOI= true;
                i.putExtras(bun);
                startActivity(i);
            }
        }
        if (!is_hPOI) {
            for (POI p : POIListCompare) {
                if (location.equals(p.getTitle())) {
                    for (POI poi : POIList) {
                        if (p.getTitle().equals(poi.getTitle())) {
                            if (!poi.getLockStatus()) {
                                Toast.makeText(this, "Location already discovered!", Toast.LENGTH_SHORT).show();
                                Intent backToMain = new Intent(ScanSuccess.this, MainActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("POIList", POIList);
                                bundle.putSerializable("sPOIList", sPOIList);
                                bundle.putSerializable("hPOIList", hPOIList);
                                backToMain.putExtras(bundle);
                                startActivity(backToMain);
                            }
                            if (poi.getLockStatus()) {
                                poi.setLockStatus(false);
                                mediaPlayerSuccess.start();
                                toast.show();
                                Toast.makeText(this, "New location discovered!", Toast.LENGTH_SHORT).show();
                                int unlockCount = 0;
                                for (POI x : POIList) {
                                    boolean ls = x.getLockStatus();
                                    if (!ls) {
                                        unlockCount = unlockCount + 1;
                                    }
                                }
                                DatabaseReference scoreOnDb = FirebaseDatabase.getInstance().getReference().child("Scores");
                                scoreOnDb.child(currentUser.getDisplayName()).setValue(POIList.size() - unlockCount);
                                Intent backToMain = new Intent(ScanSuccess.this, MapsActivity.class);
                                Bundle bundle1 = new Bundle();
                                bundle1.putSerializable("POIList", POIList);
                                bundle1.putSerializable("sPOIList", sPOIList);
                                bundle1.putSerializable("hPOIList", hPOIList);
                                backToMain.putExtras(bundle1);
                                startActivity(backToMain);
                            }

                        }
                    }
                }
                if (!location.equals(p.getTitle())) {
                    failCount += 1;
                    if (failCount == POIList.size()) {
                        Toast.makeText(this, "Invalid QR Code", Toast.LENGTH_SHORT).show();
                        mediaPlayerFailure.start();
                        Intent i = new Intent(this, MainActivity.class);
                        Bundle bundle1 = new Bundle();
                        bundle1.putSerializable("POIList", POIList);
                        bundle1.putSerializable("sPOIList", sPOIList);
                        bundle1.putSerializable("hPOIList", hPOIList);
                        i.putExtras(bundle1);
                        startActivity(i);
                    }
                }
            }
        }
    }

    //switch (location){
    //  case "minster":
    //    tv.setText(R.string.minster);
    //mediaPlayerSuccess.start();
    //  toast.show();

//                break;
    //          case "jorvik":
    //            tv.setText(R.string.jorvik);
    //          //mediaPlayerSuccess.start();
    //        toast.show();

//                break;
    //          default:
    //            CharSequence error = "Invalid QR Code";
    //mediaPlayerFailure.start();
    //          int duration = Toast.LENGTH_SHORT;
    //        Toast toastText = Toast.makeText(this,error,duration);
    //      toastText.show();
    //    Intent i =new Intent(this,MainActivity.class);
    //  startActivity(i);
    //}



    @Override
    public void onBackPressed(){
        Intent i = new Intent(this, QRActivity.class);
        Bundle b =new Bundle();
        b.putSerializable("POIList",POIList);
        b.putSerializable("sPOIList",sPOIList);
        b.putSerializable("hPOIList",hPOIList);
        i.putExtras(b);
        startActivity(i);
    }
}