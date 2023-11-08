package test;

import loader.AdaptiveClassCodeGenerator;
import loader.ExtensionLoader;

public class Main {
    public static void main(String[] args) {
        testGenerateCode();
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

    private static void testGenerateCode(){
        System.out.println(new AdaptiveClassCodeGenerator(Animal.class).generateCode());
    }

}
