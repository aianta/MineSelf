package ca.mineself;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {

    private int startHour = 7;
    private int endHour = 22;
    public static final String CHANNEL_ID = "MINE_SELF_CHANNEL";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("AlarmReceiver", "Got intent!");

        //If it is after the start hour but before the end hour
        LocalTime currentTime = LocalTime.now();
        if(currentTime.isAfter(LocalTime.of(startHour, 0)) && currentTime.isBefore(LocalTime.of(endHour,0))){

            Intent miningIntent = new Intent(context, MiningActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context).addNextIntentWithParentStack(miningIntent);
            PendingIntent pendingMiningIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("Mine Self Reminder")
                    .setContentText("Don't forget to update your mood and action!")
                    .setContentIntent(pendingMiningIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            MiningActivity.getInstance().notificationManager.notify(0,notificationBuilder.build());

        }
    }
}
