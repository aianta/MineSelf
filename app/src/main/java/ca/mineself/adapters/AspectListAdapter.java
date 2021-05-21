package ca.mineself.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ca.mineself.R;
import ca.mineself.model.Aspect;


public class AspectListAdapter extends RecyclerView.Adapter<AspectListAdapter.AspectHolder> {

    private List<Aspect> aspects = new ArrayList<>();

    protected class AspectHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        Aspect aspect;
        TextView nameLabel;
        TextView deltaLabel;
        TextView valueLabel;

        public AspectHolder(View view){
            super(view);
            nameLabel = view.findViewById(R.id.aspectNameLabel);
            deltaLabel = view.findViewById(R.id.aspectDeltaLabel);
            valueLabel = view.findViewById(R.id.aspectValueLabel);
            view.setOnClickListener(this::onClick);
        }

        public void onClick(View view){
            //TODO
        }
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
        holder.deltaLabel.setText(Integer.toString(aspects.get(position).delta));
        holder.valueLabel.setText(Integer.toString(aspects.get(position).value));
    }

    @Override
    public int getItemCount() {
        return aspects.size();
    }
}
