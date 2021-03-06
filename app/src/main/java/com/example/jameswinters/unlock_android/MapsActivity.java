package com.example.jameswinters.unlock_android;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

// MapsActivity displays icons for POI/sPOI/bPOI/hPOIs at their geolocation
// Clicking an icon takes user to respective presentationActivity.

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";


    // Get list of POI/sPOI/bPOI/hPOIs from Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser =mAuth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myPOIRef = database.getReference("POIList").child(currentUser.getDisplayName()).child("POIs");
    DatabaseReference mysPOIRef = database.getReference("POIList").child(currentUser.getDisplayName()).child("sPOIs");
    DatabaseReference myhPOIRef = database.getReference("POIList").child(currentUser.getDisplayName()).child("hPOIs");
    double POIProgress;


    private ArrayList<POI> POIList;
    private ArrayList<sPOI> sPOIList;
    private ArrayList<hPOI> hPOIList;
    private ArrayList<bPOI> bPOIList;


    // Setting up the map based on coordinates of York
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLngBounds YORK = new LatLngBounds(
            new LatLng (53.926343, -1.156002), new LatLng(53.993656, -1.022793));


    // This zoom level is the default zoom level that the map loads to
    private static final float DEFAULT_ZOOM = 14.5f;


    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);

    }

    // Checks value of progress and updates progress bar
    private void setProgressValue(double progress){
        ProgressBar pb = findViewById(R.id.progressBar);
        pb.setProgress((int)progress);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ProgressBar pb = findViewById(R.id.progressBar);
                int progressCount=0;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (POI p:POIList) {
                    boolean lsp = p.getLockStatus();
                    if (!lsp) {
                        progressCount = progressCount + 1;
                    }
                }
                for (sPOI s:sPOIList) {
                    boolean lss = s.getLockStatus();
                    if (!lss) {
                        progressCount = progressCount + 1;
                    }
                }
                for (hPOI h:hPOIList){
                    boolean lsh = h.getVisibility();
                    if (lsh){
                        progressCount = progressCount +1;
                    }
                }
               int  size = POIList.size()+hPOIList.size()+sPOIList.size();
                POIProgress = ((float)progressCount/(float)size*pb.getMax());
                setProgressValue(POIProgress);
            }
        });
        thread.start();

    }


    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat" + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the device location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location");
                            Location currentLocation = (Location)task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);
                            mMap.setMyLocationEnabled(true);
                        }
                        else{
                            Log.d(TAG, "onComplete: current location null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT);
                        }
                    }
                });
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        ProgressBar pb = findViewById(R.id.progressBar);
        pb.setMax(100);
        mMap = googleMap;

        // If progress bar pressed go to ProgressTableActivity to see user progress
        pb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapsActivity.this, ProgressTableActivity.class);
                startActivity(i);
            }
        });

        // Set up map
        int progressCount=0;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.map_style));
        mMap.setLatLngBoundsForCameraTarget(YORK);

        // Add POI markers to map
        addPOIMarkers(POIList);

        // Show sPOIs depending on zoom level
        for (sPOI s:sPOIList){
            if(mMap.getCameraPosition().zoom>15){
                    s.setVisibility(true);
            }
            else if (mMap.getCameraPosition().zoom<15) {
                    s.setVisibility(false);
            }
        }

        addsPOIMarkers(POIList,sPOIList);
        addhPOIMarkers(hPOIList);
        addbPOIMarkers(bPOIList);
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                for (sPOI s:sPOIList){
                    if(mMap.getCameraPosition().zoom>15){
                        if(s.marker!=null) {
                            s.setVisibility(true);
                            s.marker.setVisible(s.getVisibility());
                        }
                    }
                    else if (mMap.getCameraPosition().zoom<15) {
                        if (s.marker != null) {
                            s.setVisibility(false);
                            s.marker.setVisible(s.getVisibility());
                        }
                    }
                }
            }
        });

        // Logic to interpret user press input
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
            @Override
            public boolean onMarkerClick(final Marker marker) {
                int unlockCount =0;
                // Go through list of hPOIs and if hPOI pressed go to hPOIPresentationActivity
                for (hPOI h : hPOIList) {
                    if ((marker.equals(h.marker)&&(h.marker.isVisible()))) {
                        if (h.getLockStatus()) {
                            Intent i1 = new Intent(MapsActivity.this, hPOIPresentationActivity.class);
                            Bundle b = new Bundle();
                            b.putSerializable("hPOI", h);
                            i1.putExtras(b);
                            startActivity(i1);
                        }
                    }
                }
                // Go through list of bPOIs and if bPOI pressed go to hPOIPresentationActivity
                for (bPOI b : bPOIList) {
                    if (marker.equals(b.marker)) {
                       Intent i2 = new Intent(MapsActivity.this,bPOIPresentationActivity.class);
                        Bundle x= new Bundle();
                        x.putSerializable("bPOI",b);
                        i2.putExtras(x);
                        startActivity(i2);
                    }
               }

                // Go through list of sPOIs and if sPOI unlocked and pressed go to hPOIPresentationActivity
                for (sPOI s : sPOIList) {
                    if ((marker.equals(s.marker)&&(s.marker.isVisible()))) {
                        if(s.getLockStatus()) {
                            s.marker.showInfoWindow();
                        }
                    if(!s.getLockStatus()) {
                        Intent i3 = new Intent(MapsActivity.this, sPOIPresentationActivity.class);
                        Bundle b = new Bundle();
                        b.putSerializable("sPOI", s);
                        i3.putExtras(b);
                        startActivity(i3);
                    }
                    }
                }

                // Go through list of POIs and if POI unlocked add the relevant sPOIs associated with
                // that POI
                for (POI p : POIList){
                    if (marker.equals(p.marker)){
                        if (p.getLockStatus()) {
                            p.marker.showInfoWindow();
                            for (sPOI s : sPOIList) {
                                if (s.getParentName().equals(p.getTitle())) {
                                    if (!p.getLockStatus()) {
                                        addSinglesPOIMarker(s);
                                    }
                                }
                            }
                        }
                    // if POI unlocked and pressed go to POIPresentationActivity
                    else if(!p.getLockStatus()){
                        Intent i4 = new Intent(MapsActivity.this,POIPresentationActivity.class);
                        Bundle b= new Bundle();
                        b.putSerializable("POI",p);
                        i4.putExtras(b);
                        startActivity(i4);
                        }
                    }
                }

                // Update the user progress
                for (POI p:POIList) {
                    boolean lsp = p.getLockStatus();
                    if (!lsp) {
                        unlockCount = unlockCount + 1;
                    }
                }
                for (sPOI s:sPOIList) {
                    boolean lss = s.getLockStatus();
                    if (!lss) {
                        unlockCount = unlockCount + 1;
                    }
                }
                for (hPOI h:hPOIList){
                    boolean lsh = h.getVisibility();
                    if (lsh){
                        unlockCount = unlockCount +1;
                    }
                }
                myPOIRef.setValue(POIList);
                mysPOIRef.setValue(sPOIList);
                myhPOIRef.setValue(hPOIList);
                return true;
            }
        });

        System.out.println(progressCount);
        System.out.println(POIList.size());
        int size = POIList.size()+sPOIList.size()+hPOIList.size();
        POIProgress = (float)progressCount/(float)size;
        System.out.println(POIProgress);
        POIProgress = POIProgress*pb.getMax();
        System.out.println(POIProgress);
        setProgressValue(POIProgress);
        getDeviceLocation();
    }

    // Get list of all POIs (containing their locked status and more) from SD card
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_maps);
        initMap();
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        POIList = MainActivity.readPOIsFromSD(POIList,currentUser);
        sPOIList = MainActivity.readsPOIsFromSD(sPOIList,currentUser);
        hPOIList = MainActivity.readhPOIsFromSD(hPOIList,currentUser);
        bPOIList =MainActivity.readbPOIsFromSD(bPOIList,currentUser);
    }

    // Method to add single sPOI marker to map
    private void addSinglesPOIMarker(sPOI s){
        s.marker =  mMap.addMarker(new MarkerOptions()
                .visible(s.getVisibility())
                .position(new LatLng(s.getLat(),s.getLng()))
                .title(s.getTitle()));
        s.setIcon(s.getLockStatus());
    }

    // Method to add single POI marker to map
    private void addPOIMarkers(ArrayList<POI> poi) {
        for (POI p : poi) {
            p.marker =  mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(p.getLat(),p.getLng()))
                    .title(p.getTitle()));
            p.setIcon(p.getLockStatus());
        }
    }

    // Method to add single bPOI marker to map
    private void addbPOIMarkers(ArrayList<bPOI> bpoi) {
        for (bPOI b : bpoi) {
            b.marker =  mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(b.getLat(),b.getLng()))
                    .title(b.getTitle()));
            b.setBusinessIconType(b.getType());
        }
    }

    // Method to add single hPOI marker to map
    // Always unlocked so it is assigned custom hPOI unlocked icon
    private void addhPOIMarkers(ArrayList<hPOI> hpoi){
        for (hPOI h: hpoi){
            if (h.getVisibility()){
                h.marker =  mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(h.getLat(),h.getLng()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.hpoi_lock))
                        .title(h.getTitle()));
            }
        }
    }

    // Add list of sPOIs to map
    private void addsPOIMarkers(ArrayList<POI> poi, ArrayList<sPOI> spoi) {
        for (sPOI s : spoi) {
            for (POI p : poi) {
                if (s.getParentName().equals(p.getTitle())) {
                    s.setParent(p);
                    if (!p.getLockStatus()) {
                        s.marker =  mMap.addMarker(new MarkerOptions()
                                .visible(s.getVisibility())
                                .position(new LatLng(s.getLat(),s.getLng()))
                                .title(s.getTitle()));
                        s.setIcon(s.getLockStatus());
                    }
                }
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.savePOIListToSD(POIList,currentUser);
        MainActivity.savehPOIListToSD(hPOIList,currentUser);
        MainActivity.savesPOIListToSD(sPOIList,currentUser);
        MainActivity.savebPOIListToSD(bPOIList,currentUser);
        MainActivity.updateScore(POIList,sPOIList,hPOIList,currentUser,this);
    }
    @Override
    public void onBackPressed(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

}