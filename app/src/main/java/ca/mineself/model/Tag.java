package ca.mineself.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Tag {
    public String key;
    public String value;
    public List<String> suggestions = new ArrayList<>();

    public void print(){
        String result = key + " - " + (value!=null?value:"null ") + " [";
        for(String suggestion:suggestions){
            result+=suggestion + ",";
        }
        result +="]";
        Log.d("Tag", result );
    }
}
