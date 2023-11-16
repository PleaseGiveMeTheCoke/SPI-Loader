package test;

import annotation.SPI;

public class Dog implements Animal{

    private Food food;

    @Override
    public String howl() {
        return "wang";
    }

    @Override
    public String eat() {
        return "dog eat " + food.getName();
    }

    @Override
    public String owner(String name) {
        return "dog owner";
    }

    @SPI("bone")
    public void setFood(Food food){
        this.food = food;
    }
}
