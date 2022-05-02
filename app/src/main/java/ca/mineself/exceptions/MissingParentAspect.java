package ca.mineself.exceptions;

public class MissingParentAspect extends Exception{

    public String getMessage(){
        return "Cannot create an aspect entry without a parent aspect.";
    }

}
