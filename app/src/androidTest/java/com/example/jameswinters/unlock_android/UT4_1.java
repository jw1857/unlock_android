package com.example.jameswinters.unlock_android;

import android.support.test.filters.SmallTest;

import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

// UT 4.1

public class UT4_1 extends ActivityInstrumentationTestCase2<MapsActivity>{

    public UT4_1() {
        super(MapsActivity.class);
    }

    private Solo solo;

    @Override
    protected void setUp() throws Exception{
        solo = new Solo(getInstrumentation());
        super.setUp();
    }



    @SmallTest
    public void test(){
        // In MapsActivity
        getActivity();

        // Click on POI
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("University of York"));
        try {
            marker.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

        // Assert current activity is POIPresentationActivity
        solo.assertCurrentActivity("Wrong activity", POIPresentationActivity.class);

        // Check for opening hours (at top of textView)
        assertTrue(solo.waitForText("OPENING HOURS: 09:00 to 17:00"));

        // Check for text at bottom of textView (requires scrolling to be found)
        assertTrue(solo.waitForText("Test text for UT4.1"));
    }
}
