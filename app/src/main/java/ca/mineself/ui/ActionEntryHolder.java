package ca.mineself.ui;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import java.text.SimpleDateFormat;
import java.util.Date;

import ca.mineself.R;


public class ActionEntryHolder extends RecyclerView.ViewHolder {

    SimpleDateFormat timestampFormat = new SimpleDateFormat("hh:mm aa");

    private TextView startTimestamp;
    private TextView endTimestamp;
    public TextView value;
    public TextView defaultMetricValue;

    public ActionEntryHolder(View view){
        super(view);


        startTimestamp = view.findViewById(R.id.startLabel);
        endTimestamp = view.findViewById(R.id.endLabel);
        value = view.findViewById(R.id.valueLabel);
        defaultMetricValue = view.findViewById(R.id.defaultMetricValue);
    }

    public void setStart(Date date){
        startTimestamp.setText(timestampFormat.format(date));
    }

    public void setEnd(Date date){
        if(date == null){
            endTimestamp.setText("----");
            return;
        }
        endTimestamp.setText(timestampFormat.format(date));
    }

}
