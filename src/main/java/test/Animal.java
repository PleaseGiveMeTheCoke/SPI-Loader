package test;

import annotation.SPI;

@SPI("cat")
public interface Animal {
    String howl();
}
