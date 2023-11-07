package loader;

import java.lang.reflect.Method;

public class AdaptiveClassCodeGenerator {

    private final Class<?> type;

    public AdaptiveClassCodeGenerator(Class<?> type){
        this.type = type;
    }

    public String generateCode(){
        StringBuilder code = new StringBuilder();
        code.append(generatePackageInfo());
        code.append(generateImports());
        code.append(generateClassInfo());

        Method[] methods = type.getMethods();

        for(Method method : methods){
            code.append(generateMethodCode(method));
        }

        code.append("}");

        return code.toString();
    }

    private String generateMethodCode(Method method) {
        StringBuilder methodInfo = new StringBuilder(String.format("public %s %s(", method.getReturnType().getName()
                , method.getName()));

        Class<?>[] parameterTypes = method.getParameterTypes();

        int i = 0;
        for (Class<?> parameterType : parameterTypes) {
            methodInfo.append(parameterType.getName()).append(" ").append("arg").append(i).append(",");
        }

        methodInfo.deleteCharAt(methodInfo.length() - 1);
        methodInfo.append("){");
        methodInfo.append(generateMethodContent(method));
        methodInfo.append("}");

        return methodInfo.toString();
    }

    private String generateMethodContent(Method method) {
        //无注解抛出异常
        //有注解，读取index
        //如果index == -1，生成代码，获取SPI注解中指定的扩展
        //如果index != -1 并且下标位置的参数不为String，抛出异常
        //获取名为下标参数的扩展
        //调用扩展的该方法
    }

    private String generateClassInfo() {
        String classInfo = "public class %s$Adaptive implements %s {";
        return String.format(classInfo, type.getSimpleName(), type.getSimpleName());
    }

    private String generatePackageInfo() {
        return "package adaptive;\n";
    }

    private String generateImports() {
        return "import loader.ExtensionLoader;\n" +
                "import " + type.getName() + ";\n";
    }
}
