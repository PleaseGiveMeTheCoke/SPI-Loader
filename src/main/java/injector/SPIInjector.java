package injector;

import annotation.Adaptive;
import annotation.SPI;
import loader.ExtensionLoader;

import java.lang.reflect.Method;

@Adaptive
public class SPIInjector implements Injector{

    @Override
    public void inject(Object o) {
        if(o == null){
            return;
        }
        try {
            for (Method method : o.getClass().getMethods()) {
                if(method.getName().startsWith("set") && method.getParameterTypes().length == 1){
                    Class<?> setType = method.getParameterTypes()[0];
                    if(setType.isInterface()){
                        ExtensionLoader<?> extensionLoader = ExtensionLoader.getExtensionLoader(setType);
                        if(extensionLoader != null){
                            SPI spi = method.getAnnotation(SPI.class);
                            if(spi != null && !spi.value().equals("")){
                                Object extension = extensionLoader.getExtension(spi.value());
                                method.invoke(o, extension);
                            }else{
                                Object adaptiveExtension = extensionLoader.getAdaptiveExtension();
                                method.invoke(o, adaptiveExtension);
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
