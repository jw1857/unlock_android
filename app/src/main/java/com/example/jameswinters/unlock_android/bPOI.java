package com.example.jameswinters.unlock_android;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

// Business Point of Interest (bPOI)

public class bPOI extends POI {

    private String type;

    // bPOI always unlocked.
    public bPOI(){
        this.setLockStatus(false);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }


    // Depending on the type of business, set an appropriate icon
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
