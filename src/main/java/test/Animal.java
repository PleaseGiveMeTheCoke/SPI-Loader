package test;

import annotation.Adaptive;
import annotation.SPI;

@SPI("cat")
public interface Animal {

    @Adaptive(value = "cat")
    String howl();

    String eat();

    @Adaptive(index = 0)
    String owner(String name);
}
