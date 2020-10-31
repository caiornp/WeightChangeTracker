package com.example.weightchangetracker.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weightchangetracker.R;
import com.example.weightchangetracker.models.WeightRegistry;
import com.example.weightchangetracker.ui.newweight.NewWeightActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {
    public static final int NEW_WEIGHT_ACTIVITY_REQUEST_CODE = 1;

    private HomeViewModel homeViewModel;
    private WeightListAdapter mWeighListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //final TextView textView = root.findViewById(R.id.text_home);
        //homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
        //    @Override
        //    public void onChanged(@Nullable String s) {
        //        textView.setText(s);
        //    }
        //})

        mWeighListAdapter = new WeightListAdapter();

        RecyclerView recyclerView = root.findViewById(R.id.recyclerview);
        recyclerView.setAdapter(mWeighListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        homeViewModel.getAllWeights().observe(getViewLifecycleOwner(), weights -> {
            // Update the cached copy of the words in the adapter.
            mWeighListAdapter.setWeights(weights);
        });

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener( view -> {
            Intent intent = new Intent(this.getContext(), NewWeightActivity.class);
            startActivityForResult(intent, NEW_WEIGHT_ACTIVITY_REQUEST_CODE);
        });

        return root;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_WEIGHT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Date date = new Date(data.getLongExtra(NewWeightActivity.DATE_REPLY, 0));

            // Normalize the date by removing the hour, minute, second and millisecond to make searching easier
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            float weightValue = data.getFloatExtra(NewWeightActivity.WEIGHT_REPLY, 0);

            WeightRegistry weight = new WeightRegistry(cal.getTime(), weightValue);
            homeViewModel.insert(weight);
        } else {
            Toast.makeText(
                    this.getContext(),
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG).show();
        }
    }
}