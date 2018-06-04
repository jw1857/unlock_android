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
import android.widget.EditText;
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

import pl.droidsonroids.gif.GifImageView;


/**
 * Created by McKeown on 08/03/2018.
 */
public class MUT2_0 extends ActivityInstrumentationTestCase2<createAccount>{


    public MUT2_0() {
        super(createAccount.class);
    }
    private Solo solo;
    /*@Rule
    public ActivityTestRule<ScanSuccess> activityTestRule =
            new ActivityTestRule<>(ScanSuccess.class);
*/
    @Override
    protected void setUp() throws Exception {
        solo = new Solo(getInstrumentation());
        super.setUp();

    }

  /*  private Callable<Boolean> newQRscan(Activity thisActivity) {
        final TextView v = thisActivity.findViewById(R.id.txtResult);
        return new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return !(v.getText().toString().equals("Please focus camera to QR Code")); // The condition that must be fulfilled
            }
        };
    }*/




    @SmallTest
    public void test() {
        EditText et1 = getActivity().findViewById(R.id.newEmailContainer);
        EditText et2 = getActivity().findViewById(R.id.newUsernameContainer);
        EditText et3 = getActivity().findViewById(R.id.newPasswordContainer);
        solo.enterText(et1,"jrm90@btconnect.com");
        solo.enterText(et2,"jrm90");
        solo.enterText(et3,"1test1");
        solo.clickOnView(solo.getView(R.id.createAccount));
        assertTrue(solo.waitForText("Signed in as: jrm90"));
    }
}