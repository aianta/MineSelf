package ca.mineself.ui.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.mineself.R;
import ca.mineself.model.Tag;

public class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.TagHolder>  {

    List<Tag> tags = new ArrayList<>();

    public TagListAdapter setTags(List<Tag> tags){
        this.tags = tags;
        Log.d(getClass().getSimpleName(), "Set tags! Tags size: " + tags.size());
        notifyDataSetChanged();
        return this;
    }

    public TagListAdapter addTag(Tag t){
        tags.add(t);
        Log.d(getClass().getSimpleName(), "Added new tag: " + t.key + " tags size: " + tags.size());
        notifyDataSetChanged();
        return this;
    }

    public TagListAdapter removeTag(String key){
        tags.remove(key);
        Log.d(getClass().getSimpleName(), "Removed tag: " + key + " tags size: " + tags.size());
        notifyDataSetChanged();
        return this;
    }

    public TagListAdapter updateTag(String key, String value){
        tags.stream().filter(tag -> tag.key.equals(key)).findFirst().get().value = value;
        Log.d(getClass().getSimpleName(), "Updated tag: " + key + " -> " + value);
        return this;
    }

    public List<Tag> getTags(){
        return tags;
    }

    public Map<String,String> getTagsAsMap(){
        return tags.stream()
                .filter(t->!t.value.isEmpty())
                .collect(
                        HashMap::new,
                        (hashMap, tag) -> hashMap.put(tag.key,tag.value),
                        (hashMap, hashMap2) -> hashMap.putAll(hashMap2)
                );
    }

    protected class TagChangeWatcher implements TextWatcher{

        TextView keyLabel;
        TagChangeWatcher(TextView keyLabel){
            this.keyLabel = keyLabel;
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(editable.toString().isEmpty()){
                return;
            }

            updateTag(keyLabel.getText().toString(), editable.toString());
        }
    }

    protected class TagHolder extends RecyclerView.ViewHolder {
        TagListAdapter adapter;
        TextView tagKeyLabel;
        AutoCompleteTextView editTagValue;
        Button deleteTagBtn;
        List<String> suggestions = new ArrayList<>();
        ArrayAdapter<String> suggestionAdapter;

        public TagHolder(View view, TagListAdapter adapter){
            super(view);
            this.adapter = adapter;


            tagKeyLabel = view.findViewById(R.id.tagKeyLabel);
            //TODO - hmm this editTagValue ending up null somehow
            editTagValue = view.findViewById(R.id.editTagValue);
            deleteTagBtn = view.findViewById(R.id.deleteTagBtn);
            deleteTagBtn.setOnClickListener(this::removeTag);

            Log.d(getClass().getSimpleName(), editTagValue.toString());

            editTagValue.addTextChangedListener(new TagChangeWatcher(tagKeyLabel));


        }

        public void updateTag(Tag t){
            editTagValue.setAdapter(new ArrayAdapter<>(editTagValue.getContext(), android.R.layout.simple_dropdown_item_1line, t.suggestions));
            editTagValue.setThreshold(1);
            t.suggestions.forEach(s->Log.d("suggestion", s));
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
        Log.d("onBindViewHolder", "getting holder @" + position);

        Tag tag = tags.get(position);
        holder.tagKeyLabel.setText(tag.key);

        //If there is a tag value set it
        if(tag.value != null && !tag.value.isEmpty()){
            holder.editTagValue.setText(tag.value);
        }

        holder.updateTag(tag);
    }

    public int getItemCount(){
        return tags.size();
    }


}
