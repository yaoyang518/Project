package com.school.teachermanage.service;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.constants.NumConstants;
import com.school.teachermanage.entity.PayoutConfig;
import com.school.teachermanage.enumeration.ErrorCode;
import com.school.teachermanage.repository.PayoutConfigReposity;
import com.school.teachermanage.util.NumberUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 提现配置服务类
 *
 * @author wudc
 * @Date 2017/11/20.
 */
@Service
public class PayoutConfigService {
    @Autowired
    private PayoutConfigReposity payoutConfigReposity;

    public DataResult save(Long adminId, BigDecimal amount, DataResult result) {
        if(amount==null){
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.WITHDRAW_AMOUNT);
            return result;
        }
        if (!NumberUtil.isDivideAndRemainder(amount, NumConstants.HUNDRED)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.WITHDRAW_AMOUNT);
            return result;
        }
        PayoutConfig edit = payoutConfigReposity.findEnable();
        if (edit != null) {
            edit.setAvailable(false);
            payoutConfigReposity.save(edit);
        }
        PayoutConfig payoutConfig = new PayoutConfig();
        payoutConfig.setAvailable(true);
        payoutConfig.setAdminId(adminId);
        payoutConfig.setAmount(amount);
        payoutConfig.setCreateDate(new Date());
        payoutConfigReposity.save(payoutConfig);
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    public DataResult getPayoutConfigEnable(DataResult result) {
        PayoutConfig payoutConfig = payoutConfigReposity.findEnable();
        if(payoutConfig==null){
            result.setMsg(MsgConstants.QUERY_FAIL);
            result.setDataMsg(ErrorCode.WITHDRAW_NOT_EXIST);
            return result;
        }
        result.getData().put("amount",payoutConfig.getAmount());
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    public PayoutConfig findEnable() {
       return payoutConfigReposity.findEnable();
    }
}
