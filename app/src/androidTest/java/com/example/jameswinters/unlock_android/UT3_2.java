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

// UT 3.2

public class UT3_2 extends ActivityInstrumentationTestCase2<QRActivity>{
    //Start in QRActivity
    public UT3_2() {
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
        // Start in QR Activity
        getActivity();

        // Wait for QR scan
        await().until(newQRscan(getActivity()));

        // Assert current activity is POIPresentationActivity
        solo.assertCurrentActivity("Wrong activity", POIPresentationActivity.class);

        // Check for TextView containing all text
        assertTrue(solo.waitForView(solo.getView(R.id.TEXT_STATUS_ID)));

        // Go back
        solo.goBack();

        // Press POI Icon on map
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("University of York"));
        try {
            marker.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

        // Assert current activity is POIPresentationActivity
        solo.assertCurrentActivity("Wrong activity", POIPresentationActivity.class);

        // Check for TextView containing all text
        assertTrue(solo.waitForView(solo.getView(R.id.TEXT_STATUS_ID)));
    }
}