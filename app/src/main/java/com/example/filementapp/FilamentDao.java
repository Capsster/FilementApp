package com.example.filementapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FilamentDao {

    @Insert
    void insert(Filament filament);

    @Update
    void update(Filament filament);

    @Delete
    void delete(Filament filament);

    @Query("SELECT * FROM filaments ORDER BY brand ASC")
    List<Filament> getAll();

    @Query("SELECT * FROM filaments WHERE id = :id")
    Filament getById(int id);
}