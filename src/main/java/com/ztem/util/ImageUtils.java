package com.ztem.util;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author LiMeiyuan
 * @version v1.00
 * @package: cn.flyout.common.utils
 * @date 2016/5/3 17:46
 * @description:
 */
@Component
public class ImageUtils {
    public static String SERVER_PHOTO_PATH = "/home/image";

    public static final String IMAGE_EXT_REGEX = "jpe?g|png|gif|bmp";
    public static final String IMAGE_REGEX = "[^\\s]+(\\.(?i)(gif|jpe?g|tiff|png)$)";

    private static final String ALPHA = "alpha";//透明度(0.0 -- 1.0, 0.0为完全透明，1.0为完全不透明)
    private static final String FONTSIZE = "fontSize";// 字体大小，单位为像素
    private static final String HEIGHT = "height";// 字体距离图片底部的距离
    private static final String WIDTH = "width";// 字体距离图片右边的距离
    private static Color color = Color.WHITE;


    /**
     * 根据目标大小缩放图片
     *
     * @param inputStream  图片输入流
     * @param outputStream 输出图片流
     * @param toWidth      宽度
     * @param toHeight     高度
     */
    public static void zoomImage(InputStream inputStream,
                                 OutputStream outputStream, int toWidth, int toHeight)
            throws Exception {
        BufferedImage bufferedImage = ImageIO.read(inputStream);

        bufferedImage = zoomImage(bufferedImage, toWidth, toHeight);

        BufferedImage outImage = bufferedImage;
        ImageIO.write(outImage, "png", outputStream);
        inputStream.close();
        outputStream.flush();
    }

    /**
     * @param srcImage 原始图像
     * @param toWidth  目标宽度
     * @param toHeight 目标高度
     * @return 返回处理后的图像
     */
    public static BufferedImage zoomImage(BufferedImage srcImage, int toWidth, int toHeight) {
        BufferedImage result = null;

        try {
            /* 原始图像的宽度和高度 */

			/* 新生成结果图片 */
            result = new BufferedImage(toWidth, toHeight, BufferedImage.TYPE_4BYTE_ABGR);

            result.getGraphics().drawImage(srcImage.getScaledInstance(toWidth, toHeight,
                    Image.SCALE_SMOOTH), 0, 0, null);
        } catch (Exception e) {
            System.out.println("创建缩略图发生异常" + e.getMessage());
        }

        return result;
    }

    public static boolean isImage(String filename) {
        Matcher imageMatcher = Pattern.compile(IMAGE_REGEX, Pattern.CASE_INSENSITIVE).matcher(filename);
        return imageMatcher.matches();
    }

    /**
     * 将图片输出到response中
     *
     * @param filePath 文件全路径
     * @param response response
     */
    public static void outputImg(String filePath, HttpServletResponse response) {
        String[] names = FileUtil.getFileNameAndExtName(filePath);
        //根据文件不同格式设置ContentType
        if (names != null && names.length > 0) {
         /*   switch (names[1]) {
                case ".jpg":*/
                    response.setContentType("image/jpeg ");
            /*        break;
                case ".png":
                    response.setContentType("image/png");
                    break;
                default:
                    response.setContentType("image/jpeg");
                    break;*/
           // }
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        FileInputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(filePath);

            os = response.getOutputStream();
            byte[] data = new byte[2048];
            while (is.read(data) > 0) {
                os.write(data);
            }
            os.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void outputImg(String filePath, Integer width, Integer height, HttpServletResponse response) {
        if (!ImageUtils.isImage(filePath)) {
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            //原始文件不存在
            return;
        }
        if (width > 0 && height > 0) {
            String[] names = FileUtil.getFileNameAndExtName(filePath);
            //压缩之后的图片实际为.png格式
            String tmpFileName = names[0] + "_" + width + "_" + height + ".png";
            File tmpFile = new File(tmpFileName);
            if (tmpFile.exists()) {
                //有缓存文件
                ImageUtils.outputImg(tmpFile.getAbsolutePath(), response);
            } else {
                //无缓存文件，重新进行压缩
                try {
                    OutputStream outputStream = new FileOutputStream(tmpFile);
                    ImageUtils.zoomImage(new FileInputStream(file), outputStream, width, height);
                    outputStream.close();
                    ImageUtils.outputImg(tmpFile.getAbsolutePath(), response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            //无需进行压缩
            ImageUtils.outputImg(filePath, response);
        }
    }

    public static String saveImage(String folderName, CommonsMultipartFile photoFile) {
        InputStream photoFileIS;
        if (photoFile != null && !StringUtils.isBlank(photoFile.getOriginalFilename()) && photoFile.getSize() > 0) {
            try {
                String ofn = photoFile.getOriginalFilename();
                String extName = ofn.substring(ofn.lastIndexOf("."));
                photoFileIS = photoFile.getInputStream();
                //原有文件名按照时间点来进行命名改为UUID by LiMeiyuan 2016.5.4
                String filePath = File.separator + folderName + File.separator + ofn;
                String fullPath = saveImageFile(filePath, photoFileIS);
                photoFileIS.close();
                return fullPath;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static String saveImageFile(String fileName, InputStream is) throws IOException {
        String fullPath = toFullPath(fileName);
        return FileUtil.saveFile(is, fullPath);
    }

    private static String toFullPath(String fileName) {
        return SERVER_PHOTO_PATH + File.separator + fileName;
    }

    @Value("${SERVER_PHOTO_PATH}")
    public void setServerPhotoPath(String serverPhotoPath) {
        if (org.apache.commons.lang3.SystemUtils.IS_OS_MAC || org.apache.commons.lang3.SystemUtils.IS_OS_MAC_OSX) {
            SERVER_PHOTO_PATH = System.getProperty("user.home") + File.separator + "photo";
        } else {
            ImageUtils.SERVER_PHOTO_PATH = serverPhotoPath;
        }
    }

    /**
     * 图片压缩(原尺寸)
     *
     * @param srcURL 图片路径
     */
    public static void imageYS(String srcURL) {
        try {
            File srcFile = new File(srcURL);
            Image src = ImageIO.read(srcFile);
            int srcHeight = src.getHeight(null);
            int srcWidth = src.getWidth(null);
            BufferedImage tag = new BufferedImage(srcWidth, srcHeight, BufferedImage.TYPE_3BYTE_BGR);
            tag.getGraphics().drawImage(src, 0, 0, srcWidth, srcHeight, null); //绘制缩小后的图
            ImageIO.write(tag, "jpg", srcFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 图片压缩(指定尺寸,尺寸不够时补黑边框)
     *
     * @param filePath 图片路径
     * @param height   高度
     * @param width    宽度
     * @param bb       比例不对时是否需要补白
     */
    public static void resize(String filePath, int height, int width, boolean bb) {
        try {
            double ratio = 0; //缩放比例
            File f = new File(filePath);
            BufferedImage bi = ImageIO.read(f);
            Image itemp = bi.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
            //计算比例
            if ((bi.getHeight() > height) || (bi.getWidth() > width)) {
                if (bi.getHeight() > bi.getWidth()) {
                    ratio = (new Integer(height)).doubleValue() / bi.getHeight();
                } else {
                    ratio = (new Integer(width)).doubleValue() / bi.getWidth();
                }
                AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
                itemp = op.filter(bi, null);
            }
            if (bb) {
                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = image.createGraphics();
                g.setColor(Color.black);
                g.fillRect(0, 0, width, height);
                if (width == itemp.getWidth(null))
                    g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2, itemp.getWidth(null), itemp.getHeight(null), Color.white, null);
                else
                    g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0, itemp.getWidth(null), itemp.getHeight(null), Color.white, null);
                g.dispose();
                itemp = image;
            }
            ImageIO.write((BufferedImage) itemp, "jpg", f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
