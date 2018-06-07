package com.example.jameswinters.unlock_android;

import android.support.test.filters.SmallTest;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;





public class MIT4_0 extends ActivityInstrumentationTestCase2<MapsActivity>{


    public MIT4_0() {
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
        getActivity();
        solo.clickOnView(solo.getView(R.id.progressBar));
        solo.assertCurrentActivity("Wrong Activity",ProgressTableActivity.class);
        solo.goBack();
        solo.goBack();
        solo.clickOnView(solo.getView(R.id.leader_imagebutton));
        solo.assertCurrentActivity("Wrong Activity",LeaderboardsActivity.class);
    }
}