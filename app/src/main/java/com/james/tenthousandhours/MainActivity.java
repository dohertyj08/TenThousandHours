package com.james.tenthousandhours;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends ListActivity {

    private SkillDatabase db;

    // this is used to do fancy things in a list view
    // might not strictly be needed with the current feature set, but it will be handy
    private SkillAdapter skillAdapter;

    // this is used to handle the live updates on the ui
    private ScheduledExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //Add new skill floating button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newSkill = new Intent(MainActivity.this, NewSkillActivity.class);
                MainActivity.this.startActivity(newSkill);
            }
        });


        skillAdapter = new SkillAdapter();

        // skillAdapter.refresh();

        setListAdapter(skillAdapter);

        //delete skill from database and list adapter on long click
        this.getListView().setLongClickable(true);
        this.getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                //are you sure
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Configure")
                        .setMessage("Would you like to edit or delete?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        Skill skill = skillAdapter.getItem(position);
                                        db = SkillDatabase.getDatabase(getApplicationContext());
                                        db.skillDao().delete(skill);
                                        skillAdapter.removeItem(position);
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent editSkill = new Intent(MainActivity.this, NewSkillActivity.class);
                                editSkill.putExtra("id", skillAdapter.getItem(position).getId());
                                MainActivity.this.startActivity(editSkill);
                            }
                        })
                        .show();

                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        skillAdapter.refresh();

        handleProgressUpdates();
    }

    // this updates the ui with live stat updates.
    public void handleProgressUpdates() {
        executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int x = 0; x < skillAdapter.getCount(); x++) {
                            final Skill skill = skillAdapter.getItem(x);
                            long trainingStart = skill.getTraining_start();
                            if (trainingStart != -1) {
                                Helpers h = new Helpers();
                                long currentTrainingTime = skill.getTime();
                                final long newTrainingTime = currentTrainingTime + (System.currentTimeMillis() - trainingStart);
                                final int position = x;

                                if (h.getCurrentLevel(currentTrainingTime) < h.getCurrentLevel(newTrainingTime)) {
                                    sendNotification();
                                }

                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (skill.getTraining_start() != -1) {
                                            db = SkillDatabase.getDatabase(getApplicationContext());
                                            skill.setTime(newTrainingTime);
                                            skill.setTraining_start(System.currentTimeMillis());
                                            db.skillDao().updateSkill(skill);
                                            skillAdapter.updateItem(skill, position);
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        }, 0,1, TimeUnit.SECONDS);
    }

    private void sendNotification() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            NotificationChannel channel = new NotificationChannel("id", name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "id")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Congradulations!")
                .setContentText("Good job! Keep training to become a master!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(0, builder.build());
    }

    @Override
    public void onPause() {
        super.onPause();
        executor.shutdownNow();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView lv, View v, final int position, long id) {
        super.onListItemClick(lv, v, position, id);

        // Toast.makeText(MainActivity.this, skillAdapter.getItem(position).getSkillName(), Toast.LENGTH_SHORT).show();

        // write the skill start time to the database
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                db = SkillDatabase.getDatabase(getApplicationContext());
                Skill skill = db.skillDao().findById(skillAdapter.getItem(position).getId());

                if (skill.getTraining_start() == -1) {
                    skill.setTraining_start(System.currentTimeMillis());
                    Intent trainSkill = new Intent(MainActivity.this, TrainingActivity.class);
                    trainSkill.putExtra("id", skill.getId());
                    MainActivity.this.startActivity(trainSkill);
                } else {
                    long currentTrainingTime = skill.getTime();
                    currentTrainingTime += System.currentTimeMillis() - skill.getTraining_start();
                    skill.setTime(currentTrainingTime);
                    skill.setTraining_start(-1);
                }

                db.skillDao().updateSkill(skill);
                skillAdapter.updateItem(skill, position);
            }
        });
    }

    private class SkillAdapter extends BaseAdapter {
        private ArrayList<Skill> skillArrayList = new ArrayList<Skill>();
        private LayoutInflater inflater;
        private ArrayList<String> motivationTextList = new ArrayList<>();
        // private String motivationText = "You're doing great!";

        public SkillAdapter() {
            inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void refresh() {
            while (skillArrayList.size() > 0) {
                skillArrayList.remove(0);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });

            //populate the adapter with skills from the database
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    db = SkillDatabase.getDatabase(getApplicationContext());
                    List<Skill> allSkills = db.skillDao().getAll();

                    Collections.sort(allSkills, new SkillComparer());
                    Collections.reverse(allSkills);

                    for (Skill skill : allSkills ) {
                        skillAdapter.addSkill(skill);
                    }
                }
            });
        }

        public void addSkill(final Skill skill) {
            Helpers h = new Helpers();
            skillArrayList.add(skill);
            motivationTextList.add(h.getMotivational());
            //notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            // maybe do stuff here
            return -1;
        }

        @Override
        public int getCount() {
            return skillArrayList.size();
        }

        @Override
        public Skill getItem(int position) {
            return skillArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void updateItem(final Skill skill, int position) {
            skillArrayList.set(position, skill);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }

        public void removeItem(int position) {
            skillArrayList.remove(position);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                ViewHolder vh = new ViewHolder();
                Helpers h = new Helpers();

                convertView = inflater.inflate(R.layout.single_skill_layout, null);

                vh.skillName = (TextView)convertView.findViewById(R.id.skill_text_view);
                vh.skillInfo = (TextView)convertView.findViewById(R.id.skill_info);
                vh.skillLevel = (TextView)convertView.findViewById(R.id.skill_level);
                vh.skillProgress = (ProgressBar)convertView.findViewById(R.id.skill_progress);

                long trainingStart = skillArrayList.get(position).getTraining_start();
                long currentTrainingTime = skillArrayList.get(position).getTime();
                int intLevel = h.getCurrentLevel(currentTrainingTime);
                String level = Integer.toString(intLevel);
                String training;

                if (trainingStart == -1) {
                    long trainingRemaining = h.getTrainingTimeRemainingInMinutes(currentTrainingTime);
                    if (trainingRemaining == 0) {
                        training = "You are moments away from a level up! Click to start Training!";
                    } else {
                        training = "Only " + Long.toString(trainingRemaining) + " minutes until your next level! Click to start Training!";
                    }
                } else {
                    if (Math.random() > .98)
                        motivationTextList.set(position, h.getMotivational());

                    training = motivationTextList.get(position);
                }

                vh.skillLevel.setText(level);
                vh.skillName.setText((skillArrayList.get(position)).getSkillName());
                vh.skillInfo.setText(training);
                vh.skillProgress.setProgress(h.percentToNextLevel(currentTrainingTime));

                if (intLevel < 10) {
                    vh.skillLevel.setBackgroundColor(getResources().getColor(R.color.color1));
                    vh.skillName.setBackgroundColor(getResources().getColor(R.color.color1));
                    vh.skillInfo.setBackgroundColor(getResources().getColor(R.color.color1));
                    vh.skillProgress.setBackgroundColor(getResources().getColor(R.color.color1));
                } else if (intLevel < 20) {
                    vh.skillLevel.setBackgroundColor(getResources().getColor(R.color.color2));
                    vh.skillName.setBackgroundColor(getResources().getColor(R.color.color2));
                    vh.skillInfo.setBackgroundColor(getResources().getColor(R.color.color2));
                    vh.skillProgress.setBackgroundColor(getResources().getColor(R.color.color2));
                } else if (intLevel < 30) {
                    vh.skillLevel.setBackgroundColor(getResources().getColor(R.color.color3));
                    vh.skillName.setBackgroundColor(getResources().getColor(R.color.color3));
                    vh.skillInfo.setBackgroundColor(getResources().getColor(R.color.color3));
                    vh.skillProgress.setBackgroundColor(getResources().getColor(R.color.color3));
                } else if (intLevel < 40) {
                    vh.skillLevel.setBackgroundColor(getResources().getColor(R.color.color4));
                    vh.skillName.setBackgroundColor(getResources().getColor(R.color.color4));
                    vh.skillInfo.setBackgroundColor(getResources().getColor(R.color.color4));
                    vh.skillProgress.setBackgroundColor(getResources().getColor(R.color.color4));
                } else if (intLevel < 50) {
                    vh.skillLevel.setBackgroundColor(getResources().getColor(R.color.color5));
                    vh.skillName.setBackgroundColor(getResources().getColor(R.color.color5));
                    vh.skillInfo.setBackgroundColor(getResources().getColor(R.color.color5));
                    vh.skillProgress.setBackgroundColor(getResources().getColor(R.color.color5));
                } else if (intLevel < 60) {
                    vh.skillLevel.setBackgroundColor(getResources().getColor(R.color.color6));
                    vh.skillName.setBackgroundColor(getResources().getColor(R.color.color6));
                    vh.skillInfo.setBackgroundColor(getResources().getColor(R.color.color6));
                    vh.skillProgress.setBackgroundColor(getResources().getColor(R.color.color6));
                } else if (intLevel < 70) {
                    vh.skillLevel.setBackgroundColor(getResources().getColor(R.color.color7));
                    vh.skillName.setBackgroundColor(getResources().getColor(R.color.color7));
                    vh.skillInfo.setBackgroundColor(getResources().getColor(R.color.color7));
                    vh.skillProgress.setBackgroundColor(getResources().getColor(R.color.color7));
                } else if (intLevel < 80) {
                    vh.skillLevel.setBackgroundColor(getResources().getColor(R.color.color8));
                    vh.skillName.setBackgroundColor(getResources().getColor(R.color.color8));
                    vh.skillInfo.setBackgroundColor(getResources().getColor(R.color.color8));
                    vh.skillProgress.setBackgroundColor(getResources().getColor(R.color.color8));
                } else if (intLevel < 90) {
                    vh.skillLevel.setBackgroundColor(getResources().getColor(R.color.color9));
                    vh.skillName.setBackgroundColor(getResources().getColor(R.color.color9));
                    vh.skillInfo.setBackgroundColor(getResources().getColor(R.color.color9));
                    vh.skillProgress.setBackgroundColor(getResources().getColor(R.color.color9));
                } else {
                    vh.skillLevel.setBackgroundColor(getResources().getColor(R.color.color0));
                    vh.skillName.setBackgroundColor(getResources().getColor(R.color.color0));
                    vh.skillInfo.setBackgroundColor(getResources().getColor(R.color.color0));
                    vh.skillProgress.setBackgroundColor(getResources().getColor(R.color.color0));
                }
                //convertView.setTag(vh);
            }

            return convertView;
        }
    }

    public static class ViewHolder {
        public TextView skillName;
        public TextView skillInfo;
        public TextView skillLevel;
        public ProgressBar skillProgress;
    }

}
