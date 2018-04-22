package com.school.teachermanage.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author zhangsl
 * @date 2017-11-03
 */
public class StringUtil {

    public static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty() || "null".equalsIgnoreCase(s);
    }

    public static boolean isNotEmpty(String s) {
        if (isEmpty(s)) {
            return false;
        }
        return true;
    }

    public static Integer toInteger(Object obj) {
        return obj == null || obj == "" ? 0 : Integer.parseInt(obj.toString());
    }

    public static String toString(Object obj) {
        return obj == null ? null : obj.toString();
    }

    public static String toStringNotNull(Object obj) {
        if (obj == null || "null".equalsIgnoreCase(obj.toString())) {
            return "";
        }
        return obj.toString();
    }

    public static void main(String[] args) {
        String s = StringUtil.substrinAppointGdigit("123", 4);
        System.out.println(s);
    }

    public static String substrinAppointGdigit(String str, int n) {
        if (str != null && str.length() >= n) {
            return str.substring(str.length() - n);
        }
        return "";
    }

    public static String string2Unicode(String string) {

        StringBuffer unicode = new StringBuffer();

        for (int i = 0; i < string.length(); i++) {

            // 取出每一个字符
            char c = string.charAt(i);

            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }

        return unicode.toString();
    }

    public static String unicode2String(String unicode) {

        StringBuffer string = new StringBuffer();

        String[] hex = unicode.split("\\\\u");

        for (int i = 1; i < hex.length; i++) {

            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);

            // 追加成string
            string.append((char) data);
        }

        return string.toString();
    }


    /**
     * 完整的判断中文汉字和符号
     *
     * @param strName
     * @return boolean
     */
    public static boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 根据Unicode编码完美的判断中文汉字和符号
     *
     * @param c
     * @return boolean
     */
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否包含特殊字符，如果包含返回true
     *
     * @param str
     * @return
     */
    public static boolean isConSpeCharacters(String str) {
        String regEx = "[`~!@#$%^&*+=|';'//[//].<>/?~！@#￥%……&*——+|【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

}
