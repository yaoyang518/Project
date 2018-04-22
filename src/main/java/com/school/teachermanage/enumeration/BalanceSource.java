package com.school.teachermanage.enumeration;

/**
 * 积分来源枚举
 *
 * @author zhangsl
 * @date 2017-11-01
 */
public enum BalanceSource {

    CHILDREN_LEVEL_UP(0),
    CHILDREN_RECHARGE(1),
    PARENT_BALANCE(2),
    CHILDREN_SHOPKEEPER(3),
    CHILDREN_RANGE_RECHARGE(4),
    WITHDRAW_CASH_APPAY(5),
    WITHDRAW_CASH_FAIL(6),
    CHILDREN_REPLENISH(7),
    SYSTEM_TUNING(8);

    private int index;

    private BalanceSource(int index) {
        this.index = index;
    }

    public static BalanceSource getByIndex(int index) {
        switch (index) {
            case 0:
                return CHILDREN_LEVEL_UP;
            case 1:
                return CHILDREN_RECHARGE;
            case 2:
                return PARENT_BALANCE;
            case 3:
                return CHILDREN_SHOPKEEPER;
            case 4:
                return CHILDREN_RANGE_RECHARGE;
            case 5:
                return WITHDRAW_CASH_APPAY;
            case 6:
                return WITHDRAW_CASH_FAIL;
            case 7:
                return CHILDREN_REPLENISH;
            case 8:
                return SYSTEM_TUNING;
            default:
                return null;
        }
    }

    public String getName() {
        switch (index) {
            case 0:
                return "分享用户升级";
            case 1:
                return "分享用户充值";
            case 2:
                return "获得上级提成";
            case 3:
                return "分享用户升级店主";
            case 4:
                return "分享用户充值级差收入";
            case 5:
                return "用户提现申请";
            case 6:
                return "用户提现失败";
            case 7:
                return "分享用户补货";
            case 8:
                return "系统调整余额";
            default:
                return "";
        }
    }

}
