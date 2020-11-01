package com.example.weightchangetracker.ui.dashboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.example.weightchangetracker.R;
import com.example.weightchangetracker.models.WeightRegistry;
import com.example.weightchangetracker.ui.home.WeightListAdapter;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.graphics.Color.rgb;



public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private WeightListAdapter mWeighListAdapter;
    private View mRoot;

    private static final String TAG = "DashboardFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        mRoot = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // in this example, a LineChart is initialized from xml
        LineChart chart = mRoot.findViewById(R.id.chart);

        dashboardViewModel.getAllWeights().observe(getViewLifecycleOwner(), weights -> {
            dashboardViewModel.clearWeights();
            for(WeightRegistry w : weights) {
                Entry entry = new Entry(w.getDate().getTime(), w.getWeight());
                dashboardViewModel.addWeight(entry);
            }

            updateGraph();
        });

        updateGraph();

        return mRoot;
    }

    void updateGraph() {
        LineDataSet dataSet = new LineDataSet(dashboardViewModel.getWeightList(), "Real Weight"); // add entries to dataset
        dataSet.setColor(rgb(0, 0, 255));
        dataSet.setValueTextColor(rgb(0, 0, 255)); // styling, ...
        dataSet.setCircleColor(rgb(0, 0, 255));
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setLineWidth(4f);

        // ----------------

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this.getContext());

        Calendar cal = Calendar.getInstance();

        // Set end date
        cal.set(Integer.parseInt(sharedPreferences.getString("calendar_diet_end_year", "0")),
                Integer.parseInt(sharedPreferences.getString("calendar_diet_end_month", "0"))-1,
                Integer.parseInt(sharedPreferences.getString("calendar_diet_end_day", "0")));
        Date endDate = cal.getTime();

        // Set start date
        cal.set(Integer.parseInt(sharedPreferences.getString("calendar_diet_start_year", "0")),
                Integer.parseInt(sharedPreferences.getString("calendar_diet_start_month", "0"))-1,
                Integer.parseInt(sharedPreferences.getString("calendar_diet_start_day", "0")));
        Date startDate = cal.getTime();

        float maxWeekRate = Float.parseFloat(sharedPreferences.getString("max_change_rate", "0"));
        float minWeekRate = Float.parseFloat(sharedPreferences.getString("min_change_rate", "0"));

        float maxChangeRate = 1 - ((maxWeekRate / 100) / 7);
        float minChangeRate = 1 - ((minWeekRate / 100) / 7);
        float startWeight = 88.8f;

        List maxRateList = new ArrayList();
        List minRateList = new ArrayList();

        long totalDays = (endDate.getTime() - startDate.getTime()) / (1000*60*60*24);

        float cMinWeight = startWeight;
        float cMaxWeight = startWeight;
        for(long i = 0; i <totalDays; ++i) {
            Date d = cal.getTime();

            Entry maxE = new Entry(d.getTime(), cMaxWeight);
            maxRateList.add(maxE);

            Entry minE = new Entry(d.getTime(), cMinWeight);
            minRateList.add(minE);

            Log.v(TAG, "Max: " + cMaxWeight + " Min: " + cMinWeight);

            cal.add(Calendar.DAY_OF_MONTH, 1);
            cMaxWeight = cMaxWeight * maxChangeRate;
            cMinWeight = cMinWeight * minChangeRate;
        }


        LineDataSet maxWeightDataSet = new LineDataSet(maxRateList, "Max Weight"); // add entries to dataset
        maxWeightDataSet.setColor(rgb(0, 255, 0));
        maxWeightDataSet.setValueTextColor(rgb(0, 255, 0)); // styling, ...
        maxWeightDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        maxWeightDataSet.setDrawCircles(false);
        maxWeightDataSet.setLineWidth(2f);

        //---

        LineDataSet minWeightDataSet = new LineDataSet(minRateList, "Min Weight"); // add entries to dataset
        minWeightDataSet.setColor(rgb(255, 0, 0));
        minWeightDataSet.setValueTextColor(rgb(255, 0, 0)); // styling, ...
        minWeightDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        minWeightDataSet.setDrawCircles(false);
        minWeightDataSet.setLineWidth(2f);

        //---

        // use the interface ILineDataSet
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        dataSets.add(maxWeightDataSet);
        dataSets.add(minWeightDataSet);

        LineData lineData = new LineData(dataSets);

        // in this example, a LineChart is initialized from xml
        LineChart chart = mRoot.findViewById(R.id.chart);
        chart.setData(lineData);

        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                Date date = new Date((long)value);
                DateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());

                return dateFormat.format(date);
            }
        };

        XAxis x = chart.getXAxis();

        x.setAxisMinimum(startDate.getTime());
        x.setAxisMaximum(endDate.getTime());

        x.setGranularity(1f); // minimum axis-step (interval) is 1
        x.setLabelCount(7);
        x.setValueFormatter(formatter);

        YAxis y = chart.getAxisLeft();
        y.setAxisMinimum(80f);
        y.setAxisMaximum(90f);
        y.setLabelCount(12);

        YAxis y2 = chart.getAxisRight();
        y2.setEnabled(false);

        chart.setTouchEnabled(true);

        Description description = new Description();
        description.setText("Weight change and tendency");

        chart.setDescription(description);

        chart.zoom(2.0f, 1, 0, startWeight);

        chart.invalidate(); // refresh
    }
}