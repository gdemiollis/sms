package com.and1droid.smsquebec;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainActivityTest() {
        super(MainActivity.class);
    }

    private static final String SIGNATURE = "SIGNATURE";
    private MainActivity mainActivity;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mainActivity = getActivity();
    }

    public void testComputeCharCount() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext());
        int charMax = getInstrumentation().getTargetContext().getResources().getInteger(R.integer.maxchar);
        defaultSharedPreferences.edit().putBoolean(SettingsActivity.CHECK_SIGNATURE_KEY, false);
        assertEquals(260, mainActivity.computeCharCount(charMax));
        defaultSharedPreferences.edit().putBoolean(SettingsActivity.CHECK_SIGNATURE_KEY, true).commit();
        defaultSharedPreferences.edit().putString(SettingsActivity.SIGNATURE_KEY, SIGNATURE).commit();
        assertEquals(250, mainActivity.computeCharCount(charMax));

    }

}
