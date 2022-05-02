package ca.mineself;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import ca.mineself.model.Profile;

public class ProfileCreateActivity extends AppCompatActivity {

    EditText name;
    EditText host;
    EditText token;
    EditText orgId;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        View view = getLayoutInflater().inflate(R.layout.profile_create, findViewById(R.id.content));

        //Register UI components
        name = view.findViewById(R.id.editProfileName);
        host = view.findViewById(R.id.editHost);
        orgId = view.findViewById(R.id.editOrg);
        token = view.findViewById(R.id.editToken);



        //Auto-Clear placeholders
        name.setOnFocusChangeListener(new MineSelf.AutoClearingEditText(name)::onFocusChange);
        host.setOnFocusChangeListener(new MineSelf.AutoClearingEditText(host)::onFocusChange);
        token.setOnFocusChangeListener(new MineSelf.AutoClearingEditText(token)::onFocusChange);
        orgId.setOnFocusChangeListener(new MineSelf.AutoClearingEditText(orgId)::onFocusChange);

        setContentView(view);
    }

    public void createProfile(View view){

        Profile profile = new Profile();
        profile.name = name.getText().toString();
        profile.host = host.getText().toString();
        profile.token = token.getText().toString();
        profile.orgId = orgId.getText().toString();

        MineSelf.getInstance().getProfileList().addProfile(profile);

        Intent openAspects = new Intent(getApplicationContext(), AspectsActivity.class);
        //Bundle created profile for Aspects Activity
        openAspects.putExtra("profile", profile);

        //Prevent going back to this create screen.
        TaskStackBuilder.create(view.getContext()).addNextIntentWithParentStack(openAspects).startActivities();

        Log.d(getClass().getSimpleName(), "Starting Aspects activity!");


    }

}
