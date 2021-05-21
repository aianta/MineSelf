package ca.mineself.model;

import com.influxdb.client.write.Point;

import java.util.Map;
import java.util.UUID;

public class Aspect {

    public String name;
    public int delta;
    public int value;


    /**
     * Utility method for producing the first point of the aspect.
     * Should only really be used during aspect creation.
     * @param note a note to insert with the first point
     * @return the data point representing the first record for this aspect.
     */
    public Point produce(String note){
        Point point = Point.measurement(name)
                .addField("value", value)
                .addField("delta", delta)
                .addField("note", note);

        return point;
    }

    public Point produce(int delta, String note, Map<String,String> tags){

        this.value += delta;

        Point point = Point.measurement(name)
                .addField("value", value)
                .addField("delta", delta)
                .addField("note", note)
                .addTags(tags);

        return point;
    }
}
