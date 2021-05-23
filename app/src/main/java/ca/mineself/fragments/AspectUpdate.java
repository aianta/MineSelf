package ca.mineself.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.write.Point;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import ca.mineself.AspectsActivity;
import ca.mineself.Influx;
import ca.mineself.MineSelf;
import ca.mineself.R;
import ca.mineself.adapters.TagListAdapter;
import ca.mineself.model.Aspect;
import ca.mineself.model.Profile;
import ca.mineself.model.Tag;


public class AspectUpdate extends Fragment {

    Profile profile;
    Aspect aspect;
    long newDelta;

    EditText editDelta;
    EditText editValue;
    EditText editNote;
    EditText editNewTag;

    ImageView deltaUpdateUpIcon;
    ImageView deltaUpdateDownIcon;
    ImageView deltaUpdateNeutralIcon;

    Button addTagBtn;
    Button createTagBtn;
    Button updateBtn;

    TagListAdapter tagListAdapter;
    RecyclerView tagsList;

    LinearLayout createTagLayout;
    boolean showCreateTag = false;

    InfluxDBClient client;

    protected class AspectDeltaWatcher implements TextWatcher{
        Aspect aspect;
        EditText editValue;
        EditText editDelta;
        ImageView deltaUpdateDownIcon;
        ImageView deltaUpdateUpIcon;
        ImageView deltaUpdateNeutralIcon;

        public AspectDeltaWatcher(Aspect aspect,
                                  EditText editValue,
                                  EditText editDelta,
                                  ImageView deltaUpdateUpIcon,
                                  ImageView deltaUpdateDownIcon,
                                  ImageView deltaUpdateNeutralIcon){
            this.aspect = aspect;
            this.editValue = editValue;
            this.editDelta = editDelta;
            this.deltaUpdateNeutralIcon = deltaUpdateNeutralIcon;
            this.deltaUpdateDownIcon = deltaUpdateDownIcon;
            this.deltaUpdateUpIcon = deltaUpdateUpIcon;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(editable.toString().isEmpty()){
                return;
            }

            try{
                if(Long.parseLong(editable.toString()) < 0){
                    editDelta.setTextColor(Color.RED);
                    deltaUpdateDownIcon.setVisibility(View.VISIBLE);
                    deltaUpdateNeutralIcon.setVisibility(View.GONE);
                    deltaUpdateUpIcon.setVisibility(View.GONE);
                }
                if(Long.parseLong(editable.toString()) == 0){
                    editDelta.setTextColor(Color.GRAY);
                    deltaUpdateNeutralIcon.setVisibility(View.VISIBLE);
                    deltaUpdateUpIcon.setVisibility(View.GONE);
                    deltaUpdateDownIcon.setVisibility(View.GONE);
                }
                if(Long.parseLong(editable.toString()) > 0) {
                    editDelta.setTextColor(Color.GREEN);
                    deltaUpdateUpIcon.setVisibility(View.VISIBLE);
                    deltaUpdateDownIcon.setVisibility(View.GONE);
                    deltaUpdateNeutralIcon.setVisibility(View.GONE);
                }

                //Update value if it's something different
                if(aspect.value + Long.parseLong(editable.toString()) != Long.parseLong(editValue.getText().toString())){
                    editValue.setText(Long.toString(aspect.value + Long.parseLong(editable.toString())));
                }
            }catch (NumberFormatException nfe){
                Log.d(getClass().getSimpleName(), "Number format exception while processing delta value");
                return;
            }

        }
    }

    protected class AspectValueWatcher implements TextWatcher {
        Aspect aspect;
        EditText editValue;
        EditText editDelta;

        public AspectValueWatcher(Aspect aspect, EditText editValue, EditText editDelta){
            this.aspect = aspect;
            this.editValue = editValue;
            this.editDelta = editDelta;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(editable.toString().isEmpty()){
                return;
            }
            Long newDelta = Long.parseLong(editable.toString()) - aspect.value;
            //Update delta if it's something different
            if (Long.parseLong(editDelta.getText().toString()) != newDelta){
                editDelta.setText(Long.toString(newDelta));
            }

        }
    }

    @Override
    public void onStart() {
        super.onStart();



        deltaUpdateUpIcon = getView().findViewById(R.id.deltaUpdateUpIcon);
        deltaUpdateDownIcon = getView().findViewById(R.id.deltaUpdateDownIcon);
        deltaUpdateNeutralIcon = getView().findViewById(R.id.deltaUpdateNeutralIcon);

        Log.d(getClass().getSimpleName(), deltaUpdateDownIcon.toString());

        //Map UI
        editDelta = getView().findViewById(R.id.editDelta);
        editValue = getView().findViewById(R.id.editValue);


        editDelta.addTextChangedListener(new AspectDeltaWatcher(
                aspect, editValue, editDelta, deltaUpdateUpIcon, deltaUpdateDownIcon, deltaUpdateNeutralIcon));
        editDelta.setText(Long.toString(newDelta));



        editValue.setText(Long.toString(newDelta + aspect.value));
        editValue.addTextChangedListener(new AspectValueWatcher(aspect, editValue, editDelta));

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        aspect =  getArguments().getParcelable("aspect");
        profile = getArguments().getParcelable("profile");
        newDelta = getArguments().getLong("newDelta");
        client = MineSelf.getInstance().openInfluxClient(profile);
        return inflater.inflate(R.layout.aspect_update, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onViewCreated( View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Setup tag list recycler
        tagListAdapter = new TagListAdapter();
        tagsList = getView().findViewById(R.id.tagsList);
        tagsList.setLayoutManager(new LinearLayoutManager(getContext()));
        tagsList.setAdapter(tagListAdapter);


        tagListAdapter.setTags(CompletableFuture.supplyAsync(()->Influx.getTags(client,profile.name,profile.name))
                .join());


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
        Tag tag = new Tag();
        tag.key = editNewTag.getText().toString();
        tagListAdapter.addTag(tag);
        hideNewTagUI(view);
    }

    public void updateAspect(View view){

        Point dataPoint = aspect.produce(
                Long.parseLong(editDelta.getText().toString()),
                editNote.getText().toString(),
                tagListAdapter.getTagsAsMap()
        );

        CompletableFuture.runAsync(()-> Influx.insertPoint(client, profile.name, profile.name, dataPoint))
                .thenRun(() -> {
                    Log.d(getClass().getSimpleName(), "data point sent!");

                    Intent backToAspects = new Intent(getContext(), AspectsActivity.class);
                    backToAspects.putExtra("profile", profile);
                    startActivity(backToAspects);
                });
    }
}
