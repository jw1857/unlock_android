package com.example.jameswinters.unlock_android;

import android.support.test.filters.SmallTest;

import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.test.ActivityInstrumentationTestCase2;


import com.robotium.solo.Solo;

import pl.droidsonroids.gif.GifImageView;


/**
 * Created by McKeown on 08/03/2018.
 */
public class MIT16_0 extends ActivityInstrumentationTestCase2<MapsActivity>{


    public MIT16_0() {
        super(MapsActivity.class);
    }
    private Solo solo;
    /*@Rule
    public ActivityTestRule<ScanSuccess> activityTestRule =
            new ActivityTestRule<>(ScanSuccess.class);
*/
    @Override
    protected void setUp() throws Exception {
        solo = new Solo(getInstrumentation());
        super.setUp();

    }

  /*  private Callable<Boolean> newQRscan(Activity thisActivity) {
        final TextView v = thisActivity.findViewById(R.id.txtResult);
        return new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return !(v.getText().toString().equals("Please focus camera to QR Code")); // The condition that must be fulfilled
            }
        };
    }*/




    @SmallTest
    public void test() {
        //set zoom to closer
        getActivity();
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Heslington Hall"));//locked (s)POI
        try {
            marker.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        solo.assertCurrentActivity("Wrong Activity",MapsActivity.class);//stays in map for locked POI
        UiObject marker2 = device.findObject(new UiSelector().descriptionContains("University of York"));//unlocked POI
        try{
            marker2.click();
        } catch (UiObjectNotFoundException e){
            e.printStackTrace();
        }
        solo.assertCurrentActivity("Wrong Activity",POIPresentationActivity.class);
        assertTrue(solo.waitForText("University of York"));


    }
}