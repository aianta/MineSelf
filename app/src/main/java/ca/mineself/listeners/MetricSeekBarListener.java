package ca.mineself.listeners;

import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.time.Instant;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import ca.mineself.MiningActivity;
import ca.mineself.fragment.MetricFragment;
import ca.mineself.model.MetricEntry;

import static ca.mineself.Options.METRIC_SAVE_TIMEOUT;

public class MetricSeekBarListener implements SeekBar.OnSeekBarChangeListener {

    private MetricFragment fragment;
    private TimerTask saveTimeout;

    public MetricSeekBarListener(MetricFragment fragment){
        this.fragment = fragment;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {

        //Hide the saved message when the value changes
        fragment.getSavedLabel().setVisibility(View.INVISIBLE);

        Log.d("MetricValue", Integer.toString(value));
        fragment.getValueLabel().setText(Integer.toString(value));

        if(saveTimeout != null){
            saveTimeout.cancel();
        }

        saveTimeout = new TimerTask(){

            @Override
            public void run() {

                //Get the time that will be the start of the new metric value as well as the end of the old one
                Date time = Date.from(Instant.ofEpochMilli(Date.from(Instant.now()).getTime() - METRIC_SAVE_TIMEOUT));

                if(fragment.getLast() != null){
                    Log.d("MetricEntry", "Updating last metric entry");
                    fragment.getLast().setEndTime(time);
                    MiningActivity.getInstance().localDb.metricEntryDAO().updateEntries(fragment.getLast());
                }

                Log.d("MetricEntry", "Creating new metric entry");

                MetricEntry entry = new MetricEntry();

                //Set the entry start time, accounting for the save delay
                entry.setStartTime(time);
                entry.setValue(value);
                entry.setName("Mood");

                MiningActivity.getInstance().localDb.metricEntryDAO().insertAll(entry);
                fragment.setLast(entry);
                Log.d("MetricEntry","saved");

                fragment.getSavedLabel().post(()->{
                    fragment.getSavedLabel().setVisibility(View.VISIBLE);
                });

            }
        };


        new Timer().schedule(saveTimeout, METRIC_SAVE_TIMEOUT);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        /** Don't save the new value immediately as the user may still be fiddling with
         *  what value they'd like to input.
         */

        //Clear any past save timeout
        if(saveTimeout != null){
            saveTimeout.cancel();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
