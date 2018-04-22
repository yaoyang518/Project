package com.school.teachermanage.enumeration;

/**
 * 退换货类型
 *
 * @author zhangsl
 * @date 2017-11-15
 */
public enum RefundType {

    REFUND_NEW_ORDER(0, "新订单退款"),
    REFUND_CONFIRM_ORDER(1, "已确认订单退款"),
    REFUND_ALL(2, "退款退货"),
    REFUND_ALL_MONEY(3, "仅退款"),
    REFUND_PART(4, "部分退款退货"),
    REFUND_PART_MONEY(5, "部分退款");

    private int index;
    private String name;

    private RefundType(int index, String name) {
        this.index = index;
        this.name = name;
    }
}
