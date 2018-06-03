package com.example.jameswinters.unlock_android;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class bPOI extends POI {

    private String type;
    public bPOI(){
        this.setLockStatus(false);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setBusinessIconType(String type){
        switch(type) {
            case "cafe":
                this.marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.cafe));
                break;

            case "restaurant":
                this.marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant));
                break;

            case "pub":
                this.marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bar));
                break;

            case "hotel":
                this.marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.hotel));
                break;

            case "shop":
                this.marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.shop));
                break;
        }
    }
}
