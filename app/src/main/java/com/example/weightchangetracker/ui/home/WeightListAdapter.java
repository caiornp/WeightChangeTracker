package com.example.weightchangetracker.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weightchangetracker.R;
import com.example.weightchangetracker.models.WeightRegistry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class WeightListAdapter extends RecyclerView.Adapter<WeightListAdapter.WeightViewHolder> {

    public class WeightViewHolder extends RecyclerView.ViewHolder {
        private final TextView weightItemView;
        private final TextView dateItemView;

        private WeightViewHolder(View itemView) {
            super(itemView);
            weightItemView = itemView.findViewById(R.id.textViewWeight);
            dateItemView = itemView.findViewById(R.id.textViewDate);
        }
    }

    private List<WeightRegistry> mWeights;

    @Override
    @NonNull
    public WeightViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recyclerview_item, parent, false);
        return new WeightViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WeightViewHolder holder, int position) {
        if (mWeights != null) {
            WeightRegistry current = mWeights.get(position);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            //String strDate = dateFormat.format(current.getDate());
            String strDate = DateFormat.getDateInstance().format(current.getDate());

            holder.dateItemView.setText(strDate);
            holder.weightItemView.setText(String.valueOf(current.getWeight()) + " kg ");
        } else {
            // Covers the case of data not being ready yet.
            holder.weightItemView.setText("No Weight");
        }
    }

    public void setWeights(List<WeightRegistry> weights){
        mWeights = weights;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mWeights != null)
            return mWeights.size();
        else return 0;
    }

}
