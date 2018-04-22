package com.school.teachermanage.util;

import java.security.MessageDigest;

/**
 * MD5加密工具类
 * @author zhangsl
 * @date 2017-11-01
 */
public class Md5Util {

    public static void main(String[] args) {
        System.out.println(doubleMD5("123890pvlhnzybmtcdw1p5ga1yxrvh2ltbxwz9"));
    }

    public static final String MD5(String src) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] str;
        byte[] btInput = src.getBytes();
        try {
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            str = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final String doubleMD5(String src) {
        return multiMD5(src, 2);
    }

    private static final String multiMD5(String src, int times) {
        String result = src;
        for (int i = 0; i < times; i++) {
            result = MD5(result);
        }
        return result;
    }

}
