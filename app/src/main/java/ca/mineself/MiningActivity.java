package ca.mineself;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
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
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ca.mineself.fragment.MetricFragment;
import ca.mineself.model.Metric;
import ca.mineself.model.MetricEntry;
import ca.mineself.ui.ActionEntriesAdapter;
import ca.mineself.ui.TimelineFragment;

import static ca.mineself.AlarmReceiver.CHANNEL_ID;

public class MiningActivity extends AppCompatActivity {


    //Persistence
    public LocalDatabase localDb;

    //Last Metric Entry
    public MetricEntry lastMetricEntry;

    //Alarm Manager
    AlarmManager alarmManager;
    NotificationManager notificationManager;

    public FragmentManager fragmentManager;

    private MetricFragment metricFragment;
    private TimelineFragment timelineFragment;

    public static MiningActivity instance;

    public static MiningActivity getInstance(){
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /**
         * LOCAL DATABASE SETUP
         */
        localDb = Room.databaseBuilder(getApplicationContext(),
                LocalDatabase.class, "local-storage")
                .allowMainThreadQueries()
                .build();

        instance = this;
        Log.d("MiningActivity", "local database setup!");

        setContentView(R.layout.activity_main);

        /*
         * SETUP FRAGMENTS
         */
        fragmentManager = getSupportFragmentManager();

        Metric metric = new Metric();
        metric.setName("Mood");
        metricFragment = MetricFragment.addMetricFragment(fragmentManager,metric);



        /*
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


        //registerReceiver(new AlarmReceiver(), new IntentFilter("ca.mineself.START_ALARM"));

        alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Log.d("AlarmManager", alarmManager.toString());
        Intent i = new Intent("ca.mineself.START_ALARM");
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(getApplicationContext(), 1,i,0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);





        lastMetricEntry = localDb.metricEntryDAO().getLast();


        Log.d("MetricEntry", "Printing metric values from DB");
        localDb.metricEntryDAO().getAll().forEach(
                metricEntry -> {
                    Log.d("MetricEntry", metricEntry.getName() + "=" +metricEntry.getValue() + " " +  metricEntry.getStartTime().toString());
                }
        );






    }

}