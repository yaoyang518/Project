package com.school.teachermanage.enumeration;

/**
 * 支付方式
 *
 * @author zhangsl
 * @date 2017-11-14
 */
public enum PayMethod {

    ALIPAY(0), WEIXIN(1), BANKCARD(2);

    private int index;

    private PayMethod(int index) {
        this.index = index;
    }

    public static PayMethod getByIndex(int index) {
        switch (index) {
            case 0:
                return ALIPAY;
            case 1:
                return WEIXIN;
            case 2:
                return BANKCARD;
            default:
                return null;
        }
    }

    public static PayMethod getByName(String name) {
        if ("ALIPAY".equalsIgnoreCase(name)) {
            return ALIPAY;
        } else if ("WEIXIN".equalsIgnoreCase(name)) {
            return WEIXIN;
        } else if ("BANKCARD".equalsIgnoreCase(name)) {
            return BANKCARD;
        }
        return null;
    }

    public String getName() {
        switch (index) {
            case 0:
                return "支付宝";
            case 1:
                return "微信";
            case 2:
                return "银行卡";
            default:
                return null;
        }
    }

}
