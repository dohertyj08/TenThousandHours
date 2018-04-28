package com.james.tenthousandhours;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Skill {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private long id;

    @NonNull
    @ColumnInfo(name = "skill_name")
    private String skillName;

    @ColumnInfo(name = "time")
    private long time;

    @ColumnInfo(name = "training_start")
    private long training_start;

    @ColumnInfo(name = "create_time")
    private long create_time;

    //don't use this
    public Skill() {
        this.skillName = "newskill";
        this.time = 0;
        this.training_start = -1;
        this.create_time = System.currentTimeMillis();
    }

    public Skill(String skillname, long id) {
        this.id = id;
        this.skillName = skillname;
        this.time = 0;
        this.training_start = -1;
        this.create_time = System.currentTimeMillis();
    }

    public long getId() { return this.id; }

    public String getSkillName() {
        return this.skillName;
    }

    public long getTime() {
        return this.time;
    }

    public long getTraining_start() { return this.training_start; }

    public long getCreate_time() { return this.create_time; }

    public void setId(long id) { this.id = id; }

    public void setSkillName(String name) {
        this.skillName = name;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setTraining_start(long current_time) {
        this.training_start = current_time;
    }

    public void setCreate_time(long current_time) { this.create_time = current_time; }
}
