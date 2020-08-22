package ca.mineself.model.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import ca.mineself.model.MetricEntry;


@Dao
public interface MetricEntryDAO {

    /** Get the metric entry for a particular time
     *
     * @param target
     * @return
     */
    @Query("SELECT * FROM metricentry WHERE start_time >= :target ORDER BY start_time ASC LIMIT 1")
    MetricEntry selectAt(Date target);

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
