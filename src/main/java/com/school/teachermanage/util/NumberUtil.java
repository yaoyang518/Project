package com.school.teachermanage.util;

import com.school.teachermanage.constants.NumConstants;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * number util
 *
 * @author wudc
 * @date 2017/11/6.
 */
public class NumberUtil {
    /**
     * 判断数据是否在数组
     */
    public static boolean containNumber(int number, Integer[] numbers) {
        List<Integer> list = Arrays.asList(numbers);
        return list.contains(number);
    }

    public static boolean maxInThirdNumber(int firstMultiple, int secondLevel, int thirdMultiple) {
        if (firstMultiple > secondLevel && firstMultiple > thirdMultiple) {
            return false;
        } else if (secondLevel > thirdMultiple) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 大于判断
     *
     * @param a
     * @param b
     * @return a > b
     */
    public static boolean isGreater(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) == NumConstants.ONE;
    }

    /**
     * 大于等于
     *
     * @param a
     * @param b
     * @return a >= b
     */
    public static boolean isGreaterOrEqual(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) >= NumConstants.ZERO;
    }

    /**
     * 小于
     *
     * @param a
     * @param b
     * @return a < b
     */
    public static boolean isLess(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) < NumConstants.ZERO;
    }

    /**
     * 小于等于
     *
     * @param a
     * @param b
     * @return a <= b
     */
    public static boolean isLessOrEqual(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) < NumConstants.ONE;
    }

    /**
     * 是否整除
     *
     * @param a
     * @param b
     * @return a % b
     */
    public static boolean isDivideAndRemainder(BigDecimal a, BigDecimal b) {
        if (a.compareTo(BigDecimal.ZERO) == 1) {
            BigDecimal[] results = a.divideAndRemainder(b);
            return results[1].compareTo(BigDecimal.ZERO) == 0 ? true : false;
        }
        return false;
    }

}
