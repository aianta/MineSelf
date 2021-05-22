package ca.mineself.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.influxdb.client.write.Point;

import java.util.Map;
import java.util.UUID;

public class Aspect implements Parcelable {

    public String name;
    public long delta;
    public long value;

    public Aspect(){}

    public Aspect(Parcel in){
        String[] data = new String[3];
        in.readStringArray(data);

        this.name = data[0];
        this.delta = Long.parseLong(data[1]);
        this.value = Long.parseLong(data[2]);
    }

    public int describeContents(){return 0;}

    public void writeToParcel(Parcel parcel, int i){
        parcel.writeStringArray(new String[]{
                this.name, Long.toString(this.delta), Long.toString(this.value)
        });
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){

        @Override
        public Aspect createFromParcel(Parcel parcel) {
            return new Aspect(parcel);
        }

        @Override
        public Aspect[] newArray(int size) {
            return new Aspect[size];
        }
    };

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

    public Point produce(long delta, String note, Map<String,String> tags){

        this.value += delta;

        Point point = Point.measurement(name)
                .addField("value", value)
                .addField("delta", delta)
                .addField("note", note)
                .addTags(tags);

        return point;
    }
}
