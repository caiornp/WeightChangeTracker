package com.example.weightchangetracker.ui.dashboard;

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
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.weightchangetracker.R;
import com.example.weightchangetracker.util.DateConverters;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.rgb;


public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private View mRoot;
    private int mPrimaryColor;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        mRoot = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Get text primary color to make legend
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getActivity().getTheme();
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        TypedArray arr =
                getActivity().obtainStyledAttributes(typedValue.data, new int[]{
                        android.R.attr.textColorPrimary});
        mPrimaryColor = arr.getColor(0, -1);

        arr.recycle();

        dashboardViewModel.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                updateGraph();
            }
        });

        return mRoot;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void drawGraph(List<ILineDataSet> dataSets) {
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

        // TODO: Set minimum X for the minimum date of the series
        // TODO: Pan the graph to start of the diet
        //x.setAxisMinimum(DateConverters.dateToFloat(startDate));
        //x.setAxisMaximum(DateConverters.dateToFloat(endDate));

        x.setGranularity(1f); // minimum axis-step (interval) is 1
        x.setLabelCount(8);
        x.setValueFormatter(formatter);
        x.setTextColor(mPrimaryColor);

        YAxis y = chart.getAxisLeft();
        y.setAxisMinimum(dashboardViewModel.getStartWeight()-7f);
        y.setAxisMaximum(dashboardViewModel.getStartWeight()+2f);
        y.setLabelCount(12);
        y.setTextColor(mPrimaryColor);

        YAxis y2 = chart.getAxisRight();
        y2.setEnabled(false);

        //chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        chart.getAxisRight().setEnabled(false);

        Description description = new Description();
        description.setText("Weight change and tendency");

        chart.setDescription(description);

        chart.getLegend().setTextColor(mPrimaryColor);

        chart.resetZoom();
        chart.zoom(1.2f, 1f, 0, 0);
        chart.moveViewToX(dashboardViewModel.getFloatDietStartDate());

        chart.invalidate(); // refresh
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void updateGraph() {
        // Create and draw mainline
        LineDataSet mMainDataSet = new LineDataSet(dashboardViewModel.getWeightList(), "Real Weight"); // add entries to dataset
        mMainDataSet.setColor(rgb(0, 0, 255));
        mMainDataSet.setValueTextColor(rgb(0, 0, 255)); // styling, ...
        mMainDataSet.setCircleColor(rgb(0, 0, 255));
        mMainDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        mMainDataSet.setLineWidth(4f);
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(mMainDataSet);

        if (dashboardViewModel.allDietPreferencesSet()) {
             // TODO: only display lines if its on
             // Create and draw max line if it is on
            if(true) {
                LineDataSet maxWeightDataSet = new LineDataSet(dashboardViewModel.getMaxRateLine(), "Max change"); // add entries to dataset
                maxWeightDataSet.setColor(rgb(0, 255, 0));
                maxWeightDataSet.setValueTextColor(rgb(0, 255, 0)); // styling, ...
                maxWeightDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                maxWeightDataSet.setDrawCircles(false);
                maxWeightDataSet.setLineWidth(2f);
                maxWeightDataSet.setDrawValues(false);
                dataSets.add(maxWeightDataSet);
            }

            //---
            // Create and draw min line if it is on
            if(true) {
                LineDataSet minWeightDataSet = new LineDataSet(dashboardViewModel.getMinRateLine(), "Min change"); // add entries to dataset
                minWeightDataSet.setColor(rgb(255, 0, 0));
                minWeightDataSet.setValueTextColor(rgb(255, 0, 0)); // styling, ...
                minWeightDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                minWeightDataSet.setDrawCircles(false);
                minWeightDataSet.setLineWidth(2f);
                minWeightDataSet.setDrawValues(false);
                dataSets.add(minWeightDataSet);
            }

            //----
            // Create and draw tendency line if it is on
            if(true) {
                LineDataSet tendencyDataSet = new LineDataSet(dashboardViewModel.getTendencyLine(dashboardViewModel.getWeightList(), 7), "Tendency"); // add entries to dataset
                tendencyDataSet.setColor(rgb(255, 195, 20));
                tendencyDataSet.setValueTextColor(rgb(255, 195, 20)); // styling, ...
                tendencyDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                tendencyDataSet.setDrawCircles(false);
                tendencyDataSet.setLineWidth(2f);
                tendencyDataSet.setDrawValues(false);
                dataSets.add(tendencyDataSet);
            }
        }

        drawGraph(dataSets);
    }
}