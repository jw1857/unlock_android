package com.example.jameswinters.unlock_android;
import android.app.Activity;
import android.support.test.filters.SmallTest;
import android.test.ActivityInstrumentationTestCase2;


import android.widget.TextView;
import java.util.concurrent.Callable;

import static org.awaitility.Awaitility.await;

import com.robotium.solo.Solo;

public class MIT10_0 extends ActivityInstrumentationTestCase2<QRActivity>{

    public MIT10_0() {
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

        // In QRActivity
        getActivity();

        // Scan POI
        await().until(newQRscan(getActivity()));

        // Assert current activity is POIPresentationActivity
        solo.assertCurrentActivity("Wrong activity", POIPresentationActivity.class);

        // Is textView present? Does text contain correct content?
        assertTrue(solo.waitForView(solo.getView(R.id.TEXT_STATUS_ID)));
        assertTrue(solo.waitForText("OPENING HOURS: 09:00 to 17:00"));

        // Press the photo button
        solo.clickOnView(solo.getView(R.id.poipresentation_photobutton));

        // Assert that ViewPager is shown
        assertTrue(solo.waitForView(solo.getView(R.id.view_pager)));

        // Press back (to go to POIPresentationActivity)
        solo.goBack();

        // Assert current activity is POIPresentationActivity
        solo.assertCurrentActivity("Wrong activity", POIPresentationActivity.class);

        // Click on video icon
        solo.clickOnView(solo.getView(R.id.poipresentation_videoimagebutton));

        // Assert that VideoView is shown
        assertTrue(solo.waitForView(solo.getView(R.id.video_view)));

    }
}