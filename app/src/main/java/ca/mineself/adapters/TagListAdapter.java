package ca.mineself.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import ca.mineself.R;

public class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.TagHolder> {

    Map<String,String> tags = new LinkedHashMap<>();

    public TagListAdapter addTag(String key){
        tags.put(key,null);
        Log.d(getClass().getSimpleName(), "Added new tag: " + key + " tags size: " + tags.size());
        notifyDataSetChanged();
        return this;
    }

    public TagListAdapter removeTag(String key){
        tags.remove(key);
        Log.d(getClass().getSimpleName(), "Removed tag: " + key + " tags size: " + tags.size());
        notifyDataSetChanged();
        return this;
    }

    public Map<String,String> getTags(){
        return tags;
    }

    protected class TagHolder extends RecyclerView.ViewHolder{
        TagListAdapter adapter;
        TextView tagKeyLabel;
        AutoCompleteTextView editTagValue;
        Button deleteTagBtn;

        public TagHolder(View view, TagListAdapter adapter){
            super(view);
            this.adapter = adapter;

            tagKeyLabel = view.findViewById(R.id.tagKeyLabel);
            editTagValue = view.findViewWithTag(R.id.editTagValue);
            deleteTagBtn = view.findViewById(R.id.deleteTagBtn);
            deleteTagBtn.setOnClickListener(this::removeTag);
        }

        public void removeTag(View view){
            adapter.removeTag(tagKeyLabel.getText().toString());
        }


    }

    public TagHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_entry, parent, false);
        TagHolder holder = new TagHolder(view, this);
        return holder;
    }

    public void onBindViewHolder(TagHolder holder, int position){
        if(tags.size() == 0){
            return;
        }

        int index = 0;
        Iterator<Map.Entry<String,String>> it = tags.entrySet().iterator();
        Map.Entry<String,String> entry = it.next();
        while (index < position){
            entry = it.next();
            Log.d("tag", entry.getKey());
            index++;
        }

        holder.tagKeyLabel.setText(entry.getKey());
        //If there is a tag value set it
        if(entry.getValue() != null && !entry.getValue().isEmpty()){
            holder.editTagValue.setText(entry.getValue());
        }


    }

    public int getItemCount(){
        return tags.size();
    }
}
