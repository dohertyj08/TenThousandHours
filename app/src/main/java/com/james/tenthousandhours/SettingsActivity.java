package com.james.tenthousandhours;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {

    private SkillDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String colorScheme = getIntent().getStringExtra("colorscheme");

        if (colorScheme != null) {
            if (colorScheme.equals("green")) {
                setTheme(R.style.AppThemeGreen);
            }
        }

        setContentView(R.layout.activity_settings);
    }

    public void setGreen(View v) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                db = SkillDatabase.getDatabase(getApplicationContext());
                Settings s = db.settingsDao().findById(0);
                s.setColorscheme("green");
                db.settingsDao().updateSettings(s);
            }
        });
        finish();
    }

    public void setBlue(View v) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                db = SkillDatabase.getDatabase(getApplicationContext());
                Settings s = db.settingsDao().findById(0);
                s.setColorscheme("blue");
                db.settingsDao().updateSettings(s);
            }
        });
        finish();
    }
}
