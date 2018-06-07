package com.example.jameswinters.unlock_android;
import android.app.Activity;
import android.support.test.filters.SmallTest;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.test.ActivityInstrumentationTestCase2;

import android.widget.TextView;
import java.util.concurrent.Callable;

import static org.awaitility.Awaitility.await;

import com.robotium.solo.Solo;

public class UT1_2 extends ActivityInstrumentationTestCase2<MapsActivity>{

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
        assertTrue(solo.waitForView(solo.getView(R.id.map)));
        UiObject map = device.findObject(new UiSelector().resourceId("com.example.jameswinters.unlock_android:id/map"));
        map.pinchOut(20,10);
        UiObject marker = device.findObject(new UiSelector().descriptionContains("JB Morrell Library"));
        marker.click();
        solo.assertCurrentActivity("Wrong Activity",sPOIPresentationActivity.class);
    }
}



/*

        getActivity();
        */