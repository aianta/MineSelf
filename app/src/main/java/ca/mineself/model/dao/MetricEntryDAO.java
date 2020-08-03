package ca.mineself.model.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.UUID;

import ca.mineself.model.MetricEntry;


@Dao
public interface MetricEntryDAO {

    @Query("SELECT * FROM metricentry ORDER BY start_time DESC LIMIT 1")
    MetricEntry getLast();

    @Query("SELECT * FROM metricentry")
    List<MetricEntry> getAll();

    @Query("SELECT * FROM metricentry WHERE id IN (:metricEntryIds)")
    List<MetricEntry> loadAllByIds(Long[] metricEntryIds);

    @Update
    void updateEntries(MetricEntry...entries);

    @Insert
    void insertAll(MetricEntry... entries);

    @Delete
    void delete(MetricEntry entry);

}
