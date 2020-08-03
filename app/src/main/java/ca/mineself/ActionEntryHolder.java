package ca.mineself;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ca.mineself.model.ActionEntry;

public class ActionEntryHolder extends RecyclerView.ViewHolder {

    public TextView textView;

    public ActionEntryHolder(TextView textView){
        super(textView);
        this.textView = textView;
    }

}
