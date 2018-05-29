package com.example.jameswinters.unlock_android;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class hPOI extends POI {
    private boolean visibility;
    public hPOI(){

    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }
    public boolean getVisibility(){
        return this.visibility;
    }
}
