package ca.mineself.persistence;

import java.util.Deque;
import java.util.PriorityQueue;

import ca.mineself.model.AspectV2;
import ca.mineself.model.Entry;

public interface AspectV2Listener {

    /**
     * Called before newEntry is added to the aspect's entries deque.
     * @param src the aspect being updated
     * @param newEntry the entry about to be added.
     */
    void onBeforeUpdate(AspectV2 src, Entry newEntry);

    /**
     * Called after an entry is added to the aspect's entries priority queue.
     * @param src the aspect being updated
     * @param entries the updated deque for this aspect.
     */
    void onAfterUpdate(AspectV2 src, Deque<Entry> entries);

}
