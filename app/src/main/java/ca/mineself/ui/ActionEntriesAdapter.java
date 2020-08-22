package ca.mineself.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import ca.mineself.MiningActivity;
import ca.mineself.R;
import ca.mineself.model.ActionEntry;
import ca.mineself.model.MetricEntry;

public class ActionEntriesAdapter extends RecyclerView.Adapter<ActionEntryHolder> {

    private List<ActionEntry> entries;

    public void addEntry(String name){
        //Create the new action entry
        ActionEntry entry = new ActionEntry();
        entry.setName(name);
        entry.setStartTime(Date.from(Instant.now()));

        /** If we have previous entries set the end timestamp to the start timestamp of this entry.
         */
        if(entries.size() > 0){
            ActionEntry previousEntry = entries.get(0);
            previousEntry.setEndTime(entry.getStartTime());
            MiningActivity.getInstance().localDb.actionEntryDAO().updateEntries(previousEntry);
            Log.d("Persistence","Updated previous action entry end timestamp");
        }

        //Add the new entry to the list of entries
        entries.add(0,entry);

        notifyItemInserted(0);
        notifyDataSetChanged();
        Log.d("Add Entry", "allegedly inserted item and notified view..." + entries.size());

        Log.d("Persistence", "Persisting new action entry!");
        MiningActivity.getInstance().localDb.actionEntryDAO().insertAll(entry);
        Log.d("Persistence", "Action Entry persisted");
    }

    public ActionEntriesAdapter(){
        entries = MiningActivity.getInstance().localDb.actionEntryDAO().getAll();
    }

    @NonNull
    @Override
    public ActionEntryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Create a new text view for the holder

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_entry, parent, false);

        ActionEntryHolder holder = new ActionEntryHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ActionEntryHolder holder, int position) {

        ActionEntry entry = entries.get(position);
        holder.value.setText(entry.getName());
        holder.setStart(entry.getStartTime());
        holder.setEnd(entry.getEndTime());

        MetricEntry metric = MiningActivity.getInstance().localDb.metricEntryDAO().selectAt(entry.getStartTime());

        if(metric != null){
            Log.d("ActionEntriesAdapter", "name: "+metric.getName() + " value: " + metric.getValue());
            holder.defaultMetricValue.setText(""+metric.getValue());
        }


        Log.d("onBindViewHolder", "entry text @ "+position+" should be " + entries.get(position).getName());
        Log.d("entries size", Integer.toString(entries.size()));
        Log.d("textView value", holder.value.getText().toString());


    }

    @Override
    public int getItemCount() {
        return entries.size();
    }


}
