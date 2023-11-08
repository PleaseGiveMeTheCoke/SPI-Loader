package loader;

import annotation.Adaptive;

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
        if(parameterTypes.length > 0){
            methodInfo.deleteCharAt(methodInfo.length() - 1);
        }
        methodInfo.append("){\n");
        methodInfo.append(generateMethodContent(method));
        methodInfo.append("}\n");

        return methodInfo.toString();
    }

    private String generateMethodContent(Method method) {
        StringBuilder methodContent = new StringBuilder();
        Adaptive annotation = method.getAnnotation(Adaptive.class);
        if(annotation == null){
            methodContent.append("throw new RuntimeException(\"method not support adaptive\");\n");
            return methodContent.toString();
        }

        int index = annotation.index();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if(index >= 0 && index < parameterTypes.length){
            Class<?> parameterType = parameterTypes[index];
            if(parameterType != String.class){
                methodContent.append("throw new RuntimeException(\"given arg not a String\");\n");
                return methodContent.toString();
            }

            methodContent.append("if(arg" + index + " == null || arg" + index + ".equals(\"\")){\n" +
                    "            throw new RuntimeException(\"null arg of extension name\");\n" +
                    "        }\n");
            methodContent.append("Object extension = ExtensionLoader.getExtensionLoader(" + type.getName() + ".class).getExtension(arg" + index + ");\n");
            methodContent.append("if(extension == null){\n" +
                    "            throw new RuntimeException(\"no such extension of name given by index\");\n" +
                    "        }\n");
            methodContent.append("return ((" + type.getName() + ")extension)." + method.getName() + "(" + getArgList(method) + ");\n");
            return methodContent.toString();
        }

        String value = annotation.value();
        methodContent.append("Object extension = ExtensionLoader.getExtensionLoader(" + type.getName() + ".class).getExtension(\"" + value + "\");\n");
        methodContent.append("if(extension == null){\n" +
                "            throw new RuntimeException(\"no such extension of name given by value\");\n" +
                "        }\n");
        methodContent.append("return ((" + type.getName() + ")extension)." + method.getName() + "(" + getArgList(method) + ");\n");
        return methodContent.toString();
    }

    private String getArgList(Method method) {
        StringBuilder argList = new StringBuilder();
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            argList.append("arg").append(i).append(",");
        }

        if(argList.length() == 0){
            return argList.toString();
        }
        return argList.deleteCharAt(argList.length() - 1).toString();
    }

    private String generateClassInfo() {
        String classInfo = "public class %s$Adaptive implements %s {\n";
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
