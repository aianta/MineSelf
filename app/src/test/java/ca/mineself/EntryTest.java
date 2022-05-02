package ca.mineself;



import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ca.mineself.exceptions.InsufficientHistory;
import ca.mineself.exceptions.InvalidTimestamp;
import ca.mineself.exceptions.MissingParentAspect;
import ca.mineself.exceptions.TagNotFound;
import ca.mineself.model.AspectV2;
import ca.mineself.model.Entry;



public class EntryTest {

    private static final int TEST_VALUE = 100;
    private static final String TEST_NOTE = "A first entry";
    private static final String TEST_TAG = "Activity";
    private static final String TEST_TAG_VALUE = "unit testing";


    @Test
    public void createEntry() throws InvalidTimestamp, MissingParentAspect, InsufficientHistory, TagNotFound {

        AspectV2 aspect = new AspectV2.Builder("Mood")
                .addTag(TEST_TAG)
                .build();

        Entry entry = new Entry.Builder(TEST_VALUE)
                .note(TEST_NOTE)
                .parent(aspect)
                .build();

        assertEquals(100, entry.getValue());
        assertEquals(aspect, entry.getParent());
        assertEquals(TEST_NOTE, entry.getNote());
        assertNotNull(entry.getTimestamp());

        Entry nextEntry = new Entry.Builder(TEST_VALUE)
                .parent(aspect)
                .build();

        assertNotEquals(entry, nextEntry);
        assertEquals(-1, entry.compareTo(nextEntry));
        assertEquals(1, nextEntry.compareTo(entry));

        assertDoesNotThrow(()->entry.getTag(TEST_TAG));

        entry.setTag(TEST_TAG, TEST_TAG_VALUE);
        assertEquals(TEST_TAG_VALUE,entry.getTag(TEST_TAG).value);
        entry.clearTag(TEST_TAG);
        assertNull(entry.getTag(TEST_TAG).value);

        assertThrows(TagNotFound.class, ()->entry.getTag("woah"));
    }

}
