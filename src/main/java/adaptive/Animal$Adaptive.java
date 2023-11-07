package adaptive;

import loader.ExtensionLoader;
import test.Animal;

public class Animal$Adaptive implements Animal {

    @Override
    public String howl(Object arg1) {
        if(arg1 == null){
            throw new RuntimeException("null arg of extension name");
        }

        if(!(arg1 instanceof String)){
            throw new RuntimeException("unsupported type of arg: not String");
        }

        Object extension = ExtensionLoader.getExtensionLoader(Animal.class).getExtension((String)arg1);

        return ((Animal)extension).howl(arg1);
    }
}
