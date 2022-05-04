package ca.mineself.exceptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ca.mineself.model.Entry;
import ca.mineself.model.Tag;

public class ParentTagMismatch extends Exception{

    List<String> missingTags;
    List<String> extraTags;

    public static boolean isMismatched(Entry entry){
        List<String> parentKeys = Arrays.asList(entry.getParent().getTagKeys());
        Set<String> entryKeys = entry.getTags().stream().map(t->t.key).collect(Collectors.toSet());

        return findMissing(parentKeys, entryKeys).size() > 0 || findExtra(parentKeys, entryKeys).size() > 0;
    }

    public ParentTagMismatch(Entry entry){
        List<String> parentKeys = Arrays.asList(entry.getParent().getTagKeys());
        Set<String> entryKeys = entry.getTags().stream().map(t->t.key).collect(Collectors.toSet());

        missingTags = findMissing(parentKeys, entryKeys);
        extraTags = findExtra(parentKeys, entryKeys);

    }

    private static List<String> findMissing(List<String> parentKeys, Set<String> entryKeys){
        List<String> result = new ArrayList<>();
        //Go through the parent keys and find any tags that don't exist in the entry.
        for(String s: parentKeys){
            if(!entryKeys.contains(s)){
                result.add(s); //Add them to the list of missing tags.
            }
        }
        return result;
    }

    private static List<String> findExtra(List<String> parentKeys, Set<String> entryKeys){
        List<String> result = new ArrayList<>();
        //Go through the entry tags and find any that don't exist in the parent
        for(String s:entryKeys){
            if(!parentKeys.contains(s)){
                result.add(s);//Add them to the list of extra tags.
            }
        }
        return result;
    }

    private static String listToText(List<String> list){
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = list.listIterator();
        while (it.hasNext()){
            sb.append(it.next());
            if(it.hasNext()){
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public String getMessage(){
        StringBuilder sb = new StringBuilder();
        sb.append("Entry tags do not match parent aspect specifications. ");

        if(missingTags.size() > 0){
            sb.append("Entry is missing [");
            sb.append(listToText(missingTags));
            sb.append("].");
        }

        if(extraTags.size() > 0){
            sb.append("Entry has extra tags [");
            sb.append(listToText(extraTags));
            sb.append("].");
        }

        return sb.toString();
    }
}
