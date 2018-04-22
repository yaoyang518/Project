package com.school.teachermanage.service;


import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.constants.NumConstants;
import com.school.teachermanage.entity.AliPayAccount;
import com.school.teachermanage.entity.PayoutConfig;
import com.school.teachermanage.entity.TicketPercent;
import com.school.teachermanage.entity.User;
import com.school.teachermanage.enumeration.ErrorCode;
import com.school.teachermanage.repository.*;
import com.school.teachermanage.util.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 银行卡号服务类
 *
 * @author wudc
 * @date 2017/11/20
 */
@Service
public class AliPayAccountService {
    @Autowired
    private BankAccountReposity bankAccountReposity;
    @Autowired
    private AliPayAccountReposity aliPayAccountReposity;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PayoutConfigReposity payoutConfigReposity;
    @Autowired
    private TicketPercentRepository ticketPercentRepository;

    public DataResult save(AliPayAccount add, Long userId, DataResult result) {
        //判断用户合法性
        User user = userRepository.findOne(userId);
        if (user == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
            return result;
        }
        if (add == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.ALIPAY_INFO_ERROR);
            return result;
        }
        if (StringUtil.isEmpty(add.getAccountNumber())) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.ALIPAY_NUMBER_NO_EXIST);
            return result;
        }
        if (StringUtil.isEmpty(add.getUserName())) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.ALIPAY_NAME_NO_EXIST);
            return result;
        }
        AliPayAccount aliPayAccount = null;
        if (add.getId() != null) {
            aliPayAccount = aliPayAccountReposity.findOne(add.getId());
            if (aliPayAccount == null) {
                result.setMsg(MsgConstants.OPT_FAIL);
                result.setDataMsg(ErrorCode.ALIPAY_INFO_ERROR);
                return result;
            }
            AliPayAccount exist = aliPayAccountReposity.findByNameAndAccountAndUserId(add.getAccountNumber(), add.getUserName(), userId);
            if (exist != null && !exist.getId().equals(aliPayAccount.getId())) {
                result.setMsg(MsgConstants.OPT_FAIL);
                result.setDataMsg(ErrorCode.ALIPAY_EXIST);
                return result;
            }
            if (add.isAsDefault()) {
                AliPayAccount defaultAliPay = aliPayAccountReposity.findDefaultAliPayByUserId(userId);
                if (defaultAliPay != null && !defaultAliPay.getId().equals(add.getId())) {
                    defaultAliPay.setAsDefault(false);
                    aliPayAccountReposity.save(defaultAliPay);
                }
                aliPayAccount.setAsDefault(add.isAsDefault());
            }
            aliPayAccount.setUpdateDate(new Date());
        } else {
            aliPayAccount = new AliPayAccount();
            AliPayAccount exist = aliPayAccountReposity.findByNameAndAccountAndUserId(add.getAccountNumber(), add.getUserName(), userId);
            if (exist != null) {
                result.setMsg(MsgConstants.OPT_FAIL);
                result.setDataMsg(ErrorCode.ALIPAY_EXIST);
                return result;
            }
            //判断是否默认
            List<AliPayAccount> list = aliPayAccountReposity.findByUserId(userId);
            if (list == null || list.size() < 1) {
                aliPayAccount.setAsDefault(true);
            } else {
                if (add.isAsDefault()) {
                    AliPayAccount defaultAliPay = aliPayAccountReposity.findDefaultAliPayByUserId(user.getId());
                    if (defaultAliPay != null) {
                        defaultAliPay.setAsDefault(false);
                        aliPayAccountReposity.save(defaultAliPay);
                    }
                    aliPayAccount.setAsDefault(add.isAsDefault());
                }
            }
            aliPayAccount.setCreateDate(new Date());
        }
        aliPayAccount.setAccountNumber(add.getAccountNumber());
        aliPayAccount.setUserName(add.getUserName());
        aliPayAccount.setUserId(user.getId());
        aliPayAccountReposity.save(aliPayAccount);
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    public DataResult findCardsByUserId(Long userId, DataResult result) {
        //判断用户合法性
        User user = userRepository.findOne(userId);
        if (user == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
            return result;
        }
        List<AliPayAccount> aliPayAccounts = aliPayAccountReposity.findByUserId(userId);

        JSONArray array = new JSONArray();
        if (aliPayAccounts != null) {
            result.getData().put("total", aliPayAccounts.size());
            array = generateAliPayAccoutJsonArray(aliPayAccounts);

        }
        result.getData().put("aliPayAccounts", array);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        return result;
    }

    public DataResult deleteByUserId(Long userId, Long id, DataResult result) {
        AliPayAccount aliPayAccount = aliPayAccountReposity.findByUserIdAndId(userId, id);
        if (aliPayAccount == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_ALIPAY);
            return result;
        }
        aliPayAccountReposity.delete(aliPayAccount);
        if (aliPayAccount.isAsDefault()) {
            List<AliPayAccount> list = aliPayAccountReposity.findByUserId(userId);
            if(list!=null && list.size()>0){
                AliPayAccount newAlipay = list.get(0);
                newAlipay.setAsDefault(true);
                aliPayAccountReposity.save(newAlipay);
            }
        }
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    public int getAliPayAccounttSizeByUserId(Long userId) {
        return aliPayAccountReposity.getAliPayAccountSizeByUserId(userId);
    }

    public DataResult findById(Long id, Long userId, DataResult result) {
        AliPayAccount aliPayAccount;
        if (id == null) {
            aliPayAccount = aliPayAccountReposity.findDefaultAliPayByUserId(userId);
        } else {
            aliPayAccount = aliPayAccountReposity.findOne(id);
        }

        if (aliPayAccount == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_ALIPAY);
            return result;
        }
        if (!userId.equals(aliPayAccount.getUserId())) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_ALIPAY);
            return result;
        }
        JSONObject data = result.getData();
        PayoutConfig payoutConfig = payoutConfigReposity.findEnable();
        TicketPercent ticketPercent = ticketPercentRepository.findEnable();
        if (ticketPercent == null) {
            data.put("ticketPercent", 0.1);
        } else {
            BigDecimal divide = new BigDecimal(ticketPercent.getPercent()).divide(NumConstants.HUNDRED).setScale(NumConstants.TWO, BigDecimal.ROUND_HALF_DOWN);
            data.put("ticketPercent", divide);
        }
        data.put("payoutConfig", payoutConfig.getAmount());
        data.put("id", aliPayAccount.getId());
        data.put("name", aliPayAccount.getUserName());
        data.put("accountNumber", aliPayAccount.getAccountNumber());
        data.put("asDefault", aliPayAccount.isAsDefault());
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        return result;
    }

    public DataResult findDefualt(Long userId, DataResult result) {
        AliPayAccount aliPayAccount = aliPayAccountReposity.findDefaultAliPayByUserId(userId);
        if (aliPayAccount != null) {
            ArrayList<AliPayAccount> aliPayAccounts = new ArrayList<>();
            aliPayAccounts.add(aliPayAccount);
            JSONArray array = generateAliPayAccoutJsonArray(aliPayAccounts);
            result.getData().put("aliPay", array);
        } else {
            result.getData().put("aliPay", new JSONArray());
        }
        return result;
    }

    private JSONArray generateAliPayAccoutJsonArray(List<AliPayAccount> aliPayAccounts) {
        JSONArray array = new JSONArray();
        if (aliPayAccounts != null && aliPayAccounts.size() > 0) {
            for (AliPayAccount aliPayAccount : aliPayAccounts) {
                JSONObject json = new JSONObject();
                json.put("id", aliPayAccount.getId());
                json.put("accountNumber", aliPayAccount.getAccountNumber());
                json.put("name", aliPayAccount.getUserName());
                json.put("asDefault", aliPayAccount.isAsDefault());
                array.add(json);
            }
        }
        return array;
    }
}
