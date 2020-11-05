package com.example.weightchangetracker.ui.settings;

import android.os.Bundle;
import android.text.InputType;

import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.weightchangetracker.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey);

        EditTextPreference preference = findPreference("calendar_diet_start_year");
        if (preference != null) {
            preference.setOnBindEditTextListener(
                    editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER));
        }

        preference = findPreference("calendar_diet_start_month");
        if (preference != null) {
            preference.setOnBindEditTextListener(
                    editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER));
        }

        preference = findPreference("calendar_diet_start_day");
        if (preference != null) {
            preference.setOnBindEditTextListener(
                    editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER));
        }

        preference = findPreference("calendar_diet_end_year");
        if (preference != null) {
            preference.setOnBindEditTextListener(
                    editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER));
        }

        preference = findPreference("calendar_diet_end_month");
        if (preference != null) {
            preference.setOnBindEditTextListener(
                    editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER));
        }

        preference = findPreference("calendar_diet_end_day");
        if (preference != null) {
            preference.setOnBindEditTextListener(
                    editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER));
        }

        preference = findPreference("min_change_rate");
        if (preference != null) {
            preference.setOnBindEditTextListener(
                    editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER
                            | InputType.TYPE_NUMBER_FLAG_DECIMAL));
        }

        preference = findPreference("max_change_rate");
        if (preference != null) {
            preference.setOnBindEditTextListener(
                    editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER
                            | InputType.TYPE_NUMBER_FLAG_DECIMAL));
        }

    }
}