package com.example.weightchangetracker.ui.settings;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weightchangetracker.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    // PreferenceFragment class
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RecyclerView rv = getListView(); // This holds the PreferenceScreen's items
        rv.setPadding(0, 0, 0, 250); // (left, top, right, bottom)
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey);

        /**
         * TODO:
         * - default values for the diet plan
         * - show additional fields if lines are on
         * - validate value range for fields
         */

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