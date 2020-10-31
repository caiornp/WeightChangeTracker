package com.example.weightchangetracker.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "weight_registry")
public class WeightRegistry {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "date")
    public final Date date;

    @ColumnInfo(name = "weight")
    public final float weight;

    public WeightRegistry(Date date, float weight)
    {
        this.date = new Date(date.getTime());
        this.weight = weight;
    }

    public float getWeight() {return this.weight;}

    @NonNull
    public Date getDate() { return this.date; }
}
