package com.school.teachermanage.service;


import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.constants.NumConstants;
import com.school.teachermanage.entity.BankAccount;
import com.school.teachermanage.entity.PayoutConfig;
import com.school.teachermanage.entity.TicketPercent;
import com.school.teachermanage.entity.User;
import com.school.teachermanage.enumeration.DateStyle;
import com.school.teachermanage.enumeration.ErrorCode;
import com.school.teachermanage.repository.BankAccountReposity;
import com.school.teachermanage.repository.PayoutConfigReposity;
import com.school.teachermanage.repository.TicketPercentRepository;
import com.school.teachermanage.repository.UserRepository;
import com.school.teachermanage.util.CommonUtil;
import com.school.teachermanage.util.DateUtil;
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
public class BankAccountService {
    @Autowired
    private BankAccountReposity bankAccountReposity;
    @Autowired
    private AliPayAccountService aliPayAccountService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PayoutConfigReposity payoutConfigReposity;
    @Autowired
    private TicketPercentRepository ticketPercentRepository;

    public DataResult save(BankAccount bankAccount, Long userId, DataResult result) {
        //判断用户合法性
        User user = userRepository.findOne(userId);
        if (user == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
            return result;
        }
        if (bankAccount == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.BANK_INFO_ERROR);
            return result;
        }
        //判断卡的信息是否合法
        String cardNo = bankAccount.getCardNo();
        if (cardNo == null || !CommonUtil.checkBankCard(cardNo)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.BANK_CARD_ERROR);
            return result;
        }
        //判断卡是否存在
        BankAccount exist = bankAccountReposity.findByCardNoAndUserId(cardNo, userId);
        if (exist != null && exist.getUserId().equals(userId)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.BANK_CARD_EXIST);
            return result;
        }
        String bank = bankAccount.getBank();
        if (bank.isEmpty()) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.BANK_INFO_ERROR);
            return result;
        }
        String branch = bankAccount.getBranch();
        if (branch.isEmpty()) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.BANK_INFO_ERROR);
            return result;
        }
        String userName = bankAccount.getUserName();
        if (userName.isEmpty()) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.BANK_INFO_ERROR);
            return result;
        }
        //判断默认是否
        List<BankAccount> cardsByUserId = bankAccountReposity.findCardsByUserId(userId);
        if (cardsByUserId == null || cardsByUserId.size() < 1) {
            //说明用户没有添加过
            bankAccount.setAsDefault(true);
        } else {
            if (bankAccount.isAsDefault()) {
                //取消默认银行卡
                BankAccount defaultBank = bankAccountReposity.findDefaultBankByUserId(user.getId());
                if (defaultBank != null) {
                    defaultBank.setAsDefault(false);
                    bankAccountReposity.save(defaultBank);
                }
            }
        }
        bankAccount.setCreateDate(new Date());
        bankAccount.setUpdateDate(new Date());
        bankAccount.setUserId(user.getId());
        bankAccountReposity.save(bankAccount);
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
        JSONArray array = new JSONArray();
        ;
        List<BankAccount> bankAccounts = bankAccountReposity.findCardsByUserId(userId);
        if (bankAccounts != null) {
            result.getData().put("total", bankAccounts.size());
            array = generateBankAccoutJsonArray(bankAccounts);
        }
        result.getData().put("bankAccounts", array);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        return result;
    }


    public DataResult deleteByUserId(Long userId, Long bankAccountId, DataResult result) {
        BankAccount bankAccount = bankAccountReposity.findByUserIdAndId(userId, bankAccountId);
        if (bankAccount == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.BANK_CARD_ERROR);
            return result;
        }
        bankAccountReposity.delete(bankAccount);
        if (bankAccount.isAsDefault()) {
            //设置其他的卡号为默认的，查询最新添加的卡号
            List<BankAccount> list = bankAccountReposity.findCardsByUserId(userId);
            if (list != null && list.size() > 0) {
                BankAccount account = list.get(0);
                account.setAsDefault(true);
                bankAccountReposity.save(account);
            }
        }
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    public DataResult findBankCardById(Long bankAccountId, Long userId, DataResult result) {
        BankAccount bankAccount;
        if (bankAccountId == null) {
            //查询用户默认银行卡
            bankAccount = bankAccountReposity.findDefaultBankByUserId(userId);
            if (bankAccount == null) {
                List<BankAccount> list = bankAccountReposity.findCardsByUserId(userId);
                if (list != null && !list.isEmpty()) {
                    bankAccount = list.get(0);
                    bankAccount.setAsDefault(true);
                    bankAccountReposity.save(bankAccount);
                }
            }
        } else {
            bankAccount = bankAccountReposity.findOne(bankAccountId);
        }

        if (bankAccount == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_BANKCARD);
            return result;
        }
        if (!userId.equals(bankAccount.getUserId())) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_BANKCARD);
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
        data.put("id", bankAccount.getId());
        data.put("cardNo", bankAccount.getCardNo());
        data.put("bank", bankAccount.getBank());
        data.put("asDefault", bankAccount.isAsDefault());
        data.put("branch", bankAccount.getBranch());
        data.put("username", bankAccount.getUserName());
        data.put("createDate", bankAccount.getCreateDate());
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        return result;
    }

    public DataResult update(BankAccount edit, Long userId, DataResult result) {
        BankAccount bankAccount = bankAccountReposity.findOne(edit.getId());
        if (bankAccount == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_BANKCARD);
            return result;
        }
        String cardNo = edit.getCardNo();
        if (!cardNo.isEmpty() && cardNo.length() > 1) {
            if (!CommonUtil.checkBankCard(cardNo)) {
                result.setMsg(MsgConstants.OPT_FAIL);
                result.setDataMsg(ErrorCode.BANK_CARD_ERROR);
                return result;
            }
            //判断卡是否存在
            BankAccount exist = bankAccountReposity.findByCardNoAndUserId(cardNo, userId);
            if (exist != null && exist.getUserId().equals(userId)) {
                if (!exist.getId().equals(edit.getId())) {
                    result.setMsg(MsgConstants.OPT_FAIL);
                    result.setDataMsg(ErrorCode.BANK_CARD_EXIST);
                    return result;
                }
            }
            //判断是否设为默认
            if (edit.isAsDefault()) {
                BankAccount defaultBank = bankAccountReposity.findDefaultBankByUserId(userId);
                if (defaultBank != null && !defaultBank.getId().equals(edit.getId())) {
                    defaultBank.setAsDefault(false);
                    bankAccountReposity.save(defaultBank);
                }
                bankAccount.setAsDefault(edit.isAsDefault());
            }
            bankAccount.setCardNo(cardNo);

        }

        String bank = edit.getBank();
        if (!bank.isEmpty()) {
            bankAccount.setBank(bank);
        }
        String branch = edit.getBranch();
        if (!branch.isEmpty()) {
            bankAccount.setBranch(branch);
        }
        String userName = edit.getUserName();
        if (!userName.isEmpty()) {
            bankAccount.setUserName(userName);
        }
        bankAccountReposity.save(bankAccount);
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    public int getBankAccountSizeByUserId(Long userId) {
        return bankAccountReposity.getBankAccountSizeByUserId(userId);
    }

    public DataResult findBankCardDefualt(Long userId, DataResult result) {
        result = aliPayAccountService.findDefualt(userId, result);
        BankAccount bankAccount = bankAccountReposity.findDefaultBankByUserId(userId);
        if(bankAccount==null) {
            List<BankAccount> list = bankAccountReposity.findCardsByUserId(userId);
            if (list != null && !list.isEmpty()) {
                bankAccount = list.get(0);
                bankAccount.setAsDefault(true);
                bankAccountReposity.save(bankAccount);
            }
        }
        if (bankAccount != null) {
            ArrayList<BankAccount> bankAccounts = new ArrayList<>();
            bankAccounts.add(bankAccount);
            JSONArray array = generateBankAccoutJsonArray(bankAccounts);
            result.getData().put("bankAccount", array);
        } else {
            result.getData().put("bankAccount", new JSONArray());
        }
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    private JSONArray generateBankAccoutJsonArray(List<BankAccount> bankAccounts) {
        JSONArray array = new JSONArray();
        if (bankAccounts != null && bankAccounts.size() > 0) {
            for (BankAccount bankAccount : bankAccounts) {
                JSONObject json = new JSONObject();
                json.put("id", bankAccount.getId());
                json.put("cardNo", bankAccount.getCardNo());
                json.put("bank", bankAccount.getBank());
                json.put("branch", bankAccount.getBranch());
                json.put("username", bankAccount.getUserName());
                json.put("asDefault", bankAccount.isAsDefault());
                json.put("createDate", DateUtil.dateToString(bankAccount.getCreateDate(), DateStyle.YYYY_MM_DD_HH_MM_SS));
                array.add(json);
            }
        }
        return array;
    }

}
