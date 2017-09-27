package com.ztem.controller;

import com.google.gson.Gson;
import com.ztem.dto.BaseDto;
import com.ztem.util.ImageUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;


import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * Created by SunYingLu on 2017/09/26.
 */
@Controller
public class UploadController implements ServletContextAware {
    private static Gson gson = new Gson();
    //Spring这里是通过实现ServletContextAware接口来注入ServletContext对象
    private ServletContext servletContext;

    @RequestMapping("view")///{path}/{name}
    @ResponseBody
    public String viewImage(@PathVariable("path") String path,/*@PathVariable("name")String filename,*/ HttpServletResponse response){
        ImageUtils.outputImg(path,response);
        return "";
    }

    @RequestMapping("upload")
    @ResponseBody
    public String uploadImage(String uuid, HttpServletRequest request, HttpServletResponse response) {

        try {
            BaseDto dto = new BaseDto();
            //创建一个通用的多部分解析器
            CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
            //判断 request 是否有文件上传,即多部分请求
            if (multipartResolver.isMultipart(request)) {
                //转换成多部分request
                MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
                MultiValueMap<String, MultipartFile> multivaluemap = multiRequest.getMultiFileMap();
                List<MultipartFile> values = multivaluemap.get("files[]");
                if (values.size() > 0) {
                    CommonsMultipartFile commonsMultipartFile = (CommonsMultipartFile) values.get(0);
                    String filePath = ImageUtils.saveImage(uuid, commonsMultipartFile);
                    //图片压缩
                    ImageUtils.imageYS(filePath);

                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("name", commonsMultipartFile.getOriginalFilename());
                    map.put("type", commonsMultipartFile.getOriginalFilename().substring(commonsMultipartFile.getOriginalFilename().lastIndexOf(".")));
                    map.put("size", commonsMultipartFile.getSize());
                    File file = new File(filePath);
                    if (file.exists() && file.isFile()) {
                        map.put("compressSize", file.length());
                    } else {
                        map.put("compressSize", "null");
                    }
                    map.put("url", "/view?path="+filePath);
                    map.put("deleteUrl", "delete?path=" + filePath);
                    map.put("deleteType", "a");
                    map.put("thumbnailUrl", filePath);
                    List<Map<String, Object>> files = new ArrayList<Map<String, Object>>();
                    files.add(map);
                    dto.setFiles(files);
                    return gson.toJson(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将存放在sourceFilePath目录下的源文件，打包成fileName名称的zip文件，并存放到zipFilePath路径下
     *
     * @return
     */
    @RequestMapping("download")
    @ResponseBody
    public void downloadImage(String uuid, HttpServletRequest request, HttpServletResponse response) {

        try {
            boolean flag = false;
            File sourceFile = new File(ImageUtils.SERVER_PHOTO_PATH + File.separator + uuid);
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            FileOutputStream fos = null;
            ZipOutputStream zos = null;

            if (sourceFile.exists() == false) {
                System.out.println("待压缩的文件目录：" + ImageUtils.SERVER_PHOTO_PATH + uuid + "不存在.");
            } else {
                try {
                    File zipFile = new File(ImageUtils.SERVER_PHOTO_PATH + File.separator + uuid + ".zip");

                    if (zipFile.exists()) {
                        System.out.println(ImageUtils.SERVER_PHOTO_PATH + "目录下存在名字为:" + uuid + ".zip" + "打包文件.");
                    } else {

                        File[] sourceFiles = sourceFile.listFiles();
                        if (null == sourceFiles || sourceFiles.length < 1) {
                            System.out.println("待压缩的文件目录：" + ImageUtils.SERVER_PHOTO_PATH + uuid + "里面不存在文件，无需压缩.");
                        } else {
                            fos = new FileOutputStream(zipFile);
                            zos = new ZipOutputStream(new BufferedOutputStream(fos));
                            byte[] bufs = new byte[1024 * 10];
                            for (int i = 0; i < sourceFiles.length; i++) {
                                //创建ZIP实体，并添加进压缩包
                                ZipEntry zipEntry = new ZipEntry(sourceFiles[i].getName());
                                zos.putNextEntry(zipEntry);
                                //读取待压缩的文件并写进压缩包里
                                fis = new FileInputStream(sourceFiles[i]);
                                bis = new BufferedInputStream(fis, 1024 * 10);
                                int read = 0;
                                while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
                                    zos.write(bufs, 0, read);
                                }
                            }

                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //关闭流
                    try {
                        if (null != bis) bis.close();
                        if (null != zos) zos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
                fileDownload(response, uuid);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void fileDownload(HttpServletResponse response, String uuid) {
        //获取网站部署路径(通过ServletContext对象)，用于确定下载文件位置，从而实现下载


        //1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
        response.setContentType("multipart/form-data");
        //2.设置文件头：最后一个参数是设置下载文件名(假如我们叫a.pdf)
        response.setHeader("Content-Disposition", "attachment;fileName=" + uuid + ".zip");
        ServletOutputStream out;
        //通过文件路径获得File对象(假如此路径中有一个download.pdf文件)
        File file = new File(ImageUtils.SERVER_PHOTO_PATH +File.separator+ uuid + ".zip");

        try {
            FileInputStream inputStream = new FileInputStream(file);

            //3.通过response获取ServletOutputStream对象(out)
            out = response.getOutputStream();

            int b = 0;
            byte[] buffer = new byte[512];
            while (b != -1) {
                b = inputStream.read(buffer);
                //4.写到输出流(out)中
                out.write(buffer, 0, b);
            }
            inputStream.close();
            out.close();
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
