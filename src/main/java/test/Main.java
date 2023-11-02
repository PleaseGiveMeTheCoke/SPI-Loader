package test;

import loader.ExtensionLoader;

public class Main {
    public static void main(String[] args) {
        ExtensionLoader<?> loader = ExtensionLoader.getExtensionLoader(Animal.class);
        System.out.println(loader.getSupportedExtensions());
        Animal animal = (Animal)loader.getExtension("cat");
        System.out.println(animal.howl());
    }
}
