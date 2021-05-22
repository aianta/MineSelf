package ca.mineself.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Profile implements Parcelable {
    public String name;
    public String host;
    public String token;
    public String orgId;
    public String bucketId;

    public Profile(){};

    public Profile(Parcel in){
        String[] data = new String[5];
        in.readStringArray(data);

        //Order needs to be the same as in writeToParcel
        this.name = data[0];
        this.host = data[1];
        this.token = data[2];
        this.orgId = data[3];
        this.bucketId = data[4];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{
                this.name,this.host,this.token,this.orgId,this.bucketId
        });
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        @Override
        public Profile createFromParcel(Parcel parcel) {
            return new Profile(parcel);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }

    };
}
