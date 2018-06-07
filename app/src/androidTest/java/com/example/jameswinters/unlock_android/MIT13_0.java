package com.example.jameswinters.unlock_android;
import android.app.Activity;
import android.support.test.filters.SmallTest;
import android.test.ActivityInstrumentationTestCase2;


import android.widget.TextView;
import java.util.concurrent.Callable;

import static org.awaitility.Awaitility.await;

import com.robotium.solo.Solo;

public class MIT13_0 extends ActivityInstrumentationTestCase2<QRActivity>{

    public MIT13_0() {
        super(QRActivity.class);
    }

    private Solo solo;

    @Override
    protected void setUp() throws Exception{
        solo = new Solo(getInstrumentation());
        super.setUp();
    }



    @SmallTest
    public void test(){

        // In QRActivity
        getActivity();

        // Scan hPOI
        await().until(newQRscan(getActivity()));


        // Assert current activity is hPOIPresentationActivity
        solo.assertCurrentActivity("Wrong activity", hPOIPresentationActivity.class);

        // Is textView present? Does text contain correct content?
        assertTrue(solo.waitForView(solo.getView(R.id.TEXT_STATUS_ID)));
        assertTrue(solo.waitForText("The Quiet Place is set aside as an area of stillness and quiet, which can be used for the purpose of reflection, meditation, prayer, reading and contemplative walking."));

        // Press the photo button
        solo.clickOnView(solo.getView(R.id.hpoipresentation_photobutton));

        // Assert that ViewPager is shown
        assertTrue(solo.waitForView(solo.getView(R.id.view_pager)));

        // Press back (to go to hPOIPresentationActivity)
        solo.goBack();

        // Assert current activity is hPOIPresentationActivity
        solo.assertCurrentActivity("Wrong activity", hPOIPresentationActivity.class);

        // Click on video icon
        solo.clickOnView(solo.getView(R.id.hpoipresentation_videoimagebutton));

        // Assert that VideoView is shown
        assertTrue(solo.waitForView(solo.getView(R.id.video_view)));

    }

    private Callable<Boolean> newQRscan(Activity thisActivity) {
        final TextView v = thisActivity.findViewById(R.id.txtResult);
        return new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return !(v.getText().toString().equals("Please focus camera to QR Code")); // The condition that must be fulfilled
            }
        };
    }
}
