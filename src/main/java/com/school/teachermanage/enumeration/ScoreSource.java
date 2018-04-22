package com.school.teachermanage.enumeration;

/**
 * 积分来源枚举
 *
 * @author zhangsl
 * @date 2017-11-01
 */
public enum ScoreSource {

    RECHARGE(0), CONVERT(1), REG(2),
    CHILDREN_RECHARGE(3), SYSTEM_MODIFY(4),
    CHILDREN_LEVEL_UP(5),LEVEL_UP(6);

    private int index;

    private ScoreSource(int index) {
        this.index = index;
    }

    public static ScoreSource getByIndex(int index) {
        switch (index) {
            case 0:
                return RECHARGE;
            case 1:
                return CONVERT;
            case 2:
                return REG;
            case 3:
                return CHILDREN_RECHARGE;
            case 4:
                return SYSTEM_MODIFY;
            case 5:
                return CHILDREN_LEVEL_UP;
            case 6:
                return LEVEL_UP;
            default:
                return null;
        }
    }

    public String getName() {
        switch (index) {
            case 0:
                return "充值";
            case 1:
                return "每日转化";
            case 2:
                return "注册赠送";
            case 3:
                return "分享用户消费";
            case 4:
                return "系统修改";
            case 5:
                return "分享用户升级";
            case 6:
                return "用户升级";
            default:
                return "";
        }
    }
}
