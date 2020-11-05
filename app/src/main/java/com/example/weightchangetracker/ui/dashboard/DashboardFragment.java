package com.example.weightchangetracker.ui.dashboard;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.example.weightchangetracker.R;
import com.example.weightchangetracker.models.WeightRegistry;
import com.example.weightchangetracker.util.DateConverters;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.rgb;
import static java.time.temporal.ChronoUnit.DAYS;


public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private View mRoot;
    private LineDataSet mMainDataSet;
    private float mStartWeight;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        mRoot = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mStartWeight = 90f;

        dashboardViewModel.getAllWeights().observe(getViewLifecycleOwner(), weights -> {
            dashboardViewModel.clearWeights();
            for(WeightRegistry w : weights) {
                Entry entry = new Entry(DateConverters.dateToFloat(w.getDate()), w.getWeight());
                dashboardViewModel.addWeight(entry);
            }

            updateGraph();
        });

        updateGraph();

        return mRoot;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void drawGraph(List<ILineDataSet> dataSets, OffsetDateTime startDate, OffsetDateTime endDate) {
        LineData lineData = new LineData(dataSets);

        // in this example, a LineChart is initialized from xml
        LineChart chart = mRoot.findViewById(R.id.chart);
        chart.setData(lineData);

        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return DateConverters.floatToStringShort(value);
            }
        };

        XAxis x = chart.getXAxis();

        x.setAxisMinimum(DateConverters.dateToFloat(startDate));
        x.setAxisMaximum(DateConverters.dateToFloat(endDate));

        x.setGranularity(1f); // minimum axis-step (interval) is 1
        x.setLabelCount(7);
        x.setValueFormatter(formatter);

        YAxis y = chart.getAxisLeft();
        y.setAxisMinimum(mStartWeight-10f);
        y.setAxisMaximum(mStartWeight+10f);
        y.setLabelCount(12);

        YAxis y2 = chart.getAxisRight();
        y2.setEnabled(false);

        chart.setTouchEnabled(true);

        Description description = new Description();
        description.setText("Weight change and tendency");

        chart.setDescription(description);

        chart.zoom(2.0f, 1, 0, mStartWeight);

        chart.invalidate(); // refresh
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<Entry> createLine(OffsetDateTime startDate, OffsetDateTime endDate, float startWeight, float weekRate) {
        float changeRate = 1 - ((weekRate / 100) / 7);
        ArrayList<Entry> rateList = new ArrayList<>();

        long totalDays = DAYS.between(startDate, endDate);

        float currWeight = startWeight;
        OffsetDateTime currDate = startDate;

        for(long i = 0; i <totalDays; ++i) {
            Entry entry = new Entry(DateConverters.dateToFloat(currDate), currWeight);
            rateList.add(entry);
            currDate = currDate.plusDays(1);
            currWeight = currWeight * changeRate;
        }

        return rateList;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void updateGraph() {
        mMainDataSet = new LineDataSet(dashboardViewModel.getWeightList(), "Real Weight"); // add entries to dataset
        mMainDataSet.setColor(rgb(0, 0, 255));
        mMainDataSet.setValueTextColor(rgb(0, 0, 255)); // styling, ...
        mMainDataSet.setCircleColor(rgb(0, 0, 255));
        mMainDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        mMainDataSet.setLineWidth(4f);

        // ----------------

        if(this.getContext() != null) {
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(this.getContext());

            int calendar_diet_end_year =
                    Integer.parseInt(sharedPreferences.getString("calendar_diet_end_year", "0"));
            int calendar_diet_end_month =
                    Integer.parseInt(sharedPreferences.getString("calendar_diet_end_month", "0"));
            int calendar_diet_end_day =
                    Integer.parseInt(sharedPreferences.getString("calendar_diet_end_day", "0"));

            int calendar_diet_start_year =
                    Integer.parseInt(sharedPreferences.getString("calendar_diet_start_year", "0"));
            int calendar_diet_start_month =
                    Integer.parseInt(sharedPreferences.getString("calendar_diet_start_month", "0"));
            int calendar_diet_start_day =
                    Integer.parseInt(sharedPreferences.getString("calendar_diet_start_day", "0"));

            float maxWeekRate = Float.parseFloat(sharedPreferences.getString("max_change_rate", "0"));
            float minWeekRate = Float.parseFloat(sharedPreferences.getString("min_change_rate", "0"));

            if ((calendar_diet_end_year > 0) && (calendar_diet_end_month > 0) && (calendar_diet_end_day > 0)
                    && (calendar_diet_start_year > 0) && (calendar_diet_start_month > 0) && (calendar_diet_start_day > 0)
                    && (maxWeekRate > 0) && (minWeekRate > 0)) {
                // Set end date
                OffsetDateTime endDate = DateConverters.fromDayMonthYear(calendar_diet_end_year,
                        calendar_diet_end_month,
                        calendar_diet_end_day);

                OffsetDateTime startDate = DateConverters.fromDayMonthYear(calendar_diet_start_year,
                        calendar_diet_start_month,
                        calendar_diet_start_day);

                dashboardViewModel.findByDate(startDate).observe(getViewLifecycleOwner(), startWeights -> {
                    if (!startWeights.isEmpty()) {
                        mStartWeight = startWeights.get(0).getWeight();
                    }

                    List<Entry> maxRateList = createLine(startDate, endDate, mStartWeight, maxWeekRate);
                    LineDataSet maxWeightDataSet = new LineDataSet(maxRateList, "Max Weight"); // add entries to dataset
                    maxWeightDataSet.setColor(rgb(0, 255, 0));
                    maxWeightDataSet.setValueTextColor(rgb(0, 255, 0)); // styling, ...
                    maxWeightDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                    maxWeightDataSet.setDrawCircles(false);
                    maxWeightDataSet.setLineWidth(2f);

                    //---

                    List<Entry> minRateList = createLine(startDate, endDate, mStartWeight, minWeekRate);
                    LineDataSet minWeightDataSet = new LineDataSet(minRateList, "Min Weight"); // add entries to dataset
                    minWeightDataSet.setColor(rgb(255, 0, 0));
                    minWeightDataSet.setValueTextColor(rgb(255, 0, 0)); // styling, ...
                    minWeightDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                    minWeightDataSet.setDrawCircles(false);
                    minWeightDataSet.setLineWidth(2f);

                    /*
                     * TODO:
                     *   - Create a average line
                     * */

                    // use the interface ILineDataSet
                    List<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(mMainDataSet);
                    dataSets.add(maxWeightDataSet);
                    dataSets.add(minWeightDataSet);

                    drawGraph(dataSets, startDate, endDate);
                });
            }
        }

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(mMainDataSet);
        drawGraph(dataSets, OffsetDateTime.now(), OffsetDateTime.now().plusMonths(1));
    }
}