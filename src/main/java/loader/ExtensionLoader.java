package loader;

import annotation.Adaptive;
import annotation.SPI;
import config.LoadingConfig;
import injector.Injector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ExtensionLoader<T> {

    private final Class<?> type;

    private Object cachedAdaptiveInstance;

    private Injector injector;

    private static final Map<Class<?>, ExtensionLoader<?>> loaderMap = new ConcurrentHashMap<>();

    private static final Map<Class<?>, Object> objectMap = new ConcurrentHashMap<>();

    public ExtensionLoader(Class<?> type) {
        this.type = type;
        if(type == Injector.class){
            injector = null;
        }else{
            injector = ExtensionLoader.getExtensionLoader(Injector.class).getAdaptiveExtension();
        }
    }

    public T getExtension(String name){
        return getExtension(name, false);
    }

    public T getExtension(String name, boolean isSingleton){
        Map<String, Class<?>> classMap = getClassMap();
        if (!classMap.containsKey(name)) {
            return null;
        }

        Class<?> aClass = classMap.get(name);

        Object o;
        if(isSingleton){
            o = objectMap.get(aClass);
            if(o != null){
                return (T) o;
            }

            // 实例化
            try {
                o = aClass.newInstance();
                if(injector != null){
                    injector.inject(o);
                }
            } catch (Exception e) {
                return null;
            }

            objectMap.put(aClass, o);
            return (T) o;
        }

        // 实例化
        try {
          o = aClass.newInstance();
          if(injector != null){
              injector.inject(o);
          }
        } catch (Exception e) {
            return null;
        }

        return (T) o;
    }

    public T getDefaultExtension(){
        TreeMap<String, Class<?>> classMap = getClassMap();
        SPI defaultSPI = type.getAnnotation(SPI.class);
        String name = defaultSPI.value();
        if(name.equals("")){
            return getExtension(classMap.firstKey());
        }

        return getExtension(defaultSPI.value());
    }

    public T getAdaptiveExtension(){
        if(cachedAdaptiveInstance != null){
            return (T) cachedAdaptiveInstance;
        }

        cachedAdaptiveInstance = createAdaptiveInstance();
        if(injector != null){
            injector.inject(cachedAdaptiveInstance);
        }
        return (T) cachedAdaptiveInstance;
    }

    private Object createAdaptiveInstance() {
        try {
            TreeMap<String, Class<?>> classMap = getClassMap();
            for (String key : classMap.keySet()) {
                if(classMap.get(key).getAnnotation(Adaptive.class) != null){
                    return getExtension(key);
                }
            }

            String code = new AdaptiveClassCodeGenerator(type).generateCode();
            return Compiler.compile(type, code, type.getClassLoader()).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<String> getSupportedExtensions(){
        Map<String, Class<?>> classMap = getClassMap();
        return classMap.keySet();
    }

    private TreeMap<String, Class<?>> getClassMap(){
        String fileName = LoadingConfig.directoryPrefix + type.getName();
        URL resource = ClassLoader.getSystemClassLoader().getResource(fileName);
        return loadResource(resource);
    }

    private TreeMap<String, Class<?>> loadResource(URL resource) {
        TreeMap<String, Class<?>> classMap = new TreeMap<>();
        try {
            List<String> resourceContent = getResourceContent(resource);
            for(String line : resourceContent){
                String[] nameAndClass = line.split("=");
                String name = nameAndClass[0];
                String clazz = nameAndClass[1];

                classMap.put(name, Class.forName(clazz));
            }
        }catch (Exception e){
            throw new RuntimeException(e.getMessage(), e);
        }

        return classMap;
    }

    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type){
        if(loaderMap.get(type) == null){
            ExtensionLoader<T> loader = new ExtensionLoader<>(type);
            loaderMap.put(type, loader);
        }

        return (ExtensionLoader<T>) loaderMap.get(type);
    }

    private List<String> getResourceContent(URL resourceURL){
        List<String> content = new ArrayList<>();
        try(BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(resourceURL.openStream(), UTF_8))){
            String line;
            while ((line = bufferedReader.readLine()) != null){
                line = line.trim();
                if(line.length() > 0){
                    content.add(line);
                }
            }

        }catch (IOException e){
            throw new RuntimeException(e.getMessage(), e);
        }

        return content;
    }
}
