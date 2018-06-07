package com.example.jameswinters.unlock_android;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

// Hidden Point of Interest (hPOI)
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

    // Set icon for locked and unlocked hPOI
    public void setIcon(boolean locked){
        if (locked){
            this.marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.hpoi_lock_vsmallsize));
        }
        if (!locked){
            this.marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.hpoi_lock_open_vsmallsize));
        }
    }
}
