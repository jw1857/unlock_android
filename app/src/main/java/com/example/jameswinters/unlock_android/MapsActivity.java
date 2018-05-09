package com.example.jameswinters.unlock_android;


import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Xml;
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

import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser =mAuth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("POIList").child(currentUser.getUid());
    double progressy;
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private Boolean mLocationPermissionGranted = false;
    private ArrayList<POI> POIList;
    private GoogleMap mMap;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLngBounds YORK = new LatLngBounds(
            new LatLng (53.926343, -1.156002), new LatLng(53.993656, -1.022793));

    private static final float DEFAULT_ZOOM = 15f;

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);

    }

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
                for (POI p : POIList){
                    boolean ls = p.getLockStatus();
                    if (!ls){
                        progressCount = progressCount + 1;
                    }
                }
                progressy = ((float)progressCount/(float)POIList.size())*pb.getMax();
                setProgressValue(progressy);
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
            if(mLocationPermissionGranted){
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
            }

        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        ProgressBar pb = findViewById(R.id.progressBar);
        pb.setMax(100);
        Toast.makeText(this, "Map Ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        int progressCount=0;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.map_style));
        mMap.setLatLngBoundsForCameraTarget(YORK);
        //Toast.makeText(this,POIList.get(0).getTitle() , Toast.LENGTH_SHORT).show();
        addPOIMarkers(POIList);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
            @Override
            public boolean onMarkerClick(final Marker marker) {

                for (POI p : POIList){
                    if (marker.equals(p.marker)){
                        p.setIcon(!p.getLockStatus());
                        p.marker.showInfoWindow();
                        p.setLockStatus(!p.getLockStatus());
                        //Toast.makeText(MapsActivity.this,"marker pressed", Toast.LENGTH_SHORT).show();
                    }
                }
                myRef.setValue(POIList);
                return true;
            }
        });
        for (POI p : POIList){
            boolean ls = p.getLockStatus();
            if (!ls){
                progressCount = progressCount + 1;
            }
        }
        System.out.println(progressCount);
        System.out.println(POIList.size());
        int size = POIList.size();
        progressy = (float)progressCount/(float)size;
        System.out.println(progressy);
        progressy = progressy*pb.getMax();
        System.out.println(progressy);
        setProgressValue(progressy);
        if(mLocationPermissionGranted){
            getDeviceLocation();
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getLocationPermission();
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if (b!=null) {
            POIList = (ArrayList<POI>) b.getSerializable("POIList");
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionGranted = false;
        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionGranted = true;
                    // Initialize map
                    initMap();
                }
            }
        }
    }

    private void lockedPOI(POI poi){
        // poi.setPosition(poi.getLat(),poi.getLng());
        poi.marker =  mMap.addMarker(new MarkerOptions()
                .position(new LatLng(poi.getLat(),poi.getLng()))
                .title(poi.getTitle())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.lock)));

    }
    private void unlockedPOI(POI poi){
        // poi.setPosition(poi.getLat(),poi.getLng());
        poi.marker =  mMap.addMarker(new MarkerOptions()
                .position(new LatLng(poi.getLat(),poi.getLng()))
                .title(poi.getTitle())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.unlock)));

    }

    private void addPOIMarkers(ArrayList<POI> poi) {
        for (POI p : poi) {
            boolean locked = p.getLockStatus();
            if (locked) {
                lockedPOI(p);
            }
            if (!locked) {
                unlockedPOI(p);
            }
        }
        // }
    }

    //private void GPSUnlocking() {
    //  Location myLocation = mMap.getMyLocation();
    //if ((myLocation.getLatitude()) <= (minsterLat + 0.0003) || (myLocation.getLatitude()) >= (minsterLat - 0.0009)
    //      && (myLocation.getLongitude()) <= (minsterLong + 0.0013) || (myLocation.getLongitude()) >= (minsterLong - 0.0014)){
    //minster.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.unlock));
    //}
    //}

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                Log.d(TAG, "getLocationPermission: Self Permission Granted");
                Toast.makeText(this, "Self Permission Granted", Toast.LENGTH_SHORT).show();
                mLocationPermissionGranted = true;
                initMap();
            }
            else{
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }

        }
        else{
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }


    }
    @Override
    public void onBackPressed(){
        Intent i = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("POIList",POIList);
        //bundle.putInt("From_Activity",1);
        i.putExtras(bundle);
        startActivity(i);
    }


}