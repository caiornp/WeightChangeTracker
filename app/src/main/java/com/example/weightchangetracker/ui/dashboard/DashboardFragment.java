package com.example.weightchangetracker.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.weightchangetracker.R;
import com.example.weightchangetracker.models.WeightRegistry;
import com.example.weightchangetracker.ui.home.WeightListAdapter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private WeightListAdapter mWeighListAdapter;
    private View mRoot;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        mRoot = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // in this example, a LineChart is initialized from xml
        LineChart chart = (LineChart) mRoot.findViewById(R.id.chart);

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
        LineDataSet dataSet = new LineDataSet(dashboardViewModel.getWeightList(), "Weight"); // add entries to dataset
        dataSet.setColor(0xff0000ff);
        dataSet.setValueTextColor(0xff0000ff); // styling, ...

        LineData lineData = new LineData(dataSet);

        // in this example, a LineChart is initialized from xml
        LineChart chart = (LineChart) mRoot.findViewById(R.id.chart);
        chart.setData(lineData);
        chart.invalidate(); // refresh
    }
}