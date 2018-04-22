package com.school.teachermanage.enumeration;

/**
 * 订单状态
 *
 * @author zhangsl
 * @date 2017-11-14
 */
public enum OrderStatus {

    CANCEL(0), NEW(1),
    PAY(2), CONFIRM(3),
    SEND(4), FINISH(5),
    REFUND_APPLY(6), REFUND(7);

    private int index;

    public static OrderStatus getByIndex(int index){
        switch (index) {
            case 0:
                return CANCEL;
            case 1:
                return NEW;
            case 2:
                return PAY;
            case 3:
                return CONFIRM;
            case 4:
                return SEND;
            case 5:
                return FINISH;
            case 6:
                return REFUND_APPLY;
            case 7:
                return REFUND;
            default:
                return null;
        }
    }

    public String getName() {
        switch (index) {
            case 0:
                return "已取消";
            case 1:
                return "新订单";
            case 2:
                return "已支付";
            case 3:
                return "已确认";
            case 4:
                return "已发货";
            case 5:
                return "已完成";
            case 6:
                return "申请退款";
            case 7:
                return "已退款";
            default:
                return null;
        }
    }
    private OrderStatus(int index) {
        this.index = index;
    }
}
