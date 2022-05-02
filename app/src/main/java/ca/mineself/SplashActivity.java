package ca.mineself;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ca.mineself.R;
import ca.mineself.adapters.ProfileListAdapter;
import ca.mineself.model.Profile;

public class SplashActivity extends AppCompatActivity {


    RecyclerView profiles;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);



        View view = getLayoutInflater().inflate(R.layout.splash_screen, findViewById(R.id.content));

        profiles = (RecyclerView) view.findViewById(R.id.profileList);
        profiles.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        profiles.setAdapter(MineSelf.getInstance().getProfileList());

        Log.d("recycler", Integer.toString(profiles.getAdapter().getItemCount()));

        setContentView(view);

    }

    /**
     * Starts ProfileCreateActivity
     * @param view
     */
    public void createProfile(View view){

        Intent createIntent = new Intent(getApplicationContext(), ProfileCreateActivity.class);
        startActivity(createIntent);

    }



}
