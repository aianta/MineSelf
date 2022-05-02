package ca.mineself;

import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;

import java.util.concurrent.CompletableFuture;

import ca.mineself.ui.adapters.ProfileListAdapter;
import ca.mineself.model.Profile;

/**
 * Singleton for shared data structures
 */
public class MineSelf {

    private static MineSelf instance;

    //Influx Client
    private InfluxDBClient client;

    //Application Adapters
    private ProfileListAdapter profileList = new ProfileListAdapter();

    /**
     * Utility Class for edit texts to make them auto clear their placeholders when
     * tapped. Replaces the placeholders if nothing is entered.
     */
    public static class AutoClearingEditText implements View.OnFocusChangeListener {
        String placeholder;
        EditText editText;

        public AutoClearingEditText(EditText editText){
            this.editText = editText;
        }

        @Override
        public void onFocusChange(View view, boolean focused) {
            if(focused){
                this.placeholder = this.editText.getText().toString();
                this.editText.setText("");
            }else{
                if(editText.getText().toString().isEmpty()){
                    this.editText.setText(placeholder);
                }
            }
        }
    }

    private MineSelf(){

    }

    /**
     * Opens a client for a given profile. Creates org and bucket if they don't exist.
     * @param profile
     * @return
     */
    public InfluxDBClient openInfluxClient(Profile profile){


        client = InfluxDBClientFactory.create(profile.host, profile.token.toCharArray());

        if(profile.orgId == null){
            CompletableFuture.supplyAsync(()->Influx.findOrCreateOrg(client, profile.name))
                    .thenApply(organization -> {
                        profile.orgId = organization.getId();
                        return profile;
                    })
                    .thenApplyAsync(p->Influx.findOrCreateBucket(client,p.name, p.orgId))
                    .thenApply(bucket -> {
                        profile.bucketId = bucket.getId();
                        return profile;
                    }).handle((unused, throwable) -> Log.d("Influx", "Done!")
            );
        }else{
            CompletableFuture.supplyAsync(()->Influx.findOrCreateBucket(client,profile.name, profile.orgId))
                    .thenApply(bucket -> {
                        profile.bucketId = bucket.getId();
                        return profile;
                    }).handle((unused, throwable) -> Log.d("Influx", "Done!"));
        }



        return client;
    }


    public static MineSelf getInstance(){
        if (instance == null){
            instance = new MineSelf();
        }
        return instance;
    }

    public ProfileListAdapter getProfileList() {
        return profileList;
    }
}
