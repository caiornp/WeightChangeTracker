package com.example.weightchangetracker.ui.settings;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.weightchangetracker.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey);

        EditTextPreference startDatePreference = findPreference("calendar_diet_start_date");

        if (startDatePreference != null) {
            startDatePreference.setOnBindEditTextListener(
                    new EditTextPreference.OnBindEditTextListener() {
                        @Override
                        public void onBindEditText(@NonNull EditText editText) {
                            editText.setInputType(InputType.TYPE_CLASS_DATETIME);
                        }
                    });
        }

        EditTextPreference endDatePreference = findPreference("calendar_diet_end_date");

        if (endDatePreference != null) {
            endDatePreference.setOnBindEditTextListener(
                    new EditTextPreference.OnBindEditTextListener() {
                        @Override
                        public void onBindEditText(@NonNull EditText editText) {
                            editText.setInputType(InputType.TYPE_CLASS_DATETIME);
                        }
                    });
        }
    }
}