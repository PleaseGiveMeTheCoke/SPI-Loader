package loader;

import config.LoadingConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ExtensionLoader<T> {

    private Class<?> type;

    private static Map<Class<?>, ExtensionLoader<?>> loaderMap = new ConcurrentHashMap<>();

    public ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    public T getExtension(String name){
        Map<String, Class<?>> classMap = getClassMap();
        if (!classMap.containsKey(name)) {
            return null;
        }

        Class<?> aClass = classMap.get(name);
        Object o;

        // 实例化
        try {
          o = aClass.newInstance();
        } catch (Exception e) {
            return null;
        }

        return (T) o;
    }

    public Set<String> getSupportedExtensions(){
        Map<String, Class<?>> classMap = getClassMap();
        return classMap.keySet();
    }

    private Map<String, Class<?>> getClassMap(){
        String fileName = LoadingConfig.directoryPrefix + type.getName();
        URL resource = ClassLoader.getSystemClassLoader().getResource(fileName);
        return loadResource(resource);
    }

    private Map<String, Class<?>> loadResource(URL resource) {
        Map<String, Class<?>> classMap = new HashMap<>();
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

    public static ExtensionLoader<?> getExtensionLoader(Class<?> type){
        if(loaderMap.get(type) == null){
            ExtensionLoader<?> loader = new ExtensionLoader<>(type);
            loaderMap.put(type, loader);
        }

        return loaderMap.get(type);
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
