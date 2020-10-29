package com.example.weightchangetracker.models;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.sql.Date;
import java.util.List;

@Dao
public interface WeightRegistryDao {
    @Query("SELECT * FROM weight_registry")
    LiveData<List<WeightRegistry>> getAll();

    @Query("SELECT * FROM weight_registry WHERE date = :date ORDER BY date ASC")
    List<WeightRegistry> findByDate(Date date);

    @Insert
    void insertAll(WeightRegistry... weightRegistries);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(WeightRegistry weight);

    @Query("DELETE FROM weight_registry")
    void deleteAll();

    @Delete
    void delete(WeightRegistry weightRegistry);
}
