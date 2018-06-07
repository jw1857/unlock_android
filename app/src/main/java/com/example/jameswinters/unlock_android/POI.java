package com.example.jameswinters.unlock_android;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import java.io.Serializable;
import java.util.ArrayList;

// Point of Interest (POI)
// Each POI has coordinates (latitude longitude), a lock status, a marker for the map,
// a Firebase url link to video, audio and text, and a background image.

public class POI implements Serializable{
    private double lat;
    private double lng;
    private boolean lockStatus;
    public transient Marker marker;
    private String title;
    private String videoLink;
    private String mainImageLink;
    private String audioLink;
    private String text;
    private ArrayList<String> imageLinks =new ArrayList<>();

    public POI(){
    }
    public POI (double newLat, double newLong, String name, boolean locked){
        this.lat = newLat;
        this.lng = newLong;
        this.title = name;
        this.lockStatus = locked;
    }

    // These set and get methods are called on a POI object, and will set/return the relevant information.
    // The information for each POI is stored in poicoords.xml
    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setAudioLink(String audioLink) {
        this.audioLink = audioLink;
    }

    public void setMainImageLink(String mainImageLink) {
        this.mainImageLink = mainImageLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public void setImageLinks(ArrayList<String> imageLinks) {
        this.imageLinks = imageLinks;
    }

    public String getAudioLink() {
        return audioLink;
    }

    public String getMainImageLink() {
        return mainImageLink;
    }

    public ArrayList<String> getImageLinks() {
        return imageLinks;
    }

    public String getVideoLink() {
        return videoLink;
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

    public String getTitle(){
        return this.title;
    }

    public void setLat(double nLat){
        this.lat = nLat;
    }

    public void setLng(double nLng){
        this.lng = nLng;
    }

    public void setLockStatus(boolean ls){
        this.lockStatus = ls;
    }

    // Set icon if POI is locked/unlocked
    public void setIcon(boolean locked){
        if (locked){
            this.marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.lock_vsmallsize));
        }
        if (!locked){
            this.marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.lock_open_vsmallsize));
        }
    }

    public void setTitle(String newTitle){
        this.title = newTitle;
    }
}