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
import static org.hamcrest.Matchers.not;

import android.test.InstrumentationTestCase;
import android.view.View;
import android.widget.TextView;
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

public class UT4_1 extends ActivityInstrumentationTestCase2<MapsActivity>{

    public UT4_1() {
        super(MapsActivity.class);
    }

    private Solo solo;

    @Override
    protected void setUp() throws Exception{
        solo = new Solo(getInstrumentation());
        super.setUp();
    }



    @SmallTest
    public void test(){
        getActivity();
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("University of York"));
        try {
            marker.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        solo.assertCurrentActivity("Wrong activity", POIPresentationActivity.class);
        assertTrue(solo.waitForText("OPENING HOURS: 09:00 to 17:00"));
        assertTrue(solo.waitForText("Test text for UT4.1"));
    }
}
