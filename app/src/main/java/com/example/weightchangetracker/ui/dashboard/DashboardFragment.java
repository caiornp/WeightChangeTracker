package com.example.weightchangetracker.ui.dashboard;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
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
    private int mPrimaryColor;

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

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getActivity().getTheme();
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        TypedArray arr =
                getActivity().obtainStyledAttributes(typedValue.data, new int[]{
                        android.R.attr.textColorPrimary});
        mPrimaryColor = arr.getColor(0, -1);

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
        x.setLabelCount(8);
        x.setValueFormatter(formatter);
        x.setTextColor(mPrimaryColor);

        YAxis y = chart.getAxisLeft();
        y.setAxisMinimum(mStartWeight-7f);
        y.setAxisMaximum(mStartWeight+2f);
        y.setLabelCount(12);
        y.setTextColor(mPrimaryColor);

        YAxis y2 = chart.getAxisRight();
        y2.setEnabled(false);

        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        chart.getAxisRight().setEnabled(false);

        Description description = new Description();
        description.setText("Weight change and tendency");

        chart.setDescription(description);

        chart.getLegend().setTextColor(mPrimaryColor);

        chart.resetZoom();
        //chart.zoom(0.9f, 1, 0, mStartWeight);

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
    private ArrayList<Entry> createTendency(List<Entry> data, int days) {
        ArrayList<Entry> tendency = new ArrayList<>();
        for(int i=0; i<data.size(); i++) {
            float avg = 0;
            float c = 0;
            for(int j=i;(j>=0) && (j>=(i-days)); j--) {
                avg = avg + data.get(j).getY();
                c = c + 1f;
            }
            avg = avg / c;
            Entry entry = new Entry(data.get(i).getX(), avg);
            tendency.add(entry);
        }
        return tendency;
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
                    LineDataSet maxWeightDataSet = new LineDataSet(maxRateList, "Max change"); // add entries to dataset
                    maxWeightDataSet.setColor(rgb(0, 255, 0));
                    maxWeightDataSet.setValueTextColor(rgb(0, 255, 0)); // styling, ...
                    maxWeightDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                    maxWeightDataSet.setDrawCircles(false);
                    maxWeightDataSet.setLineWidth(2f);
                    maxWeightDataSet.setDrawValues(false);

                    //---

                    List<Entry> minRateList = createLine(startDate, endDate, mStartWeight, minWeekRate);
                    LineDataSet minWeightDataSet = new LineDataSet(minRateList, "Min change"); // add entries to dataset
                    minWeightDataSet.setColor(rgb(255, 0, 0));
                    minWeightDataSet.setValueTextColor(rgb(255, 0, 0)); // styling, ...
                    minWeightDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                    minWeightDataSet.setDrawCircles(false);
                    minWeightDataSet.setLineWidth(2f);
                    minWeightDataSet.setDrawValues(false);

                    //----

                    List<Entry> tendencyList = createTendency(dashboardViewModel.getWeightList(), 7);
                    LineDataSet tendencyDataSet = new LineDataSet(tendencyList, "Tendency"); // add entries to dataset
                    tendencyDataSet.setColor(rgb(253, 106, 2));
                    tendencyDataSet.setValueTextColor(rgb(253, 106, 2)); // styling, ...
                    tendencyDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                    tendencyDataSet.setDrawCircles(false);
                    tendencyDataSet.setLineWidth(2f);
                    tendencyDataSet.setDrawValues(false);

                    // use the interface ILineDataSet
                    List<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(mMainDataSet);
                    dataSets.add(maxWeightDataSet);
                    dataSets.add(minWeightDataSet);
                    dataSets.add(tendencyDataSet);

                    drawGraph(dataSets, startDate, endDate);
                });
            }
        }

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(mMainDataSet);
        drawGraph(dataSets, OffsetDateTime.now(), OffsetDateTime.now().plusMonths(1));
    }
}