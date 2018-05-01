package com.james.tenthousandhours;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NewSkillActivity extends AppCompatActivity {

    private SkillDatabase db;
    private boolean isEdit = false;
    private long oldId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String colorScheme = getIntent().getStringExtra("colorscheme");

        if (colorScheme != null) {
            if (colorScheme.equals("green")) {
                setTheme(R.style.AppThemeGreen);
            } else if (colorScheme.equals("mono")) {
                setTheme(R.style.AppThemeMono);
            }
        }

        setContentView(R.layout.activity_new_skill);

        final long id = getIntent().getLongExtra("id", -1);

        if (id != -1) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    db = SkillDatabase.getDatabase(getApplicationContext());
                    Skill pSkill = db.skillDao().findById(id);
                    String name = pSkill.getSkillName();
                    EditText skillText = findViewById(R.id.skillName);
                    skillText.setText(name);
                    oldId = id;
                }
            });
            isEdit = true;
        } else
            isEdit = false;
    }

    public void saveSkill(View v) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                db = SkillDatabase.getDatabase(getApplicationContext());

                EditText skillText = findViewById(R.id.skillName);
                String skillString = skillText.getText().toString();

                if (db.skillDao().findByName(skillString) == null && skillString != null) {
                    if (isEdit) {
                        Skill skill = db.skillDao().findById(oldId);
                        skill.setSkillName(skillString);
                        db.skillDao().updateSkill(skill);

                    } else {
                        Skill temp = db.skillDao().findSkillWithLargestId();
                        long id = 0;
                        if (temp != null)
                            id = temp.getId() + 1;
                        Skill newSkill = new Skill(skillString, id);
                        db.skillDao().insertSkill(newSkill);
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewSkillActivity.this, "Cannot add duplicate skill.", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        });
        finish();
    }
}
