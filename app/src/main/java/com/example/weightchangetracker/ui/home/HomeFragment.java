package com.example.weightchangetracker.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weightchangetracker.R;

public class HomeFragment extends Fragment {

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

        return root;
    }
}