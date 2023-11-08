package test;

public class Cat implements Animal{
    @Override
    public String howl() {
        return "miao";
    }

    @Override
    public String eat() {
        return "fish";
    }

    @Override
    public String owner(String name) {
        return "cat owner";
    }
}
