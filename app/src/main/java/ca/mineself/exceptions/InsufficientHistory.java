package ca.mineself.exceptions;

/**
 * Thrown when trying to compute a value for an aspect that requires
 * more entries than currently exist to compute.
 *
 * IE: getting the delta of an aspect before two entries exist.
 */
public class InsufficientHistory extends Exception{

    public InsufficientHistory(String msg){
        super(msg);
    }
}
