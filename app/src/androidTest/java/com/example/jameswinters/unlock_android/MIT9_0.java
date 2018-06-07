package com.example.jameswinters.unlock_android;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.test.ActivityInstrumentationTestCase2;
import org.awaitility.Awaitility.*;
import org.junit.Rule;

import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static org.hamcrest.Matchers.is;
import com.robotium.solo.Solo;

import pl.droidsonroids.gif.GifImageView;


/**
 * Created by McKeown on 08/03/2018.
 */
public class MIT9_0 extends ActivityInstrumentationTestCase2<MapsActivity>{


    public MIT9_0() {
        super(MapsActivity.class);
    }
    private Solo solo;


    @Override
    protected void setUp() throws Exception {
        solo = new Solo(getInstrumentation());
        super.setUp();

    }





    @SmallTest
    public void test() {
        //set zoom to closer
        getActivity();
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("University of York"));
        try {
            marker.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        solo.assertCurrentActivity("Wrong Activity",POIPresentationActivity.class);
        assertTrue(solo.waitForText("University of York"));
        solo.goBack();
        UiObject marker2 = device.findObject(new UiSelector().descriptionContains("JB Morrell Library"));
        try{
            marker2.click();
        } catch (UiObjectNotFoundException e){
            e.printStackTrace();
        }

        solo.assertCurrentActivity("Wrong Activity",sPOIPresentationActivity.class);
        assertTrue(solo.waitForText("JB Morrell Library"));

    }
}