package com.example.weightchangetracker.ui.newweight;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weightchangetracker.R;

import java.util.Calendar;

public class NewWeightActivity extends AppCompatActivity {

    // Unique tag for the intent reply.
    public static final String WEIGHT_REPLY = "com.example.weightchangetracker.REPLY_WEIGHT";
    public static final String DATE_REPLY = "com.example.weightchangetracker.REPLY_DATE";

    private CalendarView mDateView;
    private EditText mEditWeightView;

    private long selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_weight);

        mDateView = findViewById(R.id.calendar_date);
        mEditWeightView = findViewById(R.id.edit_weight);

        final Button button = findViewById(R.id.button_save);

        Calendar cal = Calendar. getInstance();
        selectedDate = cal.getTimeInMillis();

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

        mDateView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // display the selected date by using a toast
                Calendar cal = Calendar. getInstance();
                cal.set(year, month, dayOfMonth);
                selectedDate = cal.getTimeInMillis();
            }
        });
    }
}