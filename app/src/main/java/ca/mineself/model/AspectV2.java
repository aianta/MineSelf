package ca.mineself.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


import ca.mineself.exceptions.InsufficientHistory;
import ca.mineself.exceptions.InvalidEntry;
import ca.mineself.exceptions.MissingAspectName;
import ca.mineself.persistence.AspectV2Listener;

public class AspectV2 {

    private Instant created; //Instant when the aspect was created
    private String name;
    private Deque<Entry> entries;
    private AspectV2Listener listener;

    /**
     * Like a schema for the aspect.
     * Contains the tags which entries of this aspect must have.
     */
    private final String [] tagKeys;

    private AspectV2(Builder builder){
        this.name = builder.name;
        this.created = builder.timestamp;
        this.tagKeys = builder.tagKeys();
        this.listener = builder.listener;
        this.entries = builder.entries;
    }


    public String getName(){
        return name;
    }

    public Instant getCreated(){
        return created;
    }

    public int lastValue() throws InsufficientHistory {
        if(entries.isEmpty()){
            throw new InsufficientHistory("Need at least 1 entry to compute last value");
        }
        return entries.peekFirst().getValue();
    }

    /**
     *
     * @return the timestamp of the last entry for this aspect.
     */
    public Instant lastUpdate() throws InsufficientHistory {
        if(entries.isEmpty()){
            throw new InsufficientHistory("Need at least 1 entry to compute last updated timestamp");
        }
        return entries.getLast().getTimestamp();
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

        return latest.getValue() - secondLatest.getValue();
    }

    /**
     * Returns the tags which entries of this aspect must have.
     * @return
     */
    public String [] getTagKeys(){
        return tagKeys;
    }

    public void update(Entry e) throws InvalidEntry {
        if(!e.getParent().equals(this)){
            throw new InvalidEntry("Cannot add entry because this aspect is not its parent.");
        }

        if(listener != null){
            listener.onBeforeUpdate(this, e);
        }

        entries.push(e);

        if(listener != null){
            listener.onAfterUpdate(this, entries);
        }
    }

    void setAspectV2Listener(AspectV2Listener listener){
        this.listener = listener;
    }

    public static class Builder{
        private final String name;
        private final Instant timestamp;
        private List<String> tags = new ArrayList<>();
        private Deque<Entry> entries = new LinkedList<>();
        private AspectV2Listener listener;

        public Builder(String name){
            this.name = name;
            this.timestamp = Instant.now();
        }


        public Builder addTags(List<String> tags){
            this.tags.addAll(tags);
            return this;
        }

        public Builder addTag(String key){
            tags.add(key);
            return this;
        }

        public Builder listener(AspectV2Listener listener){
            this.listener = listener;
            return this;
        }

        private void validate(AspectV2 aspect) throws MissingAspectName {
            if(aspect.getName() == null || aspect.getName().isEmpty()){
                throw new MissingAspectName("Aspects must have a name!");
            }
        }

        private String [] tagKeys(){
            String [] result = new String[tags.size()];
            return tags.toArray(result);
        }

        public AspectV2 build() throws MissingAspectName {
            AspectV2 result = new AspectV2(this);
            validate(result);
            return result;
        }

    }
}
