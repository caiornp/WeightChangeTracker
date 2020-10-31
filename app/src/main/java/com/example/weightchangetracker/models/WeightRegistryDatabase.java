package com.example.weightchangetracker.models;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.weightchangetracker.util.DateConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {WeightRegistry.class}, version = 1, exportSchema = false)
@TypeConverters({DateConverters.class})
public abstract class WeightRegistryDatabase extends RoomDatabase {
    public abstract WeightRegistryDao weightRegistryDao();

    private static volatile WeightRegistryDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static WeightRegistryDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WeightRegistryDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            WeightRegistryDatabase.class, "weight_registry_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                // If you want to start with more words, just add them.
                WeightRegistryDao dao = INSTANCE.weightRegistryDao();
                
                //dao.deleteAll();

                //Calendar cal = Calendar.getInstance();
                //cal.set(2020, 9, 26);
                //WeightRegistry weight = new WeightRegistry(cal.getTime(),88.8f);
                //dao.insert(weight);

                //cal.set(2020, 9, 27);
                //weight = new WeightRegistry(cal.getTime(), 88f);
                //dao.insert(weight);

                //cal.set(2020, 9, 28);
                //weight = new WeightRegistry(cal.getTime(),88.3f);
                //dao.insert(weight);

                //cal.set(2020, 9, 29);
                //weight = new WeightRegistry(cal.getTime(),88f);
                //dao.insert(weight);
            });
        }
    };
}
