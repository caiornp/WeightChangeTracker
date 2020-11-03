package com.example.weightchangetracker.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weightchangetracker.R;
import com.example.weightchangetracker.models.WeightRegistry;
import com.example.weightchangetracker.ui.newweight.NewWeightActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {
    public static final int NEW_WEIGHT_ACTIVITY_REQUEST_CODE = 1;

    private HomeViewModel homeViewModel;
    private WeightListAdapter mWeighListAdapter;
    private RecyclerView mRecyclerView;
    CoordinatorLayout mCoordinatorLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mCoordinatorLayout = root.findViewById(R.id.coordinatorLayout);

        mWeighListAdapter = new WeightListAdapter();

        mRecyclerView = root.findViewById(R.id.recyclerview);
        mRecyclerView.setAdapter(mWeighListAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        enableSwipeToDeleteAndUndo();

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

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this.getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final WeightRegistry item = mWeighListAdapter.getData().get(position);

                mWeighListAdapter.removeItem(position);

                homeViewModel.delete(item);

                Snackbar snackbar = Snackbar
                        .make(mCoordinatorLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mWeighListAdapter.restoreItem(item, position);
                        homeViewModel.insert(item);
                        mRecyclerView.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(mRecyclerView);
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