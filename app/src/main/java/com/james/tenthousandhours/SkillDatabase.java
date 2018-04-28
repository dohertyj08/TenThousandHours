package com.james.tenthousandhours;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Skill.class}, version = 1)
public abstract class SkillDatabase extends RoomDatabase {
    public static SkillDatabase INSTANCE;

    public abstract SkillDao skillDao();

    public static SkillDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, SkillDatabase.class, "skill_database").build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
