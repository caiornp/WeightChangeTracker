package com.example.weightchangetracker.models;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.time.OffsetDateTime;
import java.util.List;

public class WeightRegistryRepository {
    private final WeightRegistryDao mWeightRegistryDao;
    private final LiveData<List<WeightRegistry>> mAllWeightRegistries;

    public WeightRegistryRepository(Application application) {
        WeightRegistryDatabase db = WeightRegistryDatabase.getDatabase(application);
        mWeightRegistryDao = db.weightRegistryDao();
        mAllWeightRegistries = mWeightRegistryDao.getAll();
    }

    public LiveData<List<WeightRegistry>> getAllWeightRegistries() {
        return mAllWeightRegistries;
    }

    public void insert(WeightRegistry weight) {
        WeightRegistryDatabase.databaseWriteExecutor.execute(() -> mWeightRegistryDao.insert(weight));
    }

    public void delete(WeightRegistry weight) {
        WeightRegistryDatabase.databaseWriteExecutor.execute(() -> mWeightRegistryDao.delete(weight));
    }

    public LiveData<List<WeightRegistry>> findByDate(OffsetDateTime date) {
        return mWeightRegistryDao.findByDate(date);
    }
}
