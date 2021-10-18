package com.github.cmput301f21t44.hellohabits.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * RoomDatabase class to be used by the whole app
 *
 * Make sure to increment the version number when adding new entities
 * or updating the schema of existing ones!
 */
@Database(entities = {HabitEntity.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract HabitDao habitDao();

    /** Singleton Instance for AppDatabase **/
    private static volatile AppDatabase INSTANCE;

    /** Thread pool for executing database functions (keeps it off the main thread) **/
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            // ensure only one instance is created by wrapping instantiation in a monitor lock (CMPUT 379 wink wink)
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "hello_habit_database")
                            .addCallback(sAppDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /** Callback for initializing database, use for populating with existing data. **/
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
}