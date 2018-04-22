package com.school.teachermanage.enumeration;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 补货比例
 *
 * @author wudc
 * @date 2017-11-07
 */
public enum ReplenishPercentEnum {

    ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5);

    private int index;

    private ReplenishPercentEnum(int index) {
        this.index = index;
    }

    public String getName() {
        switch (index) {
            case 1:
                return "1%";
            case 2:
                return "2%";
            case 3:
                return "3%";
            case 4:
                return "4%";
            case 5:
                return "5%";
            default:
                return null;

        }
    }

    public static ReplenishPercentEnum getByIndex(int index) {
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
            default:
                return null;
        }
    }

    public int getIndex() {
        return this.index;
    }


    public static JSONArray getJsonArray() {
        JSONArray array = new JSONArray();
        for (ReplenishPercentEnum convertPercent : ReplenishPercentEnum.values()) {
            JSONObject json = new JSONObject();
            json.put("name", convertPercent.getName());
            json.put("value", convertPercent.index);
            array.add(json);
        }
        return array;
    }

    public static boolean getConvertPercentEnumByIndex(int percent) {
        for (ReplenishPercentEnum convertPercent : ReplenishPercentEnum.values()) {
            if (convertPercent.index == percent) {
                return true;
            }
        }
        return false;
    }

}
