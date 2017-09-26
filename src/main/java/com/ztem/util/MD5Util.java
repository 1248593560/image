package com.ztem.util;

import java.security.MessageDigest;

/**
 * Created by zkb on 2017/7/31
 * MD5加密工具类
 */
public class MD5Util{


    public static String MD5Encode(String aData) throws SecurityException {
        String resultString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = bytes2HexString(md.digest(aData.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
            throw new SecurityException("MD5 运算失败");
        }
        return resultString;
    }



    private static String bytes2HexString(byte[] b) {
        String ret = "";
        for (byte aB : b) {
            String hex = Integer.toHexString(aB & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }
}
