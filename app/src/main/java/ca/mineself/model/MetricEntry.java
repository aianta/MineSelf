package ca.mineself.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.UUID;

/**
 * @Author Alexandru Ianta
 * A metric entry describes the value of a user metric for a given interval of time.
 */
@Entity
public class MetricEntry {

    @PrimaryKey(autoGenerate = true)
    long id;

    @ColumnInfo(name="start_time")
    Date startTime;

    @ColumnInfo(name="end_time")
    Date endTime;

    @ColumnInfo(name="name")
    String name;

    @ColumnInfo(name = "metric_value")
    int value;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
