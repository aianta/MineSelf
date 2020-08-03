package ca.mineself.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.UUID;

/**
 * @Author Alexandru Ianta
 *
 * An action entry describes in one word, what the user is doing at a given point
 * in time.
 */
@Entity
public class ActionEntry {

    @PrimaryKey(autoGenerate = true)
    long id;
    @ColumnInfo(name="start_time")
    Date startTime;
    @ColumnInfo(name = "end_time")
    Date endTime;
    @ColumnInfo(name="name")
    String name;

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
}
