package com.example.jameswinters.unlock_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
    private ArrayList<String> locationList=new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myPOIRef = database.getReference("POIList").child(currentUser.getDisplayName()).child("POIs");
    DatabaseReference myhPOIRef = database.getReference("POIList").child(currentUser.getDisplayName()).child("hPOIs");
    DatabaseReference mysPOIRef = database.getReference("POIList").child(currentUser.getDisplayName()).child("sPOIs");

    @Override @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        final MediaPlayer mediaPlayerSuccess = MediaPlayer.create(this, R.raw.unlock_success);
        final MediaPlayer mediaPlayerFailure = MediaPlayer.create(this, R.raw.unlock_failure);
        mediaPlayerSuccess.setVolume(12,12);
        mediaPlayerFailure.setVolume(12,12);

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout,
                (ViewGroup) findViewById(R.id.custom_toast_container));


        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);


        setContentView(R.layout.activity_scan_success);
        //TextView tv = findViewById(R.id.scan_text);

        if (b != null) {
            location = b.getString("Location");
        } else location = "invalid";
        POIList = MainActivity.readPOIsFromSD(POIList,currentUser);
        sPOIList = MainActivity.readsPOIsFromSD(sPOIList,currentUser);
        hPOIList = MainActivity.readhPOIsFromSD(hPOIList,currentUser);
        for (sPOI s : sPOIList) {
            for (POI p : POIList) {
                if (s.getParentName().equals(p.getTitle())) {
                    s.setParent(p);
                }
            }
        }
        for (POI p:POIList){
            locationList.add(p.getTitle());
        }
        for (sPOI s:sPOIList){
            locationList.add(s.getTitle());
        }
        for (hPOI h:hPOIList){
            locationList.add(h.getTitle());
        }
        MainActivity.muteAudio(this,mediaPlayerFailure);
        MainActivity.muteAudio(this,mediaPlayerSuccess);
        checkForError(location,mediaPlayerFailure);
        checkhPOI(location,mediaPlayerSuccess,toast);
        checksPOI(location,mediaPlayerSuccess,toast);
        checkPOI(location,mediaPlayerSuccess,toast);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, QRActivity.class);
        startActivity(i);
    }

    private void checkhPOI(String loc,MediaPlayer mp, Toast toast){
        boolean unlocked = false;
        for (hPOI h : hPOIList) {
            if ((loc.equals(h.getTitle())) && (!h.getLockStatus())&&(!h.getVisibility())) {
                h.setLockStatus(true);
                h.setVisibility(true);
                unlocked = true;
                Intent i = new Intent(ScanSuccess.this, hPOIPresentationActivity.class);
                Bundle bun = new Bundle();
                bun.putSerializable("hPOI",h);
                i.putExtras(bun);
                mp.start();
                disableAnimations(toast);
                Toast.makeText(this, "Hidden location discovered!", Toast.LENGTH_SHORT).show();
                startActivity(i);
            }
            if ((loc.equals(h.getTitle())) && (h.getVisibility()) && (h.getLockStatus()) && (!unlocked)){
                Toast.makeText(this, "Location already discovered!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ScanSuccess.this, hPOIPresentationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("hPOI",h);

                i.putExtras(bundle);
                startActivity(i);
            }

        }
    }

    private void checksPOI(String loc,MediaPlayer mp,Toast toast){
        boolean unlocked = false;
        for (sPOI s : sPOIList) {
            POI parent = s.getParent();
            if ((loc.equals(s.getTitle())) && (!parent.getLockStatus()) && (s.getLockStatus())) {
                s.setLockStatus(false);
                unlocked = true;
                Intent intent = new Intent(ScanSuccess.this, sPOIPresentationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("sPOI",s);
                mp.start();
                disableAnimations(toast);
                Toast.makeText(this, "Sub-location unlocked!", Toast.LENGTH_SHORT).show();
                intent.putExtras(bundle);
                startActivity(intent);
            }
            else if ((loc.equals(s.getTitle()))&&(parent.getLockStatus())){
                Toast.makeText(this, "Unlock Parent POI First! ", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
            }
            if ((loc.equals(s.getTitle()))&&(!s.getLockStatus())&&(!parent.getLockStatus())&&(!unlocked)) {
                Toast.makeText(this, "Location already discovered!", Toast.LENGTH_SHORT).show();
                Intent backToMain = new Intent(ScanSuccess.this, sPOIPresentationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("sPOI",s);
                startActivity(backToMain);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.savePOIListToSD(POIList,currentUser);
        MainActivity.savehPOIListToSD(hPOIList,currentUser);
        MainActivity.savesPOIListToSD(sPOIList,currentUser);
        MainActivity.updateScore(POIList,sPOIList,hPOIList,currentUser,this);
        myPOIRef.setValue(POIList);
        mysPOIRef.setValue(sPOIList);
        myhPOIRef.setValue(hPOIList);

    }

    private void checkPOI(String loc, MediaPlayer mp ,Toast toast){
        boolean unlocked =false;
        for (POI p:POIList){
            if ((loc.equals(p.getTitle()))&&(p.getLockStatus())){
                p.setLockStatus(false);
                unlocked = true;
                mp.start();
               disableAnimations(toast);
                Toast.makeText(this, "New location discovered!", Toast.LENGTH_SHORT).show();
                int unlockCount = 0;
                for (POI x : POIList) {
                    boolean ls = x.getLockStatus();
                    if (!ls) {
                        unlockCount = unlockCount + 1;
                    }
                }
                int size = POIList.size() + hPOIList.size() + hPOIList.size();
               // DatabaseReference scoreOnDb = FirebaseDatabase.getInstance().getReference().child("Scores");
              //  scoreOnDb.child(currentUser.getDisplayName()).setValue(size - unlockCount);
                Intent i = new Intent(ScanSuccess.this, POIPresentationActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("POI",p);
                i.putExtras(b);
                startActivity(i);
            }
            if ((loc.equals(p.getTitle()))&&(!p.getLockStatus())&&(!unlocked)) {
                Toast.makeText(this, "Location already discovered!", Toast.LENGTH_SHORT).show();
                Intent backToMain = new Intent(ScanSuccess.this, POIPresentationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("POI",p);
                backToMain.putExtras(bundle);
                startActivity(backToMain);
            }
        }
    }

    private void checkForError(String loc,MediaPlayer mp){
        int failCount = 0;
        int size = POIList.size()+sPOIList.size()+hPOIList.size();
        for(String l:locationList){
            if (!loc.equals(l)){
                failCount++;
            }
        }
        if (failCount==size){
            Toast.makeText(this, "Invalid QR Code", Toast.LENGTH_SHORT).show();
            mp.start();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
    }

    public void disableAnimations(Toast toast){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if(sp.getBoolean("animations",true)){
            toast.show();
        }

    }
}
