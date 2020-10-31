package com.example.weightchangetracker.ui.dashboard;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.weightchangetracker.models.WeightRegistry;
import com.example.weightchangetracker.models.WeightRegistryRepository;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

public class DashboardViewModel extends AndroidViewModel {

    private List<Entry> mWeightList;

    private final LiveData<List<WeightRegistry>> mAllWeightRegistries;

    public DashboardViewModel(Application application) {
        super(application);
        mWeightList = new ArrayList<>();
        WeightRegistryRepository mRepository = new WeightRegistryRepository(application);
        mAllWeightRegistries = mRepository.getAllWeightRegistries();
    }

    public List<Entry> getWeightList() {
        return mWeightList;
    }

    public void addWeight(Entry e) { mWeightList.add(e); }

    public void clearWeights() { mWeightList = new ArrayList<>(); }

    public LiveData<List<WeightRegistry>> getAllWeights() {
        return mAllWeightRegistries;
    }
}