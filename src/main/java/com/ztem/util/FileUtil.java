package com.ztem.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
@Component
public class FileUtil {
    public static String FILE_PATH = "";
    public static String ATTACH_PATH = "";

    public static String saveUploadFile(String fileName, InputStream is) throws IOException {
        String fullPath = toFullPath(fileName);
        return saveFile(is, fullPath);
    }

    public static String saveFile(InputStream is, String fullPath) throws IOException {
        File file = new File(fullPath);
        FileOutputStream fos = null;
        try {
            String parenPath = file.getParent();
            File parent = new File(parenPath);
            parent.mkdirs();
            file.createNewFile();
            fos = new FileOutputStream(file);
            FileCopyUtils.copy(is, fos);
            return fullPath;
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }

    public static void deleteUploadFile(String filePath) {
        String fullPath = toFullPath(filePath);
        File file = new File(fullPath);
        file.deleteOnExit();
    }

    private static String toFullPath(String path) {
//        String rootPath = WebContextUtil.getContextRealPath();
        return FILE_PATH + File.separator + path;
    }

    public static String getExtName(String fileName) {
        Matcher extMatcher = Pattern.compile("(\\.[^.]+)$").matcher(fileName);
        if (extMatcher.find()) {
            return extMatcher.group(1);
        }
        return null;
    }

    /**
     * 根据名称获取文件的文件名和扩展名
     * 如果名称包含路径，则路径在文件名中
     *
     * @param fileName 给定的文件名
     * @return 长度为2的String数组，0为文件名，1为文件扩展名
     */
    public static String[] getFileNameAndExtName(String fileName) {
        Matcher nameMatcher = Pattern.compile("(.*)(\\.[^.]+)$").matcher(fileName);
        String[] names = new String[2];
        if (!fileName.contains(".")) {
            names[0] = fileName;
        } else if (nameMatcher.find()) {
            names[0] = nameMatcher.group(1);
            names[1] = nameMatcher.group(2);
        }
        return names;
    }

    /**
     * 根据文件扩展名及文件存放目录，返回文件的预存储路径
     *
     * @param folderName 文件存放目录
     * @param extName    文件扩展名
     * @return 文件预存放路径
     */
    public static String getFilePath(String folderName, String extName) {
        return File.separator + folderName + File.separator + UUID.randomUUID() + extName;
    }

    @Value("${FILE_PATH}")
    public void setFilePath(String filePath) {
        FILE_PATH = filePath;
    }

    @Value("${ATTACH_PATH}")
    public void setAttachPath(String attachPath) {
        ATTACH_PATH = attachPath;
    }
}
