package ca.mineself.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;


import androidx.appcompat.app.AppCompatActivity;

import ca.mineself.R;
import ca.mineself.ui.fragments.AspectUpdate;
import ca.mineself.ui.fragments.AspectView;
import ca.mineself.ui.listeners.AspectListener;
import ca.mineself.model.Aspect;
import ca.mineself.model.Profile;

/**
 * The aspect activity provides read and edit/update functionality for an aspect.
 */
public class AspectActivity extends AppCompatActivity implements AspectListener {

    Profile profile;
    Aspect aspect;


    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);

        profile = (Profile) getIntent().getExtras().getParcelable("profile");
        Log.d(getClass().getSimpleName(), "Loaded Profile: " + profile.name);

        aspect = (Aspect) getIntent().getExtras().getParcelable("aspect");
        Log.d(getClass().getSimpleName(), "Loaded Aspect: " + aspect.name);

        //No saved state
        if(bundle == null){
            Bundle aspectBundle = new Bundle();
            aspectBundle.putParcelable("aspect", aspect);
            aspectBundle.putParcelable("profile", profile);

            AspectView aspectView = new AspectView();
            aspectView.setArguments(aspectBundle);

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.aspectFragmentContainer, aspectView)
                    .commit();
        }

        View view = getLayoutInflater().inflate(R.layout.aspect, findViewById(R.id.content));
        setContentView(view);
    }

    @Override
    public void switchToAspectUpdate(long delta) {

        Bundle bundle = new Bundle();
        bundle.putParcelable("aspect", aspect);
        bundle.putParcelable("profile", profile);
        bundle.putLong("newDelta", delta);

        AspectUpdate fragment = new AspectUpdate();
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.aspectFragmentContainer, fragment)
                .commit();
    }
}
