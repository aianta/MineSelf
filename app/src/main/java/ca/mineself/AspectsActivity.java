package ca.mineself;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import ca.mineself.model.Profile;

public class AspectsActivity extends AppCompatActivity {

    Profile profile;

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);

        profile = (Profile) getIntent().getExtras().getParcelable("profile");
        Log.d(getClass().getSimpleName(), "Loaded Profile: " + profile.name);

        View view = getLayoutInflater().inflate(R.layout.aspects, findViewById(R.id.content));
        setContentView(view);
    }

    public void toSplash(View view){
        Intent toSplash = new Intent(getApplicationContext(), SplashActivity.class);
        startActivity(toSplash);
    }
}
