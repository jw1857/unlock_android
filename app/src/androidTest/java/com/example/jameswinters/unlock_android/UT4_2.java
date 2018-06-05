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

public class UT4_2 extends ActivityInstrumentationTestCase2<QRActivity>{

    public UT4_2() {
        super(QRActivity.class);
    }

    private Solo solo;

    @Override
    protected void setUp() throws Exception{
        solo = new Solo(getInstrumentation());
        super.setUp();
    }

    private Callable<Boolean> newQRscan(Activity thisActivity) {
        final TextView v = thisActivity.findViewById(R.id.txtResult);
        return new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return !(v.getText().toString().equals("Please focus camera to QR Code")); // The condition that must be fulfilled
            }
        };
    }

    @SmallTest
    public void test(){
        getActivity();
        await().until(newQRscan(getActivity()));
        solo.assertCurrentActivity("Wrong activity", sPOIPresentationActivity.class);
        //assertTrue(solo.waitForView(solo.getView(R.id.spoi_TEXT_STATUS_ID)));
        solo.clickOnView(solo.getView(R.id.spoipresentation_videoimagebutton));
        assertTrue(solo.waitForView(solo.getView(R.id.video_view)));
        solo.goBack();
        solo.goBack();
        solo.assertCurrentActivity("Wrong activity", MapsActivity.class);
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Heslington Hall"));
        try {
            marker.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        solo.assertCurrentActivity("Wrong activity", sPOIPresentationActivity.class);
        //assertTrue(solo.waitForView(solo.getView(R.id.spoi_TEXT_STATUS_ID)));
        solo.clickOnView(solo.getView(R.id.spoipresentation_videoimagebutton));
        assertTrue(solo.waitForView(solo.getView(R.id.video_view)));
    }
}