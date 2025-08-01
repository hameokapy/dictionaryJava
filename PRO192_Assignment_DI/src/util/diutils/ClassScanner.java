package util.diutils;

import util.LoggerUtil;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class ClassScanner {
    private static final Logger logger = LoggerUtil.getLogger(ClassScanner.class);

    public static Set<Class<?>> scanPackage(String baseDir, String packageName) {
        Set<Class<?>> classes = new HashSet<>();
        File dir = new File(baseDir);
        if (!dir.exists()) return classes; //null

        File[] files = dir.listFiles();
        if (files == null) return classes; //null

        for (File file : files) {
            if (file.isDirectory()) {
                String subPackage = packageName.isEmpty() ? file.getName() : packageName + "." + file.getName();
                classes.addAll(scanPackage(file.getAbsolutePath(), subPackage)); //recursion
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().replace(".class", "");
                try {
                    classes.add(Class.forName(className));
                } catch (ClassNotFoundException e) {
                    logger.fine("No classes found for " + className);
                }
            }
        }

        return classes;
    }
}

