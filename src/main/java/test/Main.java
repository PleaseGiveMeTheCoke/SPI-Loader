package test;

import loader.AdaptiveClassCodeGenerator;
import loader.Compiler;
import loader.ExtensionLoader;

public class Main {
    public static void main(String[] args) throws Exception {
        testInject();
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

    private static void testCompile() throws IllegalAccessException, InstantiationException {
        String sourceCode = new AdaptiveClassCodeGenerator(Animal.class).generateCode();
        Class<?> compile = Compiler.compile(Animal.class, sourceCode, Thread.currentThread().getContextClassLoader());
        Animal animal = (Animal)compile.newInstance();
        System.out.println(animal.howl());
        System.out.println(animal.owner("dog"));
        System.out.println(animal.owner("cat"));
    }

    private static void testAdaptive() {
        Animal animal = ExtensionLoader.getExtensionLoader(Animal.class).getAdaptiveExtension();
        System.out.println(animal.howl());
        System.out.println(animal.owner("dog"));
        System.out.println(animal.owner("cat"));
    }

    private static void testInject(){
        Animal animal = ExtensionLoader.getExtensionLoader(Animal.class).getExtension("dog");
        System.out.println(animal.eat());
    }

}
