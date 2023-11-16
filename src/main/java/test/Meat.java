package test;

import annotation.Adaptive;

@Adaptive
public class Meat implements Food{
    @Override
    public String getName() {
        return "meat";
    }
}
