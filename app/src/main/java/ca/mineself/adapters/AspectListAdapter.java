package ca.mineself.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ca.mineself.AspectActivity;
import ca.mineself.R;
import ca.mineself.model.Aspect;
import ca.mineself.model.Profile;


public class AspectListAdapter extends RecyclerView.Adapter<AspectListAdapter.AspectHolder> {

    private List<Aspect> aspects = new ArrayList<>();
    private Profile profile; //Associated profile

    public AspectListAdapter(Profile profile){
        this.profile = profile;
    }

    protected class AspectHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        Aspect aspect;
        Profile profile;
        TextView nameLabel;
        TextView deltaLabel;
        TextView valueLabel;
        ImageView deltaUpIcon;
        ImageView deltaDownIcon;
        ImageView deltaNeutralIcon;

        public AspectHolder(View view){
            super(view);
            nameLabel = view.findViewById(R.id.aspectNameLabel);
            deltaLabel = view.findViewById(R.id.aspectDeltaLabel);
            valueLabel = view.findViewById(R.id.aspectValueLabel);
            deltaUpIcon = view.findViewById(R.id.deltaUpIcon);
            deltaDownIcon = view.findViewById(R.id.deltaDownIcon);
            deltaNeutralIcon = view.findViewById(R.id.deltaNeutralIcon);
            view.setOnClickListener(this::onClick);
        }

        public void onClick(View view){
            Intent viewAspect = new Intent(view.getContext(), AspectActivity.class);
            viewAspect.putExtra("profile", profile);
            viewAspect.putExtra("aspect", aspect);
            view.getContext().startActivity(viewAspect);
        }
    }

    public AspectListAdapter setAspects(List<Aspect> aspects){
        this.aspects = aspects;
        notifyDataSetChanged();
        return this;
    }

    public AspectListAdapter addAspect(Aspect a){
        aspects.add(a);
        notifyDataSetChanged();
        return this;
    }


    @Override
    public AspectHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.aspect_entry, parent, false);
        AspectHolder holder = new AspectHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder( AspectHolder holder, int position) {
        if(aspects.get(position) == null){
            return;
        }


        holder.nameLabel.setText(aspects.get(position).name);
        holder.deltaLabel.setText(Long.toString(aspects.get(position).delta));
        holder.valueLabel.setText(Long.toString(aspects.get(position).value));

        //Setup delta icon
        if(aspects.get(position).delta > 0){
            holder.deltaUpIcon.setVisibility(View.VISIBLE);
        }

        if(aspects.get(position).delta < 0){
            holder.deltaDownIcon.setVisibility(View.VISIBLE);
        }

        if(aspects.get(position).delta == 0){
            holder.deltaNeutralIcon.setVisibility(View.VISIBLE);
        }

        holder.aspect = aspects.get(position);
        holder.profile = profile;
    }

    @Override
    public int getItemCount() {
        return aspects.size();
    }
}
