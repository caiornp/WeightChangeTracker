package com.example.weightchangetracker.ui.dashboard;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.weightchangetracker.models.WeightRegistry;
import com.example.weightchangetracker.models.WeightRegistryRepository;
import com.example.weightchangetracker.util.DateConverters;
import com.github.mikephil.charting.data.Entry;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

public class DashboardViewModel extends AndroidViewModel {

    private List<Entry> mWeightList;

    private final LiveData<List<WeightRegistry>> mAllWeightRegistries;

    private final WeightRegistryRepository mRepository;

    public DashboardViewModel(Application application) {
        super(application);
        mWeightList = new ArrayList<>();
        mRepository = new WeightRegistryRepository(application);
        mAllWeightRegistries = mRepository.getAllWeightRegistries();
    }

    public List<Entry> getWeightList() {
        return mWeightList;
    }

    public void addWeight(Entry e) { mWeightList.add(e); }

    public void clearWeights() { mWeightList = new ArrayList<>(); }

    public LiveData<List<WeightRegistry>> findByDate(OffsetDateTime date) {
        return mRepository.findByDate(date);
    }

    /**
     * TODO:
     * - Rewrite ViewModel to handle all the data related points and leave Fragment only visualization
     */
    public void loadPreferences() {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<Entry> createLine(OffsetDateTime startDate, OffsetDateTime endDate, float startWeight, float weekRate) {
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
    public ArrayList<Entry> createTendency(List<Entry> data, int days) {
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

    public LiveData<List<WeightRegistry>> getAllWeights() {
        return mAllWeightRegistries;
    }
}