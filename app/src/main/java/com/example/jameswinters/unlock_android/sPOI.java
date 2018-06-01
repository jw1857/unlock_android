package com.example.jameswinters.unlock_android;

public class sPOI extends POI {
    private boolean visibility;
    private String parentName;
    private POI parent;

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public POI getParent() {
        return parent;
    }

    public void setParent(POI parent) {
        this.parent = parent;
    }

    public boolean getVisibility(){return this.visibility;}

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    //public void setVisibility(float zoom){
        //if (parent.getLockStatus() == false){
           // if (zoom > 0){
             //   this.visibility = true;
            //}
            //else
              //  this.visibility = false;
            //}
       // }

        public sPOI(){

    }

}
