package com.school.teachermanage.enumeration;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 消费券比例
 *
 * @author wudc
 * @date 2017-11-07
 */
public enum TicketPercentEnum {

    ONE(5), TWO(10), THREE(15), FOUR(20);

    private int index;

    private TicketPercentEnum(int index) {
        this.index = index;
    }

    public String getName() {
        switch (index) {
            case 5:
                return "5%";
            case 10:
                return "10%";
            case 15:
                return "15%";
            case 20:
                return "20%";
            default:
                return null;

        }
    }

    public static TicketPercentEnum getByIndex(int index) {
        switch (index) {
            case 5:
                return ONE;
            case 10:
                return TWO;
            case 15:
                return THREE;
            case 20:
                return FOUR;
            default:
                return null;
        }
    }

    public int getIndex() {
        return this.index;
    }


    public static JSONArray getJsonArray() {
        JSONArray array = new JSONArray();
        for (TicketPercentEnum convertPercent : TicketPercentEnum.values()) {
            JSONObject json = new JSONObject();
            json.put("name", convertPercent.getName());
            json.put("value", convertPercent.index);
            array.add(json);
        }
        return array;
    }

    public static boolean getConvertPercentEnumByIndex(int percent) {
        for (TicketPercentEnum convertPercent : TicketPercentEnum.values()) {
            if (convertPercent.index == percent) {
                return true;
            }
        }
        return false;
    }

}
