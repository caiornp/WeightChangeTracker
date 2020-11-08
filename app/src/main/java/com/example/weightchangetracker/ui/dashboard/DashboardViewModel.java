package com.example.weightchangetracker.ui.dashboard;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.preference.PreferenceManager;

import com.example.weightchangetracker.models.WeightRegistry;
import com.example.weightchangetracker.models.WeightRegistryRepository;
import com.example.weightchangetracker.util.DateConverters;
import com.github.mikephil.charting.data.Entry;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

public class DashboardViewModel extends AndroidViewModel implements Observable {
    private final WeightRegistryRepository mRepository;

    private final int mDietStartYear;
    private final int mDietStartMonth;
    private final int mDietStartDay;

    private final int mDietEndYear;
    private final int mDietEndMonth;
    private final int mDietEndDay;

    private final float maxWeekRate;
    private final float minWeekRate;

    private final OffsetDateTime mDietEndDate;
    private final OffsetDateTime mDietStartDate;

    private final boolean mShowMaxLine;
    private final boolean mShowMinLine;
    private final boolean mShowTendencyLine;

    private final PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

    private float mStartWeight;
    private ArrayList<Entry> mWeightList;

    //-----------------
    // Constructor
    //-----------------

    @RequiresApi(api = Build.VERSION_CODES.O)
    public DashboardViewModel(Application application) {
        super(application);

        mWeightList = new ArrayList<>();
        mRepository = new WeightRegistryRepository(application);
        LiveData<List<WeightRegistry>> mAllWeightRegistries = mRepository.getAllWeightRegistries();

        mAllWeightRegistries.observeForever(this::updateWeightList);

        mStartWeight = 90f;

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this.getApplication());

        mDietStartYear =
                Integer.parseInt(sharedPreferences.getString("calendar_diet_start_year", "0"));
        mDietStartMonth =
                Integer.parseInt(sharedPreferences.getString("calendar_diet_start_month", "0"));
        mDietStartDay =
                Integer.parseInt(sharedPreferences.getString("calendar_diet_start_day", "0"));

        mDietEndYear =
                Integer.parseInt(sharedPreferences.getString("calendar_diet_end_year", "0"));
        mDietEndMonth =
                Integer.parseInt(sharedPreferences.getString("calendar_diet_end_month", "0"));
        mDietEndDay =
                Integer.parseInt(sharedPreferences.getString("calendar_diet_end_day", "0"));

        maxWeekRate = Float.parseFloat(sharedPreferences.getString("max_change_rate", "0"));
        minWeekRate = Float.parseFloat(sharedPreferences.getString("min_change_rate", "0"));

        mDietEndDate = DateConverters.fromDayMonthYear(
                mDietEndYear,
                mDietEndMonth,
                mDietEndDay);

        mDietStartDate = DateConverters.fromDayMonthYear(
                mDietStartYear,
                mDietStartMonth,
                mDietStartDay);

        mShowMaxLine = sharedPreferences.getBoolean("switch_max_rate", false);
        mShowMinLine = sharedPreferences.getBoolean("switch_min_rate", false);
        mShowTendencyLine = sharedPreferences.getBoolean("switch_tendency", false);

        calculateStartWeight();

        notifyChange();
    }

    //-----------------
    // Private methods
    //-----------------

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
    private void updateWeightList(List<WeightRegistry> weights) {
        mWeightList = new ArrayList<>();

        for(WeightRegistry w : weights) {
            Entry entry = new Entry(DateConverters.dateToFloat(w.getDate()), w.getWeight());
            mWeightList.add(entry);
        }
        notifyPropertyChanged(BR.weightList);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void calculateStartWeight() {
        mRepository.findByDate(getDietStartDate()).observeForever(startWeights -> {
            if (!startWeights.isEmpty()) {
                mStartWeight = startWeights.get(0).getWeight();
                notifyPropertyChanged(BR.startWeight);
            }
        });
    }

    /**
     * Notifies observers that all properties of this instance have changed.
     *
     * */
    private void notifyChange() {
        callbacks.notifyCallbacks(this, 0, null);
    }

    private void notifyPropertyChanged(int fieldId) {
        callbacks.notifyCallbacks(this, fieldId, null);
    }

    //-----------------
    // Bindable methods
    //-----------------
    @Bindable
    ArrayList<Entry> getWeightList() {
        return mWeightList;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Bindable
    OffsetDateTime getDietStartDate() {
        return mDietStartDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Bindable
    OffsetDateTime getDietEndDate() {
        return mDietEndDate;
    }

    @Bindable
    float getStartWeight() {
        return mStartWeight;
    }

    @Bindable
    float getMaxWeekRate() {
        return maxWeekRate;
    }

    @Bindable
    float getMinWeekRate() {
        return minWeekRate;
    }

    @Bindable
    boolean getShowMaxLine() {
        return mShowMaxLine;
    }

    @Bindable
    boolean getShowMinLine() {
        return mShowMinLine;
    }

    @Bindable
    boolean getShowTendencyLine() {
        return mShowTendencyLine;
    }

    //-----------------
    // Protected methods
    //-----------------
    boolean allDietPreferencesSet() {
        return ((mDietStartYear > 0) &&
                (mDietStartMonth > 0) &&
                (mDietStartDay > 0) &&
                (mDietEndYear > 0) &&
                (mDietEndMonth > 0) &&
                (mDietEndDay > 0) &&
                (maxWeekRate > 0) &&
                (minWeekRate > 0));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    float getFloatDietStartDate() {
        return DateConverters.dateToFloat(mDietStartDate);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    ArrayList<Entry> getTendencyLine(List<Entry> data, int days) {
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
    List<Entry> getMaxRateLine() {
        return createLine(
               getDietStartDate(),
               getDietEndDate(),
               getStartWeight(),
               getMaxWeekRate());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    List<Entry> getMinRateLine() {
        return createLine(
                getDietStartDate(),
                getDietEndDate(),
                getStartWeight(),
                getMinWeekRate());
    }

    //-----------------
    // Public methods
    //-----------------

    @Override
    public void addOnPropertyChangedCallback(Observable.OnPropertyChangedCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(Observable.OnPropertyChangedCallback callback) {
        callbacks.remove(callback);
    }
}