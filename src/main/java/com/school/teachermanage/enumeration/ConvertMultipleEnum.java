package com.school.teachermanage.enumeration;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 赠送比例枚举
 *
 * @author zhangsl
 * @date 2017-11-05
 */
public enum ConvertMultipleEnum {

    ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5),
    SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10);

    private int index;

    private ConvertMultipleEnum(int index) {
        this.index = index;
    }

    public static ConvertMultipleEnum getByIndex(int index) {
        switch (index) {
            case 1:
                return ONE;
            case 2:
                return TWO;
            case 3:
                return THREE;
            case 4:
                return FOUR;
            case 5:
                return FIVE;
            case 6:
                return SIX;
            case 7:
                return SEVEN;
            case 8:
                return EIGHT;
            case 9:
                return NINE;
            case 10:
                return TEN;
            default:
                return null;
        }
    }

    public String getName() {
        switch (index) {
            case 1:
                return "一倍";
            case 2:
                return "二倍";
            case 3:
                return "三倍";
            case 4:
                return "四倍";
            case 5:
                return "五倍";
            case 6:
                return "六倍";
            case 7:
                return "七倍";
            case 8:
                return "八倍";
            case 9:
                return "九倍";
            case 10:
                return "十倍";
            default:
                return "";
        }
    }

    public int getIndex() {
        return this.index;
    }


    public static JSONArray getJsonArray() {
        JSONArray array = new JSONArray();
        for (ConvertMultipleEnum convertLevelEnum : ConvertMultipleEnum.values()) {
            JSONObject json = new JSONObject();
            json.put("name", convertLevelEnum.getName());
            json.put("value", convertLevelEnum.index);
            array.add(json);
        }
        return array;
    }

    public static boolean isValidate(int multiple) {
        for (ConvertMultipleEnum convertMultipleEnum : ConvertMultipleEnum.values()) {
            if (convertMultipleEnum.index == multiple) {
                return true;
            }
        }
        return false;
    }
} 