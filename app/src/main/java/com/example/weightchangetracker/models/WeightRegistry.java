package com.example.weightchangetracker.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;

@Entity(tableName = "weight_registry")
public class WeightRegistry {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "input_date")
    public final OffsetDateTime date;

    @ColumnInfo(name = "weight")
    public final float weight;

    public WeightRegistry(@NotNull OffsetDateTime date, float weight)
    {
        this.date = date;
        this.weight = weight;
    }

    public float getWeight() {return this.weight;}

    @NonNull
    public OffsetDateTime getDate() { return this.date; }
}
