package com.school.teachermanage.enumeration;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 用户等级定义
 *
 * @author zhangsl
 * @date 2017-11-13
 */
public enum UserLevel {

    USER_NORMAL(0),
    LEVEL_ONE(1),
    LEVEL_SIX(6),
    LEVEL_ELEVEN(11),
    LEVEL_SIXTEEN(16),
    LEVEL_TWENTY_ONE(21),
    LEVEL_TWENTY_SIX(26);

    private int index;

    private UserLevel(int index) {
        this.index = index;
    }

    public String getName() {
        switch (index) {
            case 0:
                return "普通会员";
            case 1:
                return "店主";
            case 6:
                return "消费商";
            case 11:
                return "办事处";
            case 16:
                return "体验中心";
            case 21:
                return "商务中心";
            case 26:
                return "城市运营商";
            default:
                return null;
        }
    }

    public static UserLevel getByIndex(int index) {
        switch (index) {
            case 0:
                return USER_NORMAL;
            case 1:
                return LEVEL_ONE;
            case 6:
                return LEVEL_SIX;
            case 11:
                return LEVEL_ELEVEN;
            case 16:
                return LEVEL_SIXTEEN;
            case 26:
                return LEVEL_TWENTY_SIX;
            case 21:
                return LEVEL_TWENTY_ONE;
            default:
                return null;
        }
    }

    public int getIndex() {
        return this.index;
    }


    public static JSONArray getJsonArray() {
        JSONArray array = new JSONArray();
        for (UserLevel userLevel : UserLevel.values()) {
            if (userLevel.index != 0) {
                JSONObject json = new JSONObject();
                json.put("name", userLevel.getName());
                json.put("value", userLevel.index);
                array.add(json);
            }
        }
        return array;
    }

    public static JSONArray getJsonArray(int index, boolean shopKeeper) {
        JSONArray array = new JSONArray();
        for (UserLevel userLevel : UserLevel.values()) {


            if (userLevel.getIndex() == 1) {
                if (shopKeeper) {
                    continue;
                } else {
                    JSONObject json = new JSONObject();
                    json.put("name", userLevel.getName());
                    json.put("value", userLevel.index);
                    array.add(json);
                    continue;
                }
            }

            if (userLevel.index > index) {
                JSONObject json = new JSONObject();
                json.put("name", userLevel.getName());
                json.put("value", userLevel.index);
                array.add(json);
            }
        }
        return array;
    }

    public static boolean isValidate(int level) {
        for (UserLevel userLevel : UserLevel.values()) {
            if (userLevel.index == level) {
                return true;
            }
        }
        return false;
    }

    public static UserLevel getEnumByIndex(int index) {
        for (UserLevel userLevel : UserLevel.values()) {
            if (userLevel.index == index) {
                return userLevel;
            }
        }
        return null;
    }

    public static UserLevel getEnumByName(String name) {
        for (UserLevel userLevel : UserLevel.values()) {
            if (userLevel.getName().equalsIgnoreCase(name)) {
                return userLevel;
            }
        }
        return null;
    }
}
