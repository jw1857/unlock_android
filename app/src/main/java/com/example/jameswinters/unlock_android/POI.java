package com.example.jameswinters.unlock_android;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;

public class POI implements Serializable{
    private double lat;
    private double lng;
    private boolean lockStatus;
    //private transient LatLng position;
    public transient Marker marker;
    private String title;

    public POI(){
    }
    public POI (double newLat, double newLong, String name, boolean locked){
        this.lat = newLat;
        this.lng = newLong;
        this.title = name;
        this.lockStatus = locked;

    }


    public double getLat(){
        return this.lat;
    }

    public double getLng(){
        return this.lng;
    }

    public boolean getLockStatus(){
        return this.lockStatus;
    }

    //public LatLng getPosition(){
    // return this.position;
    //}

    public String getTitle(){
        return this.title;
    }

    public void setLat(double nLat){
        this.lat = nLat;
    }

    public void setLng(double nLng){
        this.lng = nLng;
    }

    //   public void setPosition(double lat, double lng){
    //     this.position = new LatLng(lat,lng);
    //}
    public void setLockStatus(boolean ls){
        this.lockStatus = ls;
    }

    public void setIcon(boolean locked){
        if (locked){
            this.marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.lock));
        }
        if (!locked){
            this.marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.unlock));
        }
    }

    public void setTitle(String newTitle){
        this.title = newTitle;
    }
}