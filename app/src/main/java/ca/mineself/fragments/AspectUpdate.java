package ca.mineself.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.write.Point;

import java.util.concurrent.CompletableFuture;

import ca.mineself.AspectsActivity;
import ca.mineself.Influx;
import ca.mineself.MineSelf;
import ca.mineself.R;
import ca.mineself.adapters.TagListAdapter;
import ca.mineself.model.Aspect;
import ca.mineself.model.Profile;


public class AspectUpdate extends Fragment {

    Profile profile;
    Aspect aspect;
    long newDelta;

    EditText editDelta;
    EditText editValue;
    EditText editNote;
    EditText editNewTag;

    Button addTagBtn;
    Button createTagBtn;
    Button updateBtn;

    TagListAdapter tagListAdapter;
    RecyclerView tagsList;

    LinearLayout createTagLayout;
    boolean showCreateTag = false;

    InfluxDBClient client;

    @Override
    public void onStart() {
        super.onStart();

        client = MineSelf.getInstance().openInfluxClient(profile);

        //Map UI
        editDelta = getView().findViewById(R.id.editDelta);
        editDelta.setText(Long.toString(newDelta));

        editValue = getView().findViewById(R.id.editValue);
        editValue.setText(Long.toString(newDelta + aspect.value));

        editNote = getView().findViewById(R.id.editNote);
        editNote.setOnFocusChangeListener(new MineSelf.AutoClearingEditText(editNote)::onFocusChange);

        editNewTag = getView().findViewById(R.id.editNewTag);
        editNewTag.setOnFocusChangeListener(new MineSelf.AutoClearingEditText(editNewTag));

        addTagBtn = getView().findViewById(R.id.addTagBtn);
        addTagBtn.setOnClickListener(this::showNewTagUI);

        createTagBtn = getView().findViewById(R.id.createNewTagBtn);
        createTagBtn.setOnClickListener(this::createTag);
        updateBtn = getView().findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(this::updateAspect);

        createTagLayout = getView().findViewById(R.id.createTagLayout);

        //Setup tag list recycler
        tagListAdapter = new TagListAdapter();
        tagsList = getView().findViewById(R.id.tagsList);
        tagsList.setLayoutManager(new LinearLayoutManager(getContext()));
        tagsList.setAdapter(tagListAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        aspect =  getArguments().getParcelable("aspect");
        profile = getArguments().getParcelable("profile");
        newDelta = getArguments().getLong("newDelta");
        return inflater.inflate(R.layout.aspect_update, container, false);
    }

    //Show tag create UI
    public void showNewTagUI(View view){
        Log.d(getClass().getSimpleName(), "Showing create tag UI!");
        this.showCreateTag = true;
        createTagLayout.setVisibility(View.VISIBLE);
        addTagBtn.setText(R.string.cancel_button_text);
        addTagBtn.setOnClickListener(this::hideNewTagUI);
    }

    public void hideNewTagUI(View view){
        Log.d(getClass().getSimpleName(), "Hiding create tag UI");
        this.showCreateTag = false;
        createTagLayout.setVisibility(View.GONE);
        addTagBtn.setText(R.string.add_button_text);
        addTagBtn.setOnClickListener(this::showNewTagUI);
        //Clear newTag fields
        editNewTag.setText(R.string.aspect_update_new_tag_placeholder);
    }

    public void createTag(View view){
        tagListAdapter.addTag(editNewTag.getText().toString());
        hideNewTagUI(view);
    }

    public void updateAspect(View view){

        Point dataPoint = aspect.produce(
                Long.parseLong(editDelta.getText().toString()),
                editNote.getText().toString(),
                tagListAdapter.getTags()
        );

        CompletableFuture.runAsync(()-> Influx.insertPoint(client, profile.name, profile.name, dataPoint))
                .thenRun(() -> {
                    Log.d(getClass().getSimpleName(), "data point sent!");

                    Intent backToAspects = new Intent(getContext(), AspectsActivity.class);
                    startActivity(backToAspects);
                });
    }
}
