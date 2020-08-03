package ca.mineself;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import ca.mineself.model.ActionEntry;
import ca.mineself.model.MetricEntry;
import ca.mineself.model.dao.ActionEntryDAO;
import ca.mineself.model.dao.MetricEntryDAO;

@Database(entities = {ActionEntry.class, MetricEntry.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class LocalDatabase extends RoomDatabase {
    public abstract ActionEntryDAO actionEntryDAO();
    public abstract MetricEntryDAO metricEntryDAO();
}
