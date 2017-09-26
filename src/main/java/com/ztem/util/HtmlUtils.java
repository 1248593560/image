package com.ztem.util;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtils {

    private final static String REGXP_FOR_HTML = "</?[^>]+>"; // 去除P标签

    private final static String REGXP_FOR_IMG_TAG = "<img.*src\\s*=\\s*(.*?)[^>]*?>"; // 找出IMG标签

    private final static String REGXP_FOR_IMG_TAG_SRC_ATTRIB = "src\\s*=\\s*\"?(.*?)(\"|>|\\s+)"; // 找出IMG标签的SRC属性

    private final static String REGXP_FOR_ldquo = "\\&[a-zA-Z]{1,10};";  //去除&ldquo;标签

    //---------------------------------------------------------------------
    public static List<String> getImgStr(String htmlStr) {
        String img = "";
        Pattern p_image;
        Matcher m_image;
        List<String> pics = new ArrayList<String>();
        p_image = Pattern.compile
                (REGXP_FOR_IMG_TAG, Pattern.CASE_INSENSITIVE);
        m_image = p_image.matcher(htmlStr);
        while (m_image.find()) {
            img = img + "," + m_image.group();
            Matcher m = Pattern.compile(REGXP_FOR_IMG_TAG_SRC_ATTRIB).matcher(img);
            while (m.find()) {
                String a = m.group(1);
                if (a.indexOf("/") != -1) {
                    a = a.substring(a.lastIndexOf("/") + 1, a.length());
                }
                pics.add(a);
            }
        }
        return pics;
    }

    /**
     * 过滤<img>标签
     *
     * @param str
     * @return
     */
    public static String fiterImgTag(String str) {
        Pattern pattern = Pattern.compile(REGXP_FOR_IMG_TAG);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        boolean result1 = matcher.find();
        while (result1) {
            matcher.appendReplacement(sb, "");
            result1 = matcher.find();
        }
        matcher.appendTail(sb);
        return sb.toString();

    }

    /**
     * 基本功能：过滤指定标签
     */
    public static String fiterHtmlTag(String str, String tag) {
        String regxp = "<\\s*" + tag + "\\s+([^>]*)\\s*>";
        Pattern pattern = Pattern.compile(regxp);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        boolean result1 = matcher.find();
        while (result1) {
            matcher.appendReplacement(sb, "");
            result1 = matcher.find();
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 过滤所以html标签
     *
     * @param str
     * @return
     */
    public static String filterHtml(String str) {
        Pattern pattern = Pattern.compile(REGXP_FOR_HTML);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        boolean result1 = matcher.find();
        while (result1) {
            matcher.appendReplacement(sb, "");
            result1 = matcher.find();
        }
        matcher.appendTail(sb);
        return ldquo(sb.toString());
    }

    /**
     * 去除&ldquo;标签
     *
     * @param str
     * @return
     */
    private static String ldquo(String str) {
        Pattern pattern = Pattern.compile(REGXP_FOR_ldquo);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        boolean result1 = matcher.find();
        while (result1) {
            matcher.appendReplacement(sb, "");
            result1 = matcher.find();
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 基本功能：替换指定的标签
     * <p>
     *
     * @param str
     * @param beforeTag 要替换的标签
     * @param tagAttrib 要替换的标签属性值
     * @param startTag  新标签开始标记
     * @param endTag    新标签结束标记
     * @return String
     * @如：替换img标签的src属性值为[img]属性值[/img]
     */
    public static String replaceHtmlTag(String str, String beforeTag,
                                        String tagAttrib, String startTag, String endTag) {
        String regxpForTag = "<\\s*" + beforeTag + "\\s+([^>]*)\\s*>";
        String regxpForTagAttrib = tagAttrib + "=\"([^\"]+)\"";
        Pattern patternForTag = Pattern.compile(regxpForTag);
        Pattern patternForAttrib = Pattern.compile(regxpForTagAttrib);
        Matcher matcherForTag = patternForTag.matcher(str);
        StringBuffer sb = new StringBuffer();
        boolean result = matcherForTag.find();
        while (result) {
            StringBuffer sbreplace = new StringBuffer();
            Matcher matcherForAttrib = patternForAttrib.matcher(matcherForTag
                    .group(1));
            if (matcherForAttrib.find()) {
                matcherForAttrib.appendReplacement(sbreplace, startTag
                        + matcherForAttrib.group(1) + endTag);
            }
            matcherForTag.appendReplacement(sb, sbreplace.toString());
            result = matcherForTag.find();
        }
        matcherForTag.appendTail(sb);
        return sb.toString();
    }

    /**
     * 切割Sting
     *
     * @param str    原字符串
     * @param star   开始位置
     * @param length 需要切割的长度
     * @return
     */
    public static String substring(String str, int star, int length) {
        if ((star + length) > str.length()) {
            str = str.substring(star, str.length());
        } else {
            str = str.substring(star, star + length);
        }
        return str;
    }


    /**
     * @param s
     * @return 获得图片
     */
    public static List<String> getImg(String s) {
        String regex;
        List<String> list = new ArrayList<String>();
        regex = "src=\"(.*?)\"";
        Pattern pa = Pattern.compile(regex, Pattern.DOTALL);
        Matcher ma = pa.matcher(s);
        while (ma.find()) {
            list.add(ma.group());
        }
        return list;
    }

    /**
     * 返回存有图片地址的src数组
     *
     * @param tar
     * @return
     */
    public static String[] getImgaddress(String tar) {
        List<String> imgList = getImg(tar);

        String res[] = new String[imgList.size()];

        if (imgList.size() > 0) {
            for (int i = 0; i < imgList.size(); i++) {
                int begin = imgList.get(i).indexOf("/home");
                int end = imgList.get(i).lastIndexOf("\"");
                String url = imgList.get(i).substring(begin, end);
                res[i] = url;
            }
        } else {
        }
        return res;
    }
}
