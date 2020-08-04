package ca.mineself.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import ca.mineself.MiningActivity;
import ca.mineself.R;
import ca.mineself.listeners.MetricSeekBarListener;
import ca.mineself.model.Metric;
import ca.mineself.model.MetricEntry;

public class MetricFragment extends Fragment {

    //Model
    private Metric metric;

    //UI Elements
    private SeekBar valueInput;
    private TextView nameLabel;
    private TextView savedLabel;
    private TextView valueLabel;

    //Listeners
    private SeekBar.OnSeekBarChangeListener valueListener;

    private MetricEntry last;

    public static MetricFragment addMetricFragment(FragmentManager fragmentManager, Metric metric){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        MetricFragment metricFragment = new MetricFragment();
        metricFragment.setMetric(metric);
        transaction.add(R.id.miningLayout, metricFragment);
        transaction.commit();
        return metricFragment;
    }


    @Override
    public void onStart() {
        super.onStart();

        //Fetch last metric entry from database
        last = MiningActivity.getInstance().localDb.metricEntryDAO().getLast();

        //Init UI elements
        valueInput = (SeekBar) getView().findViewById(R.id.valueInput);
        valueInput.setProgress(last != null ? last.getValue() : 0);

        valueLabel = (TextView) getView().findViewById(R.id.value);
        valueLabel.setText(last != null ? Integer.toString(last.getValue()) : "0");

        nameLabel = (TextView) getView().findViewById(R.id.metricName);
        nameLabel.setText(metric.getName());

        savedLabel = (TextView) getView().findViewById(R.id.savedLabel);

        //Init Listeners
        valueListener = new MetricSeekBarListener(this);

        //Bind Listeners
        valueInput.setOnSeekBarChangeListener(valueListener);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.metric_component, container,false);
    }

    public SeekBar getValueInput() {
        return valueInput;
    }

    public TextView getSavedLabel() {
        return savedLabel;
    }

    public TextView getValueLabel() {
        return valueLabel;
    }

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    public MetricEntry getLast() {
        return last;
    }

    public void setLast(MetricEntry last) {
        this.last = last;
    }
}
