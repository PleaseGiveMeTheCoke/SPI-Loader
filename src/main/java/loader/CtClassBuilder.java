package loader;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CtClassBuilder is builder for CtClass
 * <p>
 * contains all the information, including:
 * <p>
 * class name, imported packages, super class name, implemented interfaces, constructors, fields, methods.
 */
public class CtClassBuilder {

    private String className;

    private String superClassName = "java.lang.Object";

    private final List<String> imports = new ArrayList<>();

    private final Map<String, String> fullNames = new HashMap<>();

    private final List<String> ifaces = new ArrayList<>();

    private final List<String> constructors = new ArrayList<>();

    private final List<String> fields = new ArrayList<>();

    private final List<String> methods = new ArrayList<>();

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSuperClassName() {
        return superClassName;
    }

    /**
     *
     * @param superClassName full class name
     */
    public void setSuperClassName(String superClassName) {
        this.superClassName = superClassName;
    }

    public List<String> getImports() {
        return imports;
    }

    public void addImports(String pkg) {
        int pi = pkg.lastIndexOf('.');
        if (pi > 0) {
            String pkgName = pkg.substring(0, pi);
            this.imports.add(pkgName);
            if (!pkg.endsWith(".*")) {
                fullNames.put(pkg.substring(pi + 1), pkg);
            }
        }
    }

    public List<String> getInterfaces() {
        return ifaces;
    }

    /**
     *
     * @param iface full class name
     */
    public void addInterface(String iface) {
        this.ifaces.add(iface);
    }

    public List<String> getConstructors() {
        return constructors;
    }

    public void addConstructor(String constructor) {
        this.constructors.add(constructor);
    }

    public List<String> getFields() {
        return fields;
    }

    public void addField(String field) {
        this.fields.add(field);
    }

    public List<String> getMethods() {
        return methods;
    }

    public void addMethod(String method) {
        this.methods.add(method);
    }

    /**
     * build CtClass object
     */
    public CtClass build(ClassLoader classLoader) throws NotFoundException, CannotCompileException {
        ClassPool pool = new ClassPool(true);
        pool.insertClassPath(new LoaderClassPath(classLoader));
        // pool.insertClassPath(new DubboLoaderClassPath());

        // create class
        CtClass ctClass = pool.makeClass(className, pool.get(superClassName));

        // add imported packages
        imports.forEach(pool::importPackage);

        // add implemented interfaces
        for (String iface : ifaces) {
            ctClass.addInterface(pool.get(iface));
        }

        // add constructors
        for (String constructor : constructors) {
            ctClass.addConstructor(CtNewConstructor.make(constructor, ctClass));
        }

        // add fields
        for (String field : fields) {
            ctClass.addField(CtField.make(field, ctClass));
        }

        // add methods
        for (String method : methods) {
            ctClass.addMethod(CtNewMethod.make(method, ctClass));
        }

        return ctClass;
    }

}
