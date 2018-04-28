package com.james.tenthousandhours;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface SkillDao {
    @Query("SELECT * FROM skill")
    List<Skill> getAll();

    @Query("SELECT * FROM skill WHERE skill_name LIKE :name")
    Skill findByName(String name);

    @Query("SELECT * FROM skill WHERE id = :id")
    Skill findById(long id);

    @Query("SELECT * FROM skill ORDER BY create_time DESC LIMIT 1")
    Skill findNewest();

    @Query("SELECT * FROM skill ORDER BY id DESC LIMIT 1")
    Skill findSkillWithLargestId();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateSkill(Skill skill);

    @Insert
    void insertAll(Skill... skills);

    @Insert
    void insertSkill(Skill skill);

    @Delete
    void delete(Skill skill);
}
