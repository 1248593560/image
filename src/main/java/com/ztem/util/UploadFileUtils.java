package com.ztem.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class UploadFileUtils {

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

    public static String saveFile(String folderName, CommonsMultipartFile photoFile) {
        InputStream photoFileIS;
        if (photoFile != null && !StringUtils.isBlank(photoFile.getOriginalFilename()) && photoFile.getSize() > 0) {
            try {
                String ofn = photoFile.getOriginalFilename();
                String extName = ofn.substring(ofn.lastIndexOf("."));
                photoFileIS = photoFile.getInputStream();
                //原有文件名按照时间点来进行命名改为UUID by LiMeiyuan 2016.5.4
                String filePath = File.separator + folderName + File.separator + UUID.randomUUID() + extName;
                FileUtil.saveUploadFile(filePath, photoFileIS);
                photoFileIS.close();
                return filePath;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

}
