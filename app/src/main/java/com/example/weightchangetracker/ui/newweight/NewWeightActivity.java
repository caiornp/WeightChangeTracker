package com.example.weightchangetracker.ui.newweight;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weightchangetracker.R;
import com.example.weightchangetracker.util.DateConverters;

import java.time.OffsetDateTime;

public class NewWeightActivity extends AppCompatActivity {

    // Unique tag for the intent reply.
    public static final String WEIGHT_REPLY = "com.example.weightchangetracker.REPLY_WEIGHT";
    public static final String DATE_REPLY = "com.example.weightchangetracker.REPLY_DATE";

    private EditText mEditWeightView;

    private String selectedDate;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_weight);

        CalendarView mDateView = findViewById(R.id.calendar_date);
        mEditWeightView = findViewById(R.id.edit_weight);

        final Button button = findViewById(R.id.button_save);

        selectedDate = DateConverters.dateToTimestamp(OffsetDateTime.now());

        button.setOnClickListener(view -> {
            Intent replyIntent = new Intent();
            if (TextUtils.isEmpty(mEditWeightView.getText())) {
                setResult(RESULT_CANCELED, replyIntent);
            } else {
                Float weightValue = Float.parseFloat(mEditWeightView.getText().toString());

                replyIntent.putExtra(DATE_REPLY, selectedDate);
                replyIntent.putExtra(WEIGHT_REPLY, weightValue);
                setResult(RESULT_OK, replyIntent);
            }
            finish();
        });

        mDateView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // display the selected date by using a toast
            selectedDate = DateConverters.dateToTimestamp(DateConverters.fromDayMonthYear(year, (month+1), dayOfMonth));
        });
    }
}