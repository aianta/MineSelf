package ca.mineself.model;

import android.util.Log;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ca.mineself.exceptions.InvalidTimestamp;
import ca.mineself.exceptions.MissingParentAspect;

public class Entry implements Comparable<Entry>{
    AspectV2 parent;
    Instant timestamp;
    int value;
    Set<Tag> tags;

    private Entry(Builder builder){
        this.parent = builder.parent;
        this.timestamp = builder.timestamp;
        this.value = builder.value;
        this.tags = builder.tags;
    }

    public boolean equals(Object o){
        //Must be an entry to be equal
        if(!(o instanceof Entry)){
            return false;
        }
        Entry otherEntry = (Entry) o;
        return this.timestamp.equals(otherEntry.timestamp);
    }

    /**
     * Timestamp of the entry is used for the hashcode.
     * @return Entry.timestamp.hashCode()
     */
    public int hashCode(){
        return timestamp.hashCode();
    }

    @Override
    public int compareTo(Entry otherEntry) {
        long diff = this.timestamp.toEpochMilli() - otherEntry.timestamp.toEpochMilli();
        if( diff < 0){ //This entry happened before the otherEntry
            return -1;
        }

        if(diff > 0){ //This entry happened after the otherEntry
            return 1;
        }

        //This entry happened at the same time as the otherEntry
        return 0;
    }

    public static class Builder{
        private final int value;
        private AspectV2 parent;
        private Instant timestamp;
        private Set<Tag> tags;

        public Builder(int value){
            this.value = value;
            this.timestamp = Instant.now();
        }

        public void parent(AspectV2 parent){
            this.parent = parent;
            tags(parent.getTagKeys());
        }

        /**
         * Initialize tag set for this entry by creating
         * an empty tag for each tag string.
         */
        public void tags(String [] tags){
            this.tags = new HashSet<>();
            Arrays.stream(parent.getTagKeys())
                    .map(key->new Tag(key))
                    .forEach(this.tags::add);

        }

        public void timestamp(Instant instant){
            this.timestamp = instant;
        }

        private void validate(Entry entry) throws MissingParentAspect, InvalidTimestamp {
            if(entry.parent == null){
                throw new MissingParentAspect();
            }
            if(entry.timestamp.toEpochMilli() > Instant.now().toEpochMilli()){
                throw new InvalidTimestamp("Entry timestamp cannot be in the future!");
            }
        }

        public Entry build() throws InvalidTimestamp, MissingParentAspect {
            Entry entry = new Entry(this);
            validate(entry);
            return entry;
        }
    }
}
