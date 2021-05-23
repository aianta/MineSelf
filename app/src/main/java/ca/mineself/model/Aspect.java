package ca.mineself.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.influxdb.client.write.Point;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import ca.mineself.R;

public class Aspect implements Parcelable {

    public String name;
    public long delta;
    public long value;
    public Date lastUpdate;

    public Aspect(){}

    public Aspect(Parcel in){
        String[] data = new String[4];
        in.readStringArray(data);

        this.name = data[0];
        this.delta = Long.parseLong(data[1]);
        this.value = Long.parseLong(data[2]);
        this.lastUpdate = Date.from(Instant.ofEpochMilli(Long.parseLong(data[3])));
    }

    public int describeContents(){return 0;}

    public void writeToParcel(Parcel parcel, int i){
        parcel.writeStringArray(new String[]{
                this.name,
                Long.toString(this.delta),
                Long.toString(this.value),
                Long.toString(this.lastUpdate.getTime())
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
                .addTags(tags);

        if(!note.equals(R.string.aspect_update_note_placeholder) && !note.isEmpty()){
            point.addField("note", note);
        }

        return point;
    }

    public String getLastUpdatedString(){

        Date now = Date.from(Instant.now());
        Long diff = now.getTime() - this.lastUpdate.getTime();

        Long diffSeconds = (diff/1000);
        Long diffMinutes = (diff/(1000*60));
        Long diffHours = (diff/(1000*60*60));
        Long diffDays = (diff/(1000*60*60*24));

        if(diffSeconds < 60){
            return "last updated " + diffSeconds + "s ago";
        }

        if(diffMinutes < 60){
            return "last updated " + diffMinutes + "m ago";
        }

        if(diffHours < 24){
            return "last updated " + diffHours + "h ago";
        }

        return "last updated " + diffDays + " days ago";
    }
}
