package ca.mineself.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Tag {
    public String key;
    public String value;
    public List<String> suggestions = new ArrayList<>();

    public Tag(){};

    public Tag(String key){
        this.key = key;
    }

    public void clear(){
        this.value = null;
    }

    public boolean isEmpty(){
        return value == null;
    }

    public void print(){
        String result = key + " - " + (value!=null?value:"null ") + " [";
        for(String suggestion:suggestions){
            result+=suggestion + ",";
        }
        result +="]";
        Log.d("Tag", result );
    }

    public boolean equals(Object o){
        if(!(o instanceof Tag)){
            return false;
        }

        Tag otherTag = (Tag)o;
        return this.key.equals(otherTag.key);
    }

    public int hashCode(){
        return this.key.hashCode();
    }
}
