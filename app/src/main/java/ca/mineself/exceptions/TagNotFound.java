package ca.mineself.exceptions;

/**
 * Thrown when one attempts to set a tag that doesn't exist in
 * the relevant tag set.
 */
public class TagNotFound extends Exception{
    String tagKey;
    String [] tagSet;
    public TagNotFound(String tagKey, String[] tagSet){
        this.tagKey = tagKey;
        this.tagSet = tagSet;
    }

    public String getMessage(){
        return "Could not find " + tagKey + " in tag set " + tagSet;
    }
}
