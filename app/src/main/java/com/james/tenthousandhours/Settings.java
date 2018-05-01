package com.james.tenthousandhours;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Settings {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "colorscheme")
    private String colorscheme;

    @ColumnInfo(name = "ascending")
    private boolean ascending;

    @ColumnInfo(name = "devmode")
    private boolean devmode;

    // don't use this
    public Settings() { }

    public Settings(String colorscheme, long id) {
        this.colorscheme = colorscheme;
        this.id = id;
        this.ascending = false;
        this.devmode = false;
    }

    public long getId() {
        return id;
    }

    public String getColorscheme() {
        return colorscheme;
    }

    public boolean isAscending() {
        return ascending;
    }

    public boolean isDevmode() {
        return devmode;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setColorscheme(String colorscheme) {
        this.colorscheme = colorscheme;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    public void setDevmode(boolean devmode) {
        this.devmode = devmode;
    }
}
