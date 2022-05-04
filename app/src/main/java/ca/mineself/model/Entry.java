package ca.mineself.model;

import android.util.Log;

import java.sql.Date;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import ca.mineself.exceptions.InvalidEntry;
import ca.mineself.exceptions.InvalidTagValue;
import ca.mineself.exceptions.InvalidTimestamp;
import ca.mineself.exceptions.MissingParentAspect;
import ca.mineself.exceptions.ParentTagMismatch;
import ca.mineself.exceptions.TagNotFound;

public class Entry implements Comparable<Entry>{
    private AspectV2 parent;
    private Instant timestamp;
    private int value;
    private String note;
    private Set<Tag> tags;


    public Tag getTag(String key) throws TagNotFound{
        Tag result = tags.stream()
                .filter(t->t.key.equals(key))
                .findFirst()
                .orElseThrow(()->new TagNotFound(key, parent.getTagKeys()));
        return result;
    }

    public void clearTag(String key) throws TagNotFound{
        Tag t = tags.stream()
                .filter(entryTag->entryTag.key.equals(key))
                .findFirst()
                .orElseThrow(()->new TagNotFound(key, parent.getTagKeys()));
        t.clear();
    }

    public void clearTag(Tag tag) throws TagNotFound{
        clearTag(tag.key);
    }

    public void setTag(String key, String value) throws TagNotFound {
        Tag t = tags.stream()
                .filter(entryTag->entryTag.key.equals(key))
                .findFirst()
                .orElseThrow(()->new TagNotFound(key, parent.getTagKeys()));

        t.value = value;
    }

    public void setTag(Tag tag) throws TagNotFound, InvalidTagValue {

        if(tag.isEmpty()){
            throw new InvalidTagValue("Cannot set tag with null value.");
        }

        setTag(tag.key, tag.value);
    }

    private Entry(Builder builder){
        this.parent = builder.parent;
        this.timestamp = builder.timestamp;
        this.value = builder.value;
        this.tags = builder.tags;
        this.note = builder.note;
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
        private String note;

        public Builder(int value){
            this.value = value;
            this.timestamp = Instant.now();
        }

        public Builder note(String note){
            this.note = note;
            return this;
        }

        public Builder parent(AspectV2 parent){
            this.parent = parent;
            tags(parent.getTagKeys());
            return this;
        }

        /**
         * Initialize tag set for this entry by creating
         * an empty tag for each tag string.
         */
        public Builder tags(String [] tags){
            this.tags = new HashSet<>();
            Arrays.stream(parent.getTagKeys())
                    .map(key->new Tag(key))
                    .forEach(this.tags::add);
            return this;
        }

        public Builder timestamp(Instant instant){
            this.timestamp = instant;
            return this;
        }

        private void validate(Entry entry) throws MissingParentAspect, InvalidTimestamp, InvalidEntry, ParentTagMismatch {
            if(entry.parent == null){
                throw new MissingParentAspect();
            }
            if(entry.timestamp.toEpochMilli() > Instant.now().toEpochMilli()){
                throw new InvalidTimestamp("Entry timestamp cannot be in the future!");
            }
            if(ParentTagMismatch.isMismatched(entry)){
                throw new ParentTagMismatch(entry);
            }
        }

        public Entry build() throws InvalidTimestamp, MissingParentAspect, InvalidEntry, ParentTagMismatch {
            Entry entry = new Entry(this);
            validate(entry);
            return entry;
        }
    }

    public AspectV2 getParent() {
        return parent;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getValue() {
        return value;
    }

    public String getNote() {
        return note;
    }

    public Set<Tag> getTags(){
        return tags;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(Date.from(timestamp).toString());
        sb.append(" - [" + parent.getName() + "] - ");
        sb.append(value);
        sb.append(" - [");

        if(tags.size() > 0){
            Iterator<Tag> it = tags.iterator();
            while (it.hasNext()){
                Tag t = it.next();
                sb.append(t.toString());
                if(it.hasNext()){
                    sb.append(",");
                }
            }
        }else{
            sb.append("<NO TAGS>");
        }
        sb.append("] - ");

        if(note.isEmpty() || note == null){
            sb.append("<NO NOTE>");
        }else {
            sb.append(note);
        }

        return sb.toString();
    }
}
