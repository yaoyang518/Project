package com.school.teachermanage.enumeration;

/**
 * 退换货状态
 *
 * @author zhangsl
 * @date 2017-11-15
 */
public enum RefundStatus {

    APPLY(0, "申请中"),
    CONFIRM(1, "已确认"),
    FINISH(3, "已退款"),
    REFUSE(4, "已拒绝");

    private int index;
    private String name;

    private RefundStatus(int index, String name) {
        this.index = index;
        this.name = name;
    }
    public static RefundStatus getByIndex(int index) {
        switch (index) {
            case 0:
                return APPLY;
            case 1:
                return CONFIRM;
            case 3:
                return FINISH;
            case 4:
                return REFUSE;
            default:
                return null;
        }
    }

    public String getName() {
        switch (index) {
            case 0:
                return "申请中";
            case 1:
                return "已确认";
            case 3:
                return "已退款";
            case 4:
                return "已拒绝";
            default:
                return null;
        }
    }
}
