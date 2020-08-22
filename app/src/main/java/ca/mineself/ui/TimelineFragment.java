package ca.mineself.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ca.mineself.R;

/**
 *  Creates a timeline consisting of a an action entry timeline
 *  and the ui to add new action entries.
 */
public class TimelineFragment extends Fragment {

    // UI Elements
    private FloatingActionButton addActionEntryButton;
    private EditText actionEntryInput;
    private RecyclerView actionEntriesRecycler;

    // Action Entry controllers
    private ActionEntriesAdapter actionEntriesAdapter;
    private RecyclerView.LayoutManager actionEntriesLayoutManager;

    @Override
    public void onStart(){
        super.onStart();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.timeline_component, container, true);

        view.setOnClickListener(event->{
            Log.d("[TimelineFragment]", "why am I a cunt?");
        });
        /**
         * ADD ACTION ENTRY BUTTON SETUP
         */
        addActionEntryButton = (FloatingActionButton) view.findViewById(R.id.addActionEntryButton);
        addActionEntryButton.setOnClickListener(event->{
            Log.d("[TimelineFragment]", "floating action button click listener hit!");
            actionEntriesAdapter.addEntry(actionEntryInput.getText().toString());
            actionEntryInput.setText("");

        });

        /**
         * ACTION ENTRY INPUT SETUP
         */
        actionEntryInput = (EditText) view.findViewById(R.id.entryInput);

        /**
         * ACTION ENTRIES RECYCLER SETUP
         */

        //Get the action entries recycler
        actionEntriesRecycler = (RecyclerView)view.findViewById(R.id.actionEntries);

        //Use a linear layout manager
        actionEntriesLayoutManager = new LinearLayoutManager(getContext());
        actionEntriesRecycler.setLayoutManager(actionEntriesLayoutManager);

        //Bind action entries adapter
        actionEntriesAdapter = new ActionEntriesAdapter();
        actionEntriesRecycler.setAdapter(actionEntriesAdapter);

        return view;

    }

}
