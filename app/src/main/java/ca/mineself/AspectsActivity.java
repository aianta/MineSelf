package ca.mineself;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.influxdb.client.InfluxDBClient;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import ca.mineself.adapters.AspectListAdapter;
import ca.mineself.model.Aspect;
import ca.mineself.model.Profile;

public class AspectsActivity extends AppCompatActivity {

    LinearLayout newAspectLayout;
    EditText newAspectName;
    EditText newAspectValue;
    Button newAspectButton;
    Button confirmNewAspectButton;

    boolean showCreate = false;

    RecyclerView aspects;
    private AspectListAdapter aspectListAdapter;

    Profile profile;
    InfluxDBClient client;

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);

        profile = (Profile) getIntent().getExtras().getParcelable("profile");
        Log.d(getClass().getSimpleName(), "Loaded Profile: " + profile.name);

        //Open Influx client
        client = MineSelf.getInstance().openInfluxClient(profile);

        //Setup UI
        View view = getLayoutInflater().inflate(R.layout.aspects, findViewById(R.id.content));

        newAspectLayout = view.findViewById(R.id.newAspectLayout);
        newAspectButton = view.findViewById(R.id.createAspectBtn);
        confirmNewAspectButton = view.findViewById(R.id.confirmNewAspectBtn);
        newAspectName = view.findViewById(R.id.editNewAspectName);
        newAspectValue = view.findViewById(R.id.editNewAspectValue);

        //Auto-clear on focus for new aspect name and value
        newAspectName.setOnFocusChangeListener(new MineSelf.AutoClearingEditText(newAspectName)::onFocusChange);
        newAspectValue.setOnFocusChangeListener(new MineSelf.AutoClearingEditText(newAspectValue)::onFocusChange);

        //Set up aspects recycler
        aspectListAdapter = aspectListAdapter!=null?aspectListAdapter:new AspectListAdapter(profile);
        aspects = (RecyclerView)view.findViewById(R.id.aspectList);
        aspects.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        aspects.setAdapter(aspectListAdapter);

        setContentView(view);

        refreshAspects();

    }

    private void refreshAspects(){
        //Get Aspects
        CompletableFuture.supplyAsync(()->Influx.getAspects(client, profile.name, profile.name))
                .whenComplete((results, throwable) -> {
                    aspectListAdapter.setAspects(results);
                    Log.d(getClass().getSimpleName(),"Loaded Aspects from InfluxDB");
                });
    }

    public void toSplash(View view){
        Intent toSplash = new Intent(getApplicationContext(), SplashActivity.class);
        startActivity(toSplash);
    }

    //Hide/cancel aspect create UI
    public void hideNewAspectUI(View view){
        Log.d(getClass().getSimpleName(), "Hiding create aspect UI");
        this.showCreate = false;
        newAspectLayout.setVisibility(View.GONE);
        newAspectButton.setText(R.string.add_button_text);
        newAspectButton.setOnClickListener(this::showNewAspectUI);
        //Clear newAspect fields
        newAspectName.setText(R.string.aspects_new_aspect_name_placeholder);
        newAspectValue.setText(R.string.aspects_new_aspect_value_placeholder);

    }

    //Show aspect create UI
    public void showNewAspectUI(View view){
        Log.d(getClass().getSimpleName(), "Showing create aspect UI!");
        this.showCreate = true;
        newAspectLayout.setVisibility(View.VISIBLE);
        newAspectButton.setText(R.string.cancel_button_text);
        newAspectButton.setOnClickListener(this::hideNewAspectUI);

    }

    //Create aspect and hide aspect create UI
    public void createAspect(View v){

        Aspect aspect = new Aspect();
        aspect.name = newAspectName.getText().toString();
        aspect.value = Integer.parseInt(newAspectValue.getText().toString());
        aspect.delta = 0;
        aspect.lastUpdate = Date.from(Instant.now());

        aspectListAdapter.addAspect(aspect);

        //Insert initial point into influxdb
        CompletableFuture
                .runAsync(()->Influx.insertPoint(client,profile.name, profile.name,aspect.produce("Auto-generated initial point")))
                .handle((unused, throwable) -> Log.d(getClass().getSimpleName(), "Inserted initial point for " + aspect.name));

        hideNewAspectUI(v);

    }
}
