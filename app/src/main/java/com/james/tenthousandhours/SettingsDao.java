package com.james.tenthousandhours;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.arch.persistence.room.OnConflictStrategy;

import java.util.List;

@Dao
public interface SettingsDao {
    @Query("SELECT * FROM settings")
    List<Settings> getAll();

    @Query("SELECT * FROM settings WHERE id = :id")
    Settings findById(long id);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateSettings(Settings settings);

    @Insert
    void insertSettings(Settings settings);
}
