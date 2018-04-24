package com.school.teachermanage.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 拼音处理类
 *
 * @author zhangsl
 * @date 2-18-01-29
 */
public class PinYinUtil {


    // 完整的判断中文汉字和符号
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

    // 根据Unicode编码完美的判断中文汉字和符号
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    /**
     * 提取汉字的首字母，如果里面含有费中文字符则忽略之；如果全为非中文则返回""。
     *
     * @param caseType 当为1时获取的首字母为小写，否则为大写。
     * @author yaoyang
     */
    public static String getPinYinHeadChar(String zn_str, boolean lower) {
        if (!isChinese(zn_str)){
            return zn_str.substring(0,1).toUpperCase();
        }
        if (zn_str != null && !zn_str.trim().equalsIgnoreCase("")) {
            char[] strChar = zn_str.toCharArray();
            // 汉语拼音格式输出类  
            HanyuPinyinOutputFormat hanYuPinOutputFormat = new HanyuPinyinOutputFormat();
            // 输出设置，大小写，音标方式等  
            if (lower) {
                hanYuPinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            } else {
                hanYuPinOutputFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
            }
            hanYuPinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            hanYuPinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
            StringBuffer pyStringBuffer = new StringBuffer();
            for (int i = 0; i < strChar.length; i++) {
                char c = strChar[i];
                char pyc = strChar[i];
                if (String.valueOf(c).matches("[\\u4E00-\\u9FA5]+")) {//是中文或者a-z或者A-Z转换拼音
                    try {
                        String[] pyStirngArray = PinyinHelper.toHanyuPinyinStringArray(strChar[i], hanYuPinOutputFormat);
                        if (null != pyStirngArray && pyStirngArray[0] != null) {
                            pyc = pyStirngArray[0].charAt(0);
                            pyStringBuffer.append(pyc);
                        }
                    } catch (BadHanyuPinyinOutputFormatCombination e) {
                        e.printStackTrace();
                    }
                }
            }
            return pyStringBuffer.toString();
        }
        return null;
    }

    public static String getFirstLetter(String string) {
        return getPinYinHeadChar(string, false).toCharArray()[0] + "";
    }

    public static void main(String[] args) {
        String cnStr = "单田芳";
        System.out.println(getPinYinHeadChar(cnStr, true)); //输出lff
        System.out.println(getPinYinHeadChar(cnStr, false)); //输出LFF
        System.out.println(getFirstLetter(cnStr));
    }
}
