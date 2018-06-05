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

public class MIT14_0 extends ActivityInstrumentationTestCase2<MapsActivity>{

    public MIT14_0() {
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
        getActivity();
        //solo.assertCurrentActivity("Wrong activity", MapsActivity.class);
        //assertTrue(solo.waitForView(solo.getView(R.id.spoi_TEXT_STATUS_ID)));

        // Is text box present? Does text contain correct content?
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("University of York"));
        try {
            marker.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        solo.assertCurrentActivity("Wrong activity", POIPresentationActivity.class);

        assertTrue(solo.waitForView(solo.getView(R.id.TEXT_STATUS_ID)));
        assertTrue(solo.waitForText("OPENING HOURS: 09:00 to 17:00"));



        solo.clickOnView(solo.getView(R.id.poipresentation_photobutton));
        assertTrue(solo.waitForView(solo.getView(R.id.view_pager)));
        solo.goBack();

        solo.assertCurrentActivity("Wrong activity", POIPresentationActivity.class);
        solo.clickOnView(solo.getView(R.id.poipresentation_videoimagebutton));
        assertTrue(solo.waitForView(solo.getView(R.id.video_view)));

    }
}