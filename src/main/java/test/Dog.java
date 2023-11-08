package test;

public class Dog implements Animal{
    @Override
    public String howl() {
        return "wang";
    }

    @Override
    public String eat() {
        return "bone";
    }

    @Override
    public String owner(String name) {
        return "dog owner";
    }
}
