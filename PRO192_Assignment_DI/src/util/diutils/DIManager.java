package util.diutils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class DIManager {
    private static final Map<Class<?>, Object> instanceCache = new HashMap<>();

    public static void initialize(String baseDir, String basePackage) {
        Set<Class<?>> classes = ClassScanner.scanPackage(baseDir, basePackage);
        for (Class<?> clazz : classes) {
            // class co @Component
            if (clazz.isAnnotationPresent(Component.class)) {
                create(clazz);
            }
        }
    }

    public static <T> T create(Class<T> clazz) {
        try {
            if (instanceCache.containsKey(clazz)) //case co constructor (constructor co tham so gay error)
                return clazz.cast(instanceCache.get(clazz));

            T instance = clazz.getDeclaredConstructor().newInstance(); //case ko co construction
            instanceCache.put(clazz, instance);

            // field co @Injected
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Injected.class)) {
                    field.setAccessible(true);
                    Object dependency = create(field.getType()); //recursion
                    field.set(instance, dependency);
                }
            }

            // method co @AfterCreation
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(AfterCreation.class)) {
                    if (method.getParameterCount() == 0) { //method ko tham so
                        method.setAccessible(true);
                        method.invoke(instance); //goi new instance() trong method
                    } else {
                        throw new IllegalStateException("@AfterCreation method must have no parameters: " + method);
                    }
                }
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance: " + clazz.getName(), e);
        }
    }

    public static <T> T getInstance(Class<T> clazz) {
        return clazz.cast(instanceCache.get(clazz));
    }
}
