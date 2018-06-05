package com.example.jameswinters.unlock_android;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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


/**
 * Created by McKeown on 08/03/2018.
 */
public class UT1_0 extends ActivityInstrumentationTestCase2<MainActivity>{


    public UT1_0() {
        super(MainActivity.class);
    }
    private Solo solo;


    @Override
    protected void setUp() throws Exception {
        solo = new Solo(getInstrumentation());
        super.setUp();

    }


    @SmallTest
    public void test() {
        getActivity();
        solo.clickOnView(solo.getView(R.id.map_imagebutton));
        solo.assertCurrentActivity("Wrong Activity",MapsActivity.class);
        assertTrue(solo.waitForView(R.id.map));
    }
}