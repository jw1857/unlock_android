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
    private ArrayList<POI> POIListCompare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int failCount = 0;
        POIXMLParser parser = new POIXMLParser(this);
        POIListCompare = parser.getPOIList();
        Intent intent = getIntent();
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
        }
        else location = "invalid";
        for (POI p : POIListCompare){
            if (location.equals(p.getTitle())){
                for (POI poi : POIList){
                    if (p.getTitle().equals(poi.getTitle())) {
                        if(!poi.getLockStatus()) {
                            Toast.makeText(this, "Location already discovered!", Toast.LENGTH_SHORT).show();
                            Intent backToMain = new Intent(ScanSuccess.this, MainActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("POIList", POIList);
                            backToMain.putExtras(bundle);
                            startActivity(backToMain);
                        }
                        if(poi.getLockStatus()) {
                            poi.setLockStatus(false);
                            mediaPlayerSuccess.start();
                            toast.show();
                            Toast.makeText(this, "New location discovered!", Toast.LENGTH_SHORT).show();
                            Intent backToMain = new Intent(ScanSuccess.this, MainActivity.class);
                            Bundle bundle1 = new Bundle();
                            bundle1.putSerializable("POIList", POIList);
                            backToMain.putExtras(bundle1);
                            startActivity(backToMain);
                        }

                    }
                }
            }
            if(!location.equals(p.getTitle())){
                failCount += 1;
                if (failCount==POIList.size()) {
                    Toast.makeText(this, "Invalid QR Code", Toast.LENGTH_SHORT).show();
                    mediaPlayerFailure.start();
                    Intent i = new Intent(this, MainActivity.class);
                    startActivity(i);
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
        startActivity(i);
    }
}