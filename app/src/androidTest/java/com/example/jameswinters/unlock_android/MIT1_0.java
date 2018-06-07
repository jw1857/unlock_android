package com.example.jameswinters.unlock_android;
import android.app.Activity;
import android.support.test.filters.SmallTest;

import android.test.ActivityInstrumentationTestCase2;

import android.widget.TextView;
import java.util.concurrent.Callable;
import static org.awaitility.Awaitility.await;

import com.robotium.solo.Solo;

//tests for animation playing on POI unlock
public class MIT1_0 extends ActivityInstrumentationTestCase2<QRActivity>{


    public MIT1_0() {
        super(QRActivity.class);
    }
    private Solo solo;
    @Override
    protected void setUp() throws Exception {
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
    public void test() {
        await().until(newQRscan(getActivity()));//wait for new qr scan
        solo.assertCurrentActivity("Wrong activity",POIPresentationActivity.class);
        assertTrue(solo.waitForText("testanimation"));
    }
}