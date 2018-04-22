package com.school.teachermanage.enumeration;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 充值等级枚举
 *
 * @author zhangsl
 * @date 2017-11-05
 */
public enum ConvertLevelEnum {

    five(5), ten(10), fourteen(15);

    private int index;

    private ConvertLevelEnum(int index) {
        this.index = index;
    }


    public int getIndex() {
        return this.index;
    }


    public static JSONArray getJsonArray() {
        JSONArray array = new JSONArray();
        for (ConvertLevelEnum convertLevelEnum : ConvertLevelEnum.values()) {
            JSONObject json = new JSONObject();
            json.put("name", convertLevelEnum.getName());
            json.put("value", convertLevelEnum.index);
            array.add(json);
        }
        return array;
    }

    public static boolean isValidate(int level) {
        for (ConvertLevelEnum convertLevelEnum : ConvertLevelEnum.values()) {
            if (convertLevelEnum.index == level) {
                return true;
            }
        }
        return false;
    }

    public static ConvertLevelEnum getByIndex(int index){
        switch (index) {
            case 5:
                return five;
            case 10:
                return ten;
            case 15:
                return fourteen;
            default:
                return null;
        }
    }

    public String getName() {
        switch (index) {
            case 5:
                return "5万元";
            case 10:
                return "10万元";
            case 15:
                return "15万元";
            default:
                return null;
        }
    }
} 