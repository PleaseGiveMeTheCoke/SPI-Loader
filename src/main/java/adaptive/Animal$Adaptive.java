package adaptive;
import loader.ExtensionLoader;
import test.Animal;
public class Animal$Adaptive implements Animal {
    public java.lang.String howl(){
    Object extension = ExtensionLoader.getExtensionLoader(test.Animal.class).getExtension("cat");
    if(extension == null){
        throw new RuntimeException("no such extension of name given by value");
    }
    return ((test.Animal)extension).howl();
}
    public java.lang.String owner(java.lang.String arg0){
        if(arg0 == null || arg0.equals("")){
            throw new RuntimeException("null arg of extension name");
        }
        Object extension = ExtensionLoader.getExtensionLoader(test.Animal.class).getExtension(arg0);
        if(extension == null){
            throw new RuntimeException("no such extension of name given by index");
        }
        return ((test.Animal)extension).owner(arg0);
    }
    public java.lang.String eat(){
        throw new RuntimeException("method not support adaptive");
    }
}
