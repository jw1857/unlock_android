package com.example.jameswinters.unlock_android;
import android.app.Activity;
import android.support.test.filters.SmallTest;

import android.test.ActivityInstrumentationTestCase2;

import android.widget.TextView;
import java.util.concurrent.Callable;

import com.robotium.solo.Solo;

public class UT3_4 extends ActivityInstrumentationTestCase2<VideoActivity>{

    public UT3_4() {
        super(VideoActivity.class);
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
        solo.clickOnView(solo.getView(R.id.poipresentation_videoimagebutton));
        assertTrue(solo.waitForView(solo.getView(R.id.video_view)));
    }
}