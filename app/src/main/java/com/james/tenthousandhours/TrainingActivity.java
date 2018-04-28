package com.james.tenthousandhours;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import pl.droidsonroids.gif.GifTextView;

public class TrainingActivity extends AppCompatActivity {

    private SkillDatabase db;
    private Skill skill;
    private String skillName;

    private TextView title;

    private ScheduledExecutorService executor;
    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_skill);

        final long id = getIntent().getLongExtra("id", -1);
        counter = 0;

        if (id != -1) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    db = SkillDatabase.getDatabase(getApplicationContext());
                    Helpers h = new Helpers();

                    skill = db.skillDao().findById(id);
                    skillName = skill.getSkillName();

                    title = findViewById(R.id.trainingTitle);
                    String lvl = " [level " + h.getCurrentLevel(skill.getTime()) + "]";
                    title.setText(skillName + lvl);
                }
            });
        }

        GifTextView gif = findViewById(R.id.rsGif);
        gif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        db = SkillDatabase.getDatabase(getApplicationContext());
                        long currentTrainingTime = skill.getTime();
                        currentTrainingTime += System.currentTimeMillis() - skill.getTraining_start();
                        skill.setTime(currentTrainingTime);
                        skill.setTraining_start(-1);
                        db.skillDao().updateSkill(skill);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Stopped Training " + skillName, Toast.LENGTH_SHORT).show();
                            }
                        });

                        finish();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleProgressUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        executor.shutdownNow();
    }

    public void handleProgressUpdates() {
        executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        long trainingStart = skill.getTraining_start();
                        //this should always happen
                        if (trainingStart != -1) {
                            final long currentTrainingTime = skill.getTime();
                            final long newTrainingTime = currentTrainingTime + (System.currentTimeMillis() - trainingStart);

                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    db = SkillDatabase.getDatabase(getApplicationContext());
                                    Helpers h = new Helpers();

                                    skill.setTime(newTrainingTime);
                                    skill.setTraining_start(System.currentTimeMillis());
                                    int intLevel = h.getCurrentLevel(newTrainingTime);
                                    db.skillDao().updateSkill(skill);

                                    updateUI(intLevel, h.percentToNextLevel(currentTrainingTime), h.getMotivational());
                                }
                            });
                        }
                    }
                });
                counter++;
            }
        }, 0,1, TimeUnit.SECONDS);
    }

    public void updateUI(final int intLevel, final int percent, final String motivationText) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout ll = findViewById(R.id.trainingLayout);
                ProgressBar pb = findViewById(R.id.trainProgress);
                TextView title = findViewById(R.id.trainingTitle);
                TextView motivate = findViewById(R.id.feelGoodText);

                if (counter > 30) {
                    motivate.setText(motivationText);
                    counter = 0;
                }

                title.setText(skillName + " [level " + Integer.toString(intLevel) + "]");
                pb.setProgress(percent);
                if (intLevel < 10) {
                    ll.setBackgroundColor(getResources().getColor(R.color.color1));
                } else if (intLevel < 20) {
                    ll.setBackgroundColor(getResources().getColor(R.color.color2));
                } else if (intLevel < 30) {
                    ll.setBackgroundColor(getResources().getColor(R.color.color3));
                } else if (intLevel < 40) {
                    ll.setBackgroundColor(getResources().getColor(R.color.color4));
                } else if (intLevel < 50) {
                    ll.setBackgroundColor(getResources().getColor(R.color.color5));
                } else if (intLevel < 60) {
                    ll.setBackgroundColor(getResources().getColor(R.color.color6));
                } else if (intLevel < 70) {
                    ll.setBackgroundColor(getResources().getColor(R.color.color7));
                } else if (intLevel < 80) {
                    ll.setBackgroundColor(getResources().getColor(R.color.color8));
                } else if (intLevel < 90) {
                    ll.setBackgroundColor(getResources().getColor(R.color.color9));
                } else {
                    ll.setBackgroundColor(getResources().getColor(R.color.color0));
                }
            }
        });
    }
}
