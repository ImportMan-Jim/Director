package com.jim.director.framework.tool;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * author: Jim
 * date: 2024/10/3
 * info:
 */

public class FileUtils {

    public static File getFileAndCreateIfNotExists(String filePath) throws IOException {
        // 创建 File 对象
        File file = new File(filePath);
        // 检查文件是否存在
        if (!file.exists()) {
            // 如果文件不存在，尝试创建文件
            if (!file.createNewFile()){
                System.out.println("can not create file: " + filePath);
            }
        }
        return file;
    }

    public static String getAbsolutePathFromClassAndPath(Class<?> clazz, String relativePath) {
        // 获取传入类的资源URL
        URL classLocation = clazz.getProtectionDomain().getCodeSource().getLocation();

        // 将 URL 转换为 File
        File classFile = new File(classLocation.getPath());

        // 获取类所在目录
        File parentDir = classFile.isDirectory() ? classFile : classFile.getParentFile();

        // 基于类所在目录，返回相对路径的文件对象，并获取绝对路径
        File targetFile = new File(parentDir, relativePath);
        return targetFile.getAbsolutePath();
    }
}
