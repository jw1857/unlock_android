package com.example.jameswinters.unlock_android;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.test.ActivityInstrumentationTestCase2;
import org.awaitility.Awaitility.*;
import org.junit.Rule;

import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import android.test.InstrumentationTestCase;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.concurrent.Callable;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.with;
import static org.junit.Assert.*;
import com.robotium.solo.Solo;

import pl.droidsonroids.gif.GifImageView;


//check that unlocking a hPOI takes you to content screen and that all content can be accessed
public class MIT12_0 extends ActivityInstrumentationTestCase2<QRActivity>{


    public MIT12_0() {
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
        solo.assertCurrentActivity("Wrong activity",hPOIPresentationActivity.class);
        assertTrue(solo.waitForText("The Quiet Place is set aside as an area of stillness and quiet"));
        solo.clickOnView(solo.getView(R.id.hpoipresentation_audio));
        solo.assertCurrentActivity("Wrong Activity",AudioActivity.class);
        solo.goBack();
        solo.clickOnView(solo.getView(R.id.hpoipresentation_photobutton));
        solo.goBack();
        solo.clickOnView(solo.getView(R.id.hpoipresentation_videoimagebutton));
        solo.assertCurrentActivity("Wrong Activity",VideoActivity.class);

    }
}