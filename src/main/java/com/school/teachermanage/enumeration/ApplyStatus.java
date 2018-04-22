package com.school.teachermanage.enumeration;

/**
 * 申请状态枚举
 *
 * @author zhangsl
 * @date 2017-11-01
 */
public enum ApplyStatus {

    APPLY(0),
    REFUSE(1),
    PASS(2);

    private int index;

    private ApplyStatus(int index) {
        this.index = index;
    }

    public static ApplyStatus getByIndex(int index) {
        switch (index) {
            case 0:
                return APPLY;
            case 1:
                return REFUSE;
            case 2:
                return PASS;
            default:
                return null;
        }
    }

    public String getName() {
        switch (index) {
            case 0:
                return "申请中";
            case 1:
                return "已拒绝";
            case 2:
                return "已通过";
            default:
                return "";
        }
    }
}
