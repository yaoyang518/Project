package com.school.teachermanage.util;

import com.school.teachermanage.constants.NumConstants;
import net.sf.json.JSONObject;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

/**
 * 通用工具类
 *
 * @author zhangsl
 * @date 2017-11-01
 */
public class CommonUtil {

    public static boolean isRightPhone(String number) {
        String regex = "^(13|15|18|17)\\d{9}$";
        return StringUtil.isNotEmpty(number) && number.matches(regex);
    }

    public static boolean isRightPassword(String password) {
        String regex = "^(?![a-zA-z]+$)(?!\\d+$)[a-zA-Z\\d].{5,9}$";
        return password.length() > 5 && password.length() < 11 && password.matches(regex);
    }

    public static boolean isRightDate(String date) {
        String regex = "^20\\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$";
        return StringUtil.isNotEmpty(date) && date.matches(regex);
    }

    public static boolean isRightDateTime(String date) {
        String regex = "^20\\d{2}\\-(0?[1-9]|1[0-2])\\-(0?[1-9]|[12]\\d|3[01])\\s*(0?[1-9]|1\\d|2[0-3])(\\:(0?[1-9]|[1-5]\\d)){2}$";
        return StringUtil.isNotEmpty(date) && date.matches(regex);
    }

    /**
     * 校验银行卡卡号
     *
     * @param card
     * @return
     */
    public static boolean checkBankCard(String card) {
        String regex = "^([1-9]{1})(\\d{14}|\\d{15}|\\d{18})$";
        if (!card.matches(regex)) {
            return false;
        } else {
            char bit = getBankCardCheckCode(card
                    .substring(0, card.length() - 1));
            return card.charAt(card.length() - 1) == bit;
        }
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     *
     * @param nonCheckCodeCardId
     * @return
     */
    public static char getBankCardCheckCode(String nonCheckCodeCardId) {
        int cardLenth = nonCheckCodeCardId.trim().length();
        if (nonCheckCodeCardId == null || cardLenth == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            return 0;
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }

    public static JSONObject generatePageJSON(Page page) {
        JSONObject json = new JSONObject();
        json.put("total", page.getTotalElements());
        json.put("totalPage", page.getTotalPages());
        json.put("size", page.getSize());
        json.put("page", page.getNumber() + 1);
        json.put("hasNext", page.hasNext());
        json.put("hasPrevious", page.hasPrevious());
        json.put("isFirst", page.isFirst());
        json.put("isLast", page.isLast());
        return json;
    }


    public static void main(String[] args) {

        System.out.println(checkBankCard("6222031202000387109"));

        BigDecimal percent = new BigDecimal(3).divide(NumConstants.HUNDRED);
        System.out.println(percent);

        /*

        BigDecimal amount = new BigDecimal("11239");
        System.out.println(amount.divide(NumConstants.THOUSAND).setScale(0,BigDecimal.ROUND_HALF_DOWN));

        //上级收益
        int level = 1;
        BigDecimal percent = new BigDecimal(0.12);
        BigDecimal limit = new BigDecimal(0.0000001);
        BigDecimal benifit = amount.multiply(percent).setScale(7, BigDecimal.ROUND_HALF_UP);
        while (benifit.compareTo(limit) > 0){

            benifit = amount.multiply(percent).setScale(7, BigDecimal.ROUND_HALF_UP);
            if (benifit.compareTo(limit) < 1){
                break;
            }
            //比例转换
            percent = percent.multiply(new BigDecimal(0.5));
            level++;
        }
*/
    }


}
