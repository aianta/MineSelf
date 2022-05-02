package ca.mineself.ui.adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ca.mineself.ui.AspectsActivity;
import ca.mineself.R;
import ca.mineself.model.Profile;

public class ProfileListAdapter extends RecyclerView.Adapter<ProfileListAdapter.ProfileHolder>  {

    private List<Profile> profiles = new ArrayList<>();

    protected class ProfileHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Profile profile;
        TextView name;

        public ProfileHolder(View view) {
            super(view);
            name = view.findViewById(R.id.profileLabel);
            view.setOnClickListener(this::onClick);
        }

        /**
         * Start aspects activity for this profile
         * @param v
         */
        @Override
        public void onClick(View v){
            //Bundle profile for Aspects Activity
            Intent openAspects = new Intent(v.getContext(), AspectsActivity.class);
            openAspects.putExtra("profile", profile);
            v.getContext().startActivity(openAspects);
        }
    }

    public ProfileListAdapter addProfile(Profile p){
        profiles.add(p);
        Log.d(getClass().getSimpleName(), "Added profile " + p.name + " profiles size: " + profiles.size());
        notifyDataSetChanged(); //CRITICAL recycler won't update without this.
        return this;
    }


    @Override
    public ProfileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(getClass().getSimpleName(),"Creating view holder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_entry, parent, false);
        ProfileHolder holder = new ProfileHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ProfileHolder holder, int position) {
        holder.name.setText(profiles.get(position).name);
        holder.profile = profiles.get(position);

        Log.d(getClass().getSimpleName(), "profile @" + position + "-" + profiles.get(position).name);
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }
}
