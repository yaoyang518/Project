package com.school.teachermanage.enumeration;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 转换率来源
 *
 * @author wudc
 * @date 2017-11-07
 */
public enum ConvertPercentEnum {

    one(1), two(2), three(3);

    private int index;

    private ConvertPercentEnum(int index) {
        this.index = index;
    }

    public String getName() {
        switch (index) {
            case 1:
                return "1‰";
            case 2:
                return "2‰";
            case 3:
                return "3‰";
                default:
                    return null;

        }
    }

    public static ConvertPercentEnum getByIndex(int index) {
        switch (index) {
            case 1:
                return one;
            case 2:
                return two;
            case 3:
                return three;
            default:
                return null;
        }
    }

    public int getIndex() {
        return this.index;
    }


    public static JSONArray getJsonArray() {
        JSONArray array = new JSONArray();
        for (ConvertPercentEnum convertPercent : ConvertPercentEnum.values()) {
            JSONObject json = new JSONObject();
            json.put("name", convertPercent.getName());
            json.put("value", convertPercent.index);
            array.add(json);
        }
        return array;
    }

    public static boolean getConvertPercentEnumByIndex(int percent) {
        for (ConvertPercentEnum convertPercent : ConvertPercentEnum.values()) {
            if (convertPercent.index == percent) {
                return true;
            }
        }
        return false;
    }

}
