package com.school.teachermanage.enumeration;

/**
 * 报单积分来源枚举
 *
 * @author zhangsl
 * @date 2017-11-01
 */
public enum BitcoinSource {

    RECHARGE(0),DONATE(1),CONSUME(2);

    private int index;

    private BitcoinSource(int index) {
        this.index = index;
    }

    public static BitcoinSource getByIndex(int index) {
        switch (index) {
            case 0:
                return RECHARGE;
            case 1:
                return DONATE;
            case 2:
                return CONSUME;
            default:
                return null;
        }
    }

    public String getName() {
        switch (index) {
            case 0:
                return "充值";
            case 1:
                return "赠送";
            case 2:
                return "消费";
            default:
                return "";
        }
    }
}
