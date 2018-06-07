package com.example.jameswinters.unlock_android;

import android.app.Activity;
import android.support.test.filters.SmallTest;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;
import java.util.concurrent.Callable;
import static org.awaitility.Awaitility.await;

import com.robotium.solo.Solo;

public class UT3_3 extends ActivityInstrumentationTestCase2<QRActivity>{
    // Start in QRActivity
    public UT3_3() {
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

        // Press video button
        solo.clickOnView(solo.getView(R.id.poipresentation_videoimagebutton));

        // Check VideoView is shown
        assertTrue(solo.waitForView(solo.getView(R.id.video_view)));
    }
}