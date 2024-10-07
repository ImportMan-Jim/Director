package com.jim.director.framework.tool;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * author: Jim
 * date: 2024/10/2
 * info:
 */

public class PackageScanner {
    public static List<Class<?>> scanPackage(Class<?> targetClass) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        String packageName = targetClass.getPackage().getName();
        String path = packageName.replace('.', '/');

        // 获取包的URL
        Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File directory = new File(resource.getFile());
            if (directory.exists() && directory.isDirectory()) {
                // 扫描目录
                classes.addAll(findClasses(directory, packageName));
            }
        }
        return classes;
    }

    private static List<Class<?>> findClasses(File directory, String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 递归扫描子目录
                    classes.addAll(findClasses(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    try {
                        // 获取类名并加载
                        String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                        classes.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return classes;
    }
}
