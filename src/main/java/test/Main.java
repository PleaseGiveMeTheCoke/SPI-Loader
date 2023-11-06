package test;

import loader.ExtensionLoader;

public class Main {
    public static void main(String[] args) {
        testSingleton();
    }

    private static void testSingleton(){
        ExtensionLoader<?> loader = ExtensionLoader.getExtensionLoader(Animal.class);
        Animal animal = (Animal)loader.getExtension("cat", true);
        System.out.println(animal);
        animal = (Animal)loader.getExtension("cat", true);
        System.out.println(animal);
        animal = (Animal)loader.getExtension("cat", false);
        System.out.println(animal);
    }

}
