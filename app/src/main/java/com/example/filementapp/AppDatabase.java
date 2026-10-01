package com.example.filementapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Filament.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    public abstract FilamentDao filamentDao();

    private static AppDatabase instance;

    // Tilføjer den nye kolonne uden at slette eksisterende spoler
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                    "ALTER TABLE filaments ADD COLUMN purchaseDate TEXT NOT NULL DEFAULT ''");
        }
    };

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "filament-db")
                    .addMigrations(MIGRATION_1_2)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}