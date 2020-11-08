package com.example.weightchangetracker.ui.home;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weightchangetracker.R;
import com.example.weightchangetracker.models.WeightRegistry;
import com.example.weightchangetracker.ui.newweight.NewWeightActivity;
import com.example.weightchangetracker.util.DateConverters;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.time.OffsetDateTime;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {
    public static final int NEW_WEIGHT_ACTIVITY_REQUEST_CODE = 1;

    private HomeViewModel homeViewModel;
    private WeightListAdapter mWeighListAdapter;
    private RecyclerView mRecyclerView;
    ConstraintLayout mHomeLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mHomeLayout = root.findViewById(R.id.homeLayout);

        mWeighListAdapter = new WeightListAdapter();

        mRecyclerView = root.findViewById(R.id.recyclerview);
        mRecyclerView.setAdapter(mWeighListAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            mRecyclerView.setPadding(0, 0, 0, resources.getDimensionPixelSize(resourceId));
        }

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
                        .make(mHomeLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", view -> {

                    mWeighListAdapter.restoreItem(item, position);
                    homeViewModel.insert(item);
                    mRecyclerView.scrollToPosition(position);
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(mRecyclerView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_WEIGHT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            OffsetDateTime date = DateConverters.fromTimestamp(data.getStringExtra(NewWeightActivity.DATE_REPLY));

            float weightValue = data.getFloatExtra(NewWeightActivity.WEIGHT_REPLY, 0);

            WeightRegistry weight = new WeightRegistry(date, weightValue);
            homeViewModel.insert(weight);
        } else {
            Toast.makeText(
                    this.getContext(),
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG).show();
        }
    }
}