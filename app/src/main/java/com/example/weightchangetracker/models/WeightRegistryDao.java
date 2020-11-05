package com.example.weightchangetracker.models;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.time.OffsetDateTime;
import java.util.List;

@Dao
public interface WeightRegistryDao {
    @Query("SELECT * FROM weight_registry")
    LiveData<List<WeightRegistry>>getAll();

    @Query("SELECT * FROM weight_registry WHERE input_date = :date ORDER BY input_date ASC")
    LiveData<List<WeightRegistry>> findByDate(OffsetDateTime date);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(WeightRegistry weight);

    @Delete
    void delete(WeightRegistry weightRegistry);
}
