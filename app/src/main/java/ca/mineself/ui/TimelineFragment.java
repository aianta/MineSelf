package ca.mineself.ui;

import android.content.res.ColorStateList;
import android.content.res.Resources;
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

    private final String LOG_TAG = "[Timeline Fragment]";

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

    private void clearEntryInput(View v){
        actionEntryInput.setText("");
    }

    private void handleAddEntryClick(View v){
        Log.d(LOG_TAG, "floating action button click listener hit!");

        String entryValue = actionEntryInput.getText().toString();

        Log.d(LOG_TAG, "placeholder: " + getResources().getString(R.string.add_entry_input_placeholder));
        // Don't add empty action entries
        if(entryValue.isEmpty() || entryValue.equals(getResources().getString(R.string.add_entry_input_placeholder))){

            actionEntryInput.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.inputError,getActivity().getTheme())));
            Log.d("[TimelineFragment]", "Background tint mode "  + actionEntryInput.getBackgroundTintList().toString());
            return;
        }

        //Reset any input value highlighting
        actionEntryInput.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent, getActivity().getTheme())));

        //Add the new entry
        actionEntriesAdapter.addEntry(actionEntryInput.getText().toString());
        actionEntryInput.setText("");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.timeline_component, container, true);

        /**
         * ADD ACTION ENTRY BUTTON SETUP
         */
        addActionEntryButton = (FloatingActionButton) view.findViewById(R.id.addActionEntryButton);
        addActionEntryButton.setOnClickListener(this::handleAddEntryClick);

        /**
         * ACTION ENTRY INPUT SETUP
         */
        actionEntryInput = (EditText) view.findViewById(R.id.entryInput);
        actionEntryInput.setOnClickListener(this::clearEntryInput);
        actionEntryInput.setOnFocusChangeListener((view1, b) -> clearEntryInput(view1));

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
