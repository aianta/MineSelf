package ca.mineself;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;


import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import ca.mineself.exceptions.InsufficientHistory;
import ca.mineself.exceptions.InvalidEntry;
import ca.mineself.exceptions.InvalidTimestamp;
import ca.mineself.exceptions.MissingAspectName;
import ca.mineself.exceptions.MissingParentAspect;
import ca.mineself.exceptions.ParentTagMismatch;
import ca.mineself.model.AspectV2;
import ca.mineself.model.Entry;
import ca.mineself.persistence.AspectV2Listener;

public class AspectV2Test {

    private static final Logger log = LoggerFactory.getLogger(AspectV2Test.class);

    private static final String ASPECT_NAME = "Mood";
    private static final String SINGLE_TAG = "Activity";
    private static final List<String> TAG_LIST = Arrays.asList(
            "TimeOfDay", "Game"
    );


    @Test
    public void createAspect() throws MissingAspectName {

        assertDoesNotThrow(()->{
            AspectV2 aspect = new AspectV2.Builder(ASPECT_NAME)
                    .build();

            assertNotNull(aspect.getName());
            assertNotNull(aspect.getCreated());
            assertFalse(aspect.getName().isEmpty());
        });
    }

    public void createAspectWithTag() throws MissingAspectName {
        AspectV2 aspect = new AspectV2.Builder(ASPECT_NAME)
                .addTag(SINGLE_TAG)
                .build();

        assertEquals(1, aspect.getTagKeys().length);
        assertEquals(SINGLE_TAG, aspect.getTagKeys()[0]);
    }

    public void createAspectWithTags() throws MissingAspectName{
        AspectV2 aspect = new AspectV2.Builder(ASPECT_NAME)
                .addTags(TAG_LIST)
                .build();
        assertEquals(TAG_LIST.size(), aspect.getTagKeys().length);

        for(String key: aspect.getTagKeys()){
            assertTrue(TAG_LIST.contains(key));
        }
    }

    @Test
    public void deltaTest() throws MissingAspectName, InvalidTimestamp, ParentTagMismatch, InvalidEntry, MissingParentAspect, InsufficientHistory {
        AspectV2 aspectV2 = new AspectV2.Builder(ASPECT_NAME)
                .build();

        Entry e1 = new Entry.Builder(10)
                .parent(aspectV2)
                .build();

        Entry e2 = new Entry.Builder(20)
                .parent(aspectV2)
                .build();

        aspectV2.update(e1);
        aspectV2.update(e2);

        assertEquals(10, aspectV2.delta());

    }

    @Test
    public void failDeltaTest() throws MissingAspectName, InvalidTimestamp, ParentTagMismatch, InvalidEntry, MissingParentAspect, InsufficientHistory {
        AspectV2 aspectV2 = new AspectV2.Builder(ASPECT_NAME)
                .build();

        Entry e1 = new Entry.Builder(10)
                .parent(aspectV2)
                .build();


        aspectV2.update(e1);

        assertThrows(InsufficientHistory.class, ()->aspectV2.delta());

    }

    @Test
    public void lastValueTest() throws MissingAspectName, InvalidTimestamp, ParentTagMismatch, InvalidEntry, MissingParentAspect, InsufficientHistory {
        AspectV2 aspectV2 = new AspectV2.Builder(ASPECT_NAME)
                .build();

        Entry e1 = new Entry.Builder(10)
                .parent(aspectV2)
                .build();

        Entry e2 = new Entry.Builder(20)
                .parent(aspectV2)
                .build();

        aspectV2.update(e1);
        aspectV2.update(e2);

        assertEquals(20, aspectV2.lastValue());

    }

    @Test
    public void lastUpdateTest() throws MissingAspectName, InvalidTimestamp, ParentTagMismatch, InvalidEntry, MissingParentAspect, InsufficientHistory {
        AspectV2 aspectV2 = new AspectV2.Builder(ASPECT_NAME)
                .build();

        Entry e1 = new Entry.Builder(10)
                .parent(aspectV2)
                .build();

        Entry e2 = new Entry.Builder(20)
                .parent(aspectV2)
                .build();

        aspectV2.update(e1);
        aspectV2.update(e2);

        assertEquals(e2.getTimestamp(), aspectV2.lastUpdate());

    }

    @Test
    public void failLastUpdateTest() throws MissingAspectName, InvalidTimestamp, ParentTagMismatch, InvalidEntry, MissingParentAspect, InsufficientHistory {
        AspectV2 aspectV2 = new AspectV2.Builder(ASPECT_NAME)
                .build();

        assertThrows(InsufficientHistory.class, ()->aspectV2.lastUpdate());

    }

    @Test
    public void failLastValueTest() throws MissingAspectName, InvalidTimestamp, ParentTagMismatch, InvalidEntry, MissingParentAspect, InsufficientHistory {
        AspectV2 aspectV2 = new AspectV2.Builder(ASPECT_NAME)
                .build();

        assertThrows(InsufficientHistory.class, ()->aspectV2.lastValue());

    }



    @Test
    public void aspectListener() throws MissingAspectName, InvalidTimestamp, ParentTagMismatch, InvalidEntry, MissingParentAspect {

        class AspectListener implements AspectV2Listener {

            @Override
            public void onBeforeUpdate(AspectV2 src, Entry newEntry) {
                assertNotNull(newEntry);
                assertNotNull(src);
               log.info(()->newEntry.toString());
            }

            @Override
            public void onAfterUpdate(AspectV2 src, Deque<Entry> entries) {
                assertNotNull(src);
                assertNotNull(entries);
                assertEquals(1, entries.size());
                log.info(()->entries.toString());
            }
        }

        AspectListener listener = new AspectListener();
        AspectV2 aspect = new AspectV2.Builder(ASPECT_NAME)
                .listener(listener)
                .build();

        Entry e = new Entry.Builder(120)
                .note("A first entry")
                .parent(aspect)
                .build();

        aspect.update(e);
    }

}
