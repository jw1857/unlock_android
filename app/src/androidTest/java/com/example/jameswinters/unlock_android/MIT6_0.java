package com.example.jameswinters.unlock_android;
import android.app.Activity;

import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Rule;

import android.widget.TextView;
import java.util.concurrent.Callable;

import static org.awaitility.Awaitility.await;


import com.robotium.solo.Solo;




public class MIT6_0 extends ActivityInstrumentationTestCase2<QRActivity>{


    public MIT6_0() {
        super(QRActivity.class);
    }
    private Solo solo;
    @Rule
    public ActivityTestRule<ScanSuccess> activityTestRule =
            new ActivityTestRule<>(ScanSuccess.class);

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
        await().until(newQRscan(getActivity()));
        solo.assertCurrentActivity("Wrong Activity",hPOIPresentationActivity.class);
        assertTrue(solo.waitForText("testanimation"));
    }
}