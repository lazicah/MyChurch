package com.disciplesbay.latterhousehq.mychurch.fragment;

import android.os.Bundle;
import android.support.v7.preference.Preference;


import com.disciplesbay.latterhousehq.mychurch.BuildConfig;
import com.disciplesbay.latterhousehq.mychurch.R;


public class MainSettingsFragment extends BasePreferenceFragment {
    public static final boolean DEBUG = !BuildConfig.BUILD_TYPE.equals("release");

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.download_settings);

        if (!DEBUG) {
            final Preference debug = findPreference(getString(R.string.debug_pref_screen_key));
            getPreferenceScreen().removePreference(debug);
        }
    }
}
