package ca.mineself;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import ca.mineself.model.MetricEntry;

import static ca.mineself.AlarmReceiver.CHANNEL_ID;

public class MiningActivity extends AppCompatActivity {

    //Metric Value Input
    private SeekBar metricValueInput;

    //Metric Name Label
    private TextView metricNameLabel;

    //Metric Value Label
    private TextView metricValueLabel;

    //Add Action Entry Button
    private FloatingActionButton addActionEntryButton;

    //Action Entry Text Input
    private EditText actionEntryInput;

    //Action entries
    private RecyclerView actionEntriesRecycler;
    private ActionEntriesAdapter actionEntriesAdapter;
    private RecyclerView.LayoutManager actionEntriesLayoutManager;

    //Save timeout
    TimerTask metricSaveTimeout = null;

    //Constants
    final Long METRIC_SAVE_TIMEOUT = 5000L;
    final String METRIC_NAME = "Mood";

    final long REMINDER_INTERVAL = 10000; //Every half hour

    //Persistence
    public LocalDatabase localDb;

    //Last Metric Entry
    public MetricEntry lastMetricEntry;

    //Alarm Manager
    AlarmManager alarmManager;
    NotificationManager notificationManager;

    public static MiningActivity instance;

    public static MiningActivity getInstance(){
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * SETUP NOTIFICATIONS
         */

        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);

        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);


        registerReceiver(new AlarmReceiver(), new IntentFilter("ca.mineself.START_ALARM"));

        alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Log.d("AlarmManager", alarmManager.toString());
        Intent i = new Intent("ca.mineself.START_ALARM");
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(getApplicationContext(), 1,i,0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000*60*30, pendingIntent);



        /**
         * LOCAL DATABASE SETUP
         */
        localDb = Room.databaseBuilder(getApplicationContext(),
                LocalDatabase.class, "local-storage")
                .allowMainThreadQueries()
                .build();

        instance = this;

        lastMetricEntry = localDb.metricEntryDAO().getLast();


        Log.d("MetricEntry", "Printing metric values from DB");
        localDb.metricEntryDAO().getAll().forEach(
                metricEntry -> {
                    Log.d("MetricEntry", metricEntry.getName() + "=" +metricEntry.getValue() + " " +  metricEntry.getStartTime().toString());
                }
        );


        /**
         * METRIC NAME LABEL SETUP
         */
        metricNameLabel = (TextView) findViewById(R.id.metricNameLabel);


        /**
         * METRIC VALUE LABEL SETUP
         */
        metricValueLabel = (TextView) findViewById(R.id.metricValueLabel);
        metricValueLabel.setText(
                lastMetricEntry != null?Integer.toString(lastMetricEntry.getValue()):"0");

        /**
         * METRIC VALUE INPUT SETUP
         */
        metricValueInput = (SeekBar) findViewById(R.id.metricValueInput);
        metricValueInput.setProgress(
                lastMetricEntry!=null?lastMetricEntry.getValue():0);
        metricValueInput.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {

                findViewById(R.id.savedLabel).setVisibility(View.INVISIBLE);

                Log.d("metric value", Integer.toString(value));
                metricValueLabel.setText(Integer.toString(value));
                Log.d("timer task", "creating task");

                //Clear any past save timeout
                if(metricSaveTimeout != null){
                    metricSaveTimeout.cancel();
                }

                metricSaveTimeout = new TimerTask(){

                    @Override
                    public void run() {

                        //Get the time that will be the start of the new metric value as well as the end of the old one
                        Date time = Date.from(Instant.ofEpochMilli(Date.from(Instant.now()).getTime() - METRIC_SAVE_TIMEOUT));

                        if(lastMetricEntry != null){
                            Log.d("MetricEntry", "Updating last metric entry");
                            lastMetricEntry.setEndTime(time);
                            localDb.metricEntryDAO().updateEntries(lastMetricEntry);
                        }

                        Log.d("MetricEntry", "Creating new metric entry");
                        MetricEntry entry = new MetricEntry();
                        //Set the entry start time, accounting for the save delay
                        entry.setStartTime(time);
                        entry.setValue(value);
                        entry.setName(METRIC_NAME);

                        localDb.metricEntryDAO().insertAll(entry);
                        lastMetricEntry = entry;
                        Log.d("MetricEntry","saved");
                        findViewById(R.id.savedLabel).post(()->{
                            findViewById(R.id.savedLabel).setVisibility(View.VISIBLE);
                        });

                    }
                };


                new Timer().schedule(metricSaveTimeout, METRIC_SAVE_TIMEOUT);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        /**
         * ADD ACTION ENTRY BUTTON SETUP
         */
        addActionEntryButton = (FloatingActionButton) findViewById(R.id.addActionEntryButton);
        addActionEntryButton.setOnClickListener(event->{
            Log.d("mining activity", "floating action button click listener hit!");
            actionEntriesAdapter.addEntry(actionEntryInput.getText().toString());
            actionEntryInput.setText("");

        });

        /**
         * ACTION ENTRY INPUT SETUP
         */
        actionEntryInput = (EditText) findViewById(R.id.actionEntryInput);

        /**
         * ACTION ENTRIES RECYCLER SETUP
         */

        //Get the action entries recycler
        actionEntriesRecycler = (RecyclerView)findViewById(R.id.actionEntires);

        //Use a linear layout manager
        actionEntriesLayoutManager = new LinearLayoutManager(this);
        actionEntriesRecycler.setLayoutManager(actionEntriesLayoutManager);

        //Bind action entries adapter
        actionEntriesAdapter = new ActionEntriesAdapter();
        actionEntriesRecycler.setAdapter(actionEntriesAdapter);
    }
}