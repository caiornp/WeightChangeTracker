package com.example.weightchangetracker.ui.home;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weightchangetracker.R;
import com.example.weightchangetracker.models.WeightRegistry;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.List;

public class WeightListAdapter extends RecyclerView.Adapter<WeightListAdapter.WeightViewHolder> {

    public static class WeightViewHolder extends RecyclerView.ViewHolder {
        private final TextView weightItemView;
        private final TextView dateItemView;

        private WeightViewHolder(View itemView) {
            super(itemView);
            weightItemView = itemView.findViewById(R.id.textViewWeight);
            dateItemView = itemView.findViewById(R.id.textViewDate);
        }
    }

    private List<WeightRegistry> mWeights;
    private ViewGroup mParent;

    @Override
    @NonNull
    public WeightViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recyclerview_item, parent, false);
        mParent = parent;
        return new WeightViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NotNull WeightViewHolder holder, int position) {
        if (mWeights != null) {
            WeightRegistry current = mWeights.get(position);

            String strDate = DateFormat.getDateInstance().format(current.getDate());

            holder.dateItemView.setText(strDate);

            Resources res = mParent.getResources();

            String str = res.getString(R.string.weight_display, current.getWeight(),
                    res.getString(R.string.weight_unit));

            holder.weightItemView.setText(str);
        } else {
            // Covers the case of data not being ready yet.
            holder.weightItemView.setText(R.string.no_weight);
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

    public void removeItem(int position) {
        mWeights.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(WeightRegistry item, int position) {
        mWeights.add(position, item);
        notifyItemInserted(position);
    }

    public List<WeightRegistry> getData() {
        return mWeights;
    }

}
