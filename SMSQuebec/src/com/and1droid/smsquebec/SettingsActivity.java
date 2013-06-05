package com.and1droid.smsquebec;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    public static final String CHECK_SIGNATURE_KEY = "check_signature_key";
    public static final String SIGNATURE_KEY = "signature_key";

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        updateSummary();
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (SIGNATURE_KEY.equals(key)) {
            updateSummary();
        }
    }
    
    private void updateSummary() {
        getPreferenceManager().findPreference(SIGNATURE_KEY).setSummary(
                PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsActivity.SIGNATURE_KEY, ""));
    }
}
