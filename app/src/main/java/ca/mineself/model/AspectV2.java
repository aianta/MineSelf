package ca.mineself.model;

import java.time.Instant;
import java.util.Deque;
import java.util.Iterator;


import ca.mineself.exceptions.InsufficientHistory;
import ca.mineself.persistence.AspectV2Listener;

public class AspectV2 {

    Instant created; //Instant when the aspect was created
    String name;
    Deque<Entry> entries;
    AspectV2Listener listener;

    /**
     * Like a schema for the aspect.
     * Contains the tags which entries of this aspect must have.
     */
    String [] tagKeys;


    public int lastValue() throws InsufficientHistory {
        if(entries.isEmpty()){
            throw new InsufficientHistory("Need at least 1 entry to compute last value");
        }
        return entries.peekFirst().value;
    }

    /**
     *
     * @return the timestamp of the last entry for this aspect.
     */
    public Instant lastUpdate() throws InsufficientHistory {
        if(entries.isEmpty()){
            throw new InsufficientHistory("Need at least 1 entry to compute last updated timestamp");
        }
        return entries.getLast().timestamp;
    }

    /**
     *
     * @return the change in the aspect's value.
     * @throws InsufficientHistory thrown if there are insufficient entires to compute delta.
     */
    public int delta() throws InsufficientHistory {
        if(entries.size() < 2){
            throw new InsufficientHistory("Need at least 2 entries to compute delta.");
        }

        Iterator<Entry> it = entries.iterator();
        Entry latest = it.next();
        Entry secondLatest = it.next();

        return latest.value - secondLatest.value;
    }

    /**
     * Returns the tags which entries of this aspect must have.
     * @return
     */
    public String [] getTagKeys(){
        return tagKeys;
    }

    public void update(Entry e){
        listener.onBeforeUpdate(this, e);
        entries.push(e);
        listener.onAfterUpdate(this, entries);
    }

    void setAspectV2Listener(AspectV2Listener listener){
        this.listener = listener;
    }

}
