package com.github.cmput301f21t44.hellohabits.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.github.cmput301f21t44.hellohabits.db.habit.HabitDao;
import com.github.cmput301f21t44.hellohabits.db.habit.HabitEntity;
import com.github.cmput301f21t44.hellohabits.db.habitevent.HabitEventDao;
import com.github.cmput301f21t44.hellohabits.db.habitevent.HabitEventEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * RoomDatabase class to be used by the whole app
 * <p>
 * Make sure to increment the version number when adding new entities
 * or updating the schema of existing ones!
 */
@Database(entities = {HabitEntity.class, HabitEventEntity.class}, version = 5, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    /**
     * Thread pool for executing database functions (keeps it off the main thread)
     **/
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    /**
     * Singleton Instance for AppDatabase
     **/
    private static volatile AppDatabase INSTANCE;

    /**
     * Callback for initializing database, use for populating with existing data.
     **/
    private static final AppDatabase.Callback sAppDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                HabitDao dao = INSTANCE.habitDao();
                dao.deleteAll();
                // pre-populate DB here
            });
        }
    };

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            // ensure only one instance is created
            // by wrapping instantiation in a monitor lock (CMPUT 379 wink wink)
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                            "hello_habit_database")
                            .addCallback(sAppDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract HabitDao habitDao();

    public abstract HabitEventDao habitEventDao();
}
