package com.example.jameswinters.unlock_android;

import android.support.test.filters.SmallTest;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.test.ActivityInstrumentationTestCase2;



import com.robotium.solo.Solo;

// UT 1.2
// In MapsActivity, zoom in around POI. Click on sPOI to go to sPOIPresentationActivity

public class UT1_2 extends ActivityInstrumentationTestCase2<MapsActivity>{

    // Start in MapsActivity
    public UT1_2() {
        super(MapsActivity.class);
    }

    private Solo solo;

    @Override
    protected void setUp() throws Exception{
        solo = new Solo(getInstrumentation());
        super.setUp();
    }


    @SmallTest
    public void test() throws UiObjectNotFoundException {
        getActivity();
        UiDevice device =UiDevice.getInstance(getInstrumentation());

        // Is test in MapsActivity?
        assertTrue(solo.waitForView(solo.getView(R.id.map)));

        // Perform zoom-in on map
        UiObject map = device.findObject(new UiSelector().resourceId("com.example.jameswinters.unlock_android:id/map"));
        map.pinchOut(20,10);

        // Identify sPOI and click it
        UiObject marker = device.findObject(new UiSelector().descriptionContains("JB Morrell Library"));
        marker.click();

        // Assert that current activity is now sPOIPresentationActivity
        solo.assertCurrentActivity("Wrong Activity",sPOIPresentationActivity.class);
    }
}
