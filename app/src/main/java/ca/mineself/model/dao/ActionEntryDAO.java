package ca.mineself.model.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.UUID;

import ca.mineself.model.ActionEntry;

@Dao
public interface ActionEntryDAO {

    @Query("SELECT * FROM actionentry")
    List<ActionEntry> getAll();

    @Query("SELECT * FROM actionentry WHERE id IN (:actionEntryIds)")
    List<ActionEntry> loadAllByIds(Long[] actionEntryIds);


    @Update
    void updateEntries(ActionEntry...entries);

    @Insert
    void insertAll(ActionEntry... entries);

    @Delete
    void delete(ActionEntry user);


}
