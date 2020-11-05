package com.example.weightchangetracker.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.weightchangetracker.models.WeightRegistry;
import com.example.weightchangetracker.models.WeightRegistryRepository;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

        private final WeightRegistryRepository mRepository;
        private final LiveData<List<WeightRegistry>> mAllWeightRegistries;

        public HomeViewModel (Application application) {
                super(application);
                mRepository = new WeightRegistryRepository(application);
                mAllWeightRegistries = mRepository.getAllWeightRegistries();
        }

        public LiveData<List<WeightRegistry>> getAllWeights() {
                return mAllWeightRegistries;
        }

        public void insert(WeightRegistry weight) { mRepository.insert(weight); }

        public void delete(WeightRegistry weight) {
                mRepository.delete(weight);
        }
}