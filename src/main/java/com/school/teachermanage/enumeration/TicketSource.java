package com.school.teachermanage.enumeration;

/**
 * 积分来源枚举
 *
 * @author zhangsl
 * @date 2017-11-01
 */
public enum TicketSource {

    CONVERT(0),SYSTEM_MODIFY(1);

    private int index;

    private TicketSource(int index) {
        this.index = index;
    }

    public static TicketSource getByIndex(int index) {
        switch (index) {
            case 0:
                return CONVERT;
            case 1:
                return SYSTEM_MODIFY;
            default:
                return null;
        }
    }

    public String getName() {
        switch (index) {
            case 0:
                return "提现转换";
            case 1:
                return "系统修改";
            default:
                return "";
        }
    }
}
