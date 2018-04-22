package com.school.teachermanage.service;


import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.constants.NumConstants;
import com.school.teachermanage.entity.*;
import com.school.teachermanage.enumeration.*;
import com.school.teachermanage.repository.*;
import com.school.teachermanage.util.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 提现记录服务类
 *
 * @author isWeedend
 * @Date 2017/11/20.
 */
@Service
public class PayoutRecordService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PayoutConfigReposity payoutConfigReposity;
    @Autowired
    private BankAccountReposity bankAccountReposity;
    @Autowired
    private AccountReposity accountReposity;
    @Autowired
    private PayoutRecordReposity payoutRecordReposity;
    @Autowired
    private BalanceRecordService balanceRecordService;
    @Autowired
    private TicketPercentService ticketPercentService;
    @Autowired
    private TicketRecordService ticketRecordService;
    @Autowired
    private AliPayAccountReposity aliPayAccountReposity;

    public DataResult appay(Long userId, Long id, BigDecimal amount, String type, DataResult result) {
        User user = userRepository.findOne(userId);
        //判断用户是否存在
        if (user == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
            return result;
        }
        if(StringUtil.isEmpty(type)){
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.PAYOUT_RECORD_ERROR);
            return result;
        }
        PayoutConfig payoutConfig = payoutConfigReposity.findEnable();
        //判断提现的金额是否是基础金额的倍数
        if (!NumberUtil.isDivideAndRemainder(amount, payoutConfig.getAmount())) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.WITHDRAW_AMOUNT);
            return result;
        }
        //判断 用户金额是否足够
        Account account = accountReposity.findByUser(user);
        BigDecimal balance = account.getBalance();
        if (NumberUtil.isLess(balance, amount)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.BALANCE_LESS_ERROR);
            return result;
        }
        TicketPercent ticketPercent = ticketPercentService.findEnable();
        if (ticketPercent == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.TICKETPERCENT_NULL);
            return result;
        }

        //生成提现申请记录
        PayoutRecord payoutRecord = new PayoutRecord();
        if(PayMethod.getByName(type).equals(PayMethod.ALIPAY)){
            AliPayAccount aliPayAccount = aliPayAccountReposity.findOne(id);
            if(aliPayAccount==null){
                result.setMsg(MsgConstants.OPT_FAIL);
                result.setDataMsg(ErrorCode.USER_NOT_ALIPAY);
                return result;
            }
            payoutRecord.setCardNo(aliPayAccount.getAccountNumber());
            payoutRecord.setUserName(aliPayAccount.getUserName());
        }else if(PayMethod.getByName(type).equals(PayMethod.BANKCARD)){
            //判断银行账户是否正确
            BankAccount bankAccount = bankAccountReposity.findOne(id);
            if (bankAccount == null) {
                result.setMsg(MsgConstants.OPT_FAIL);
                result.setDataMsg(ErrorCode.BANK_CARD_ERROR);
                return result;
            }
            payoutRecord.setBranch(bankAccount.getBranch());
            payoutRecord.setCardNo(bankAccount.getCardNo());
            payoutRecord.setUserName(bankAccount.getUserName());
            payoutRecord.setBank(bankAccount.getBank());
        }
        BigDecimal percent = new BigDecimal(ticketPercent.getPercent()).divide(NumConstants.HUNDRED)
                .setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN);

        BigDecimal ticket = percent.multiply(amount).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN);
        account.setBalance(balance.subtract(amount).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN));
        account.setTicket(account.getTicket().add(ticket).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN));
        accountReposity.save(account);
        payoutRecord.setAmount(amount);
        payoutRecord.setCash(amount.subtract(ticket).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN));
        payoutRecord.setApplyDate(new Date());
        payoutRecord.setApplyStatus(ApplyStatus.APPLY);
        payoutRecord.setPayMethod(PayMethod.getByName(type));
        payoutRecord.setUserId(user.getId());
        payoutRecord.setMobile(user.getMobile());
        payoutRecordReposity.save(payoutRecord);
        //生成余额记录
        balanceRecordService.generateBalanceRecord(null, true,
                BalanceSource.WITHDRAW_CASH_APPAY,
                user, user, user, 0, amount,
                account.getBalance(),
                BalanceSource.WITHDRAW_CASH_APPAY.getName(),payoutRecord);
        //生成消费劵记录
        ticketRecordService.generateTicketRecord(null, false, TicketSource.CONVERT, user, ticket, account.getTicket(), "提现转换");
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    public DataResult payoutRecordApprove(Long adminId, Long payoutRecordId, String tradeNo, DataResult result) {
        PayoutRecord payoutRecord = payoutRecordReposity.findOne(payoutRecordId);
        if (payoutRecord == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.PAYOUT_RECORD_ERROR);
            return result;
        }
        if (tradeNo.isEmpty()) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.TRADENO_ISEMPTY);
            return result;
        }
        if (payoutRecord.getApplyStatus().compareTo(ApplyStatus.APPLY) != 0) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.PAYOUT_RECORD_ERROR);
            return result;
        }
        payoutRecord.setTradeNo(tradeNo);
        payoutRecord.setAdminId(adminId);
        payoutRecord.setApplyStatus(ApplyStatus.PASS);
        payoutRecord.setPayDate(new Date());
        payoutRecord.setUpdateDate(new Date());
        payoutRecordReposity.save(payoutRecord);
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    public DataResult payoutRecordReject(Long adminId, Long payoutRecordId, String remark, DataResult result) {
        PayoutRecord payoutRecord = payoutRecordReposity.findOne(payoutRecordId);
        if (payoutRecord == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.PAYOUT_RECORD_ERROR);
            return result;
        }
        if (remark.isEmpty()) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.REJECT_REMARK_ISEMPTY);
            return result;
        }
        if (payoutRecord.getApplyStatus().compareTo(ApplyStatus.APPLY) != 0) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.PAYOUT_RECORD_ERROR);
            return result;
        }
        payoutRecord.setRemark(remark);
        payoutRecord.setAdminId(adminId);
        payoutRecord.setApplyStatus(ApplyStatus.REFUSE);
        payoutRecord.setRefuseDate(new Date());
        payoutRecord.setUpdateDate(new Date());

        User user = userRepository.findOne(payoutRecord.getUserId());
        Account account = accountReposity.findByUser(user);
        account.setBalance(account.getBalance().add(payoutRecord.getAmount()).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN));
        BigDecimal ticket = payoutRecord.getAmount().subtract(payoutRecord.getCash()).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN);
        account.setTicket(account.getTicket().subtract(ticket).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN));
        accountReposity.save(account);
        payoutRecordReposity.save(payoutRecord);
        //生成记录
        balanceRecordService.generateBalanceRecord(null, false,
                BalanceSource.WITHDRAW_CASH_FAIL,
                user, user, user, 0, payoutRecord.getAmount(),
                account.getBalance(),
                BalanceSource.WITHDRAW_CASH_FAIL.getName(),payoutRecord);
        ticketRecordService.generateTicketRecord(null, true, TicketSource.CONVERT, user, ticket, account.getTicket(), "提现拒绝转换");
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    public DataResult list(Date start, Date end, int page, int size, String mobile, String cardNo, ApplyStatus applyStatus, DataResult result) {
        if (mobile != null) {
            mobile = "%" + mobile + "%";
        } else {
            mobile = "%" + "" + "%";
        }
        if (cardNo != null) {
            cardNo = "%" + cardNo + "%";
        } else {
            cardNo = "%" + "" + "%";
        }
        PageRequest pageRequest = QueryUtil.buildPageRequest(page, size, "id", true);
        Page<PayoutRecord> userPage;
        if (applyStatus != null) {
            userPage = payoutRecordReposity.findByMobileAndApplyStatusWithDate(mobile, cardNo, applyStatus, start, end, pageRequest);
        } else {
            userPage = payoutRecordReposity.findByMobileWithDate(mobile, cardNo, start, end, pageRequest);
        }
        JSONObject data = result.getData();
        data.put("page", CommonUtil.generatePageJSON(userPage));
        List<PayoutRecord> payoutRecords = userPage.getContent();
        JSONArray array = this.generatePayoutRecordJsonArray(payoutRecords);
        data.put("payoutRecords", array);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        return result;
    }

    private JSONArray generatePayoutRecordJsonArray(List<PayoutRecord> payoutRecords) {
        JSONArray array = new JSONArray();
        for (PayoutRecord payoutRecord : payoutRecords) {
            JSONObject json = new JSONObject();
            json.put("id", payoutRecord.getId());
            json.put("adminId", payoutRecord.getAdminId());
            BigDecimal cash = payoutRecord.getCash();
            if(cash!=null){
                json.put("cash",cash.setScale(NumConstants.TWO, BigDecimal.ROUND_FLOOR));
            }
            PayMethod payMethod = payoutRecord.getPayMethod();
            if(payMethod!=null){
                json.put("payMethod", payMethod.getName());
            }
            json.put("amount", payoutRecord.getAmount().setScale(NumConstants.TWO, BigDecimal.ROUND_FLOOR));
            json.put("applyDate", DateUtil.dateToString(payoutRecord.getApplyDate(), DateStyle.YYYY_MM_DD_HH_MM_SS));
            json.put("updateDate", DateUtil.dateToString(payoutRecord.getUpdateDate(), DateStyle.YYYY_MM_DD_HH_MM_SS));
            json.put("payDate", DateUtil.dateToString(payoutRecord.getPayDate(), DateStyle.YYYY_MM_DD_HH_MM_SS));
            json.put("refuseDate", DateUtil.dateToString(payoutRecord.getRefuseDate(), DateStyle.YYYY_MM_DD_HH_MM_SS));
            json.put("applyStatus", payoutRecord.getApplyStatus().getName());
            json.put("bank", payoutRecord.getBank());
            json.put("branch", payoutRecord.getBranch());
            json.put("cardNo", payoutRecord.getCardNo());
            json.put("mobile", payoutRecord.getMobile());
            json.put("remark", payoutRecord.getRemark());
            json.put("tradeNo", payoutRecord.getTradeNo());
            json.put("username", payoutRecord.getUserName());
            json.put("userId", payoutRecord.getUserId());
            array.add(json);
        }
        return array;
    }

    public DataResult exportExcel(String mobile, String cardNo, Date start, Date end, ApplyStatus applyStatus, DataResult result) {
        if (mobile != null) {
            mobile = "%" + mobile + "%";
        } else {
            mobile = "%" + "" + "%";
        }
        if (cardNo != null) {
            cardNo = "%" + cardNo + "%";
        } else {
            cardNo = "%" + "" + "%";
        }
        int account;
        List<PayoutRecord> list;
        if (applyStatus == null) {
            account = payoutRecordReposity.getDownLoadUserAccount(mobile, cardNo, start, end);
            list = payoutRecordReposity.findDownLoadUsers(mobile, cardNo, start, end);
        } else {
            account = payoutRecordReposity.getDownLoadUserByAppStatusAccount(mobile, cardNo, applyStatus, start, end);
            list = payoutRecordReposity.findDownLoadByAppStatusUsers(mobile, cardNo, applyStatus, start, end);
        }

        if (account > NumConstants.DOWNLOAD_LIMIT) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.DOWNLOAD_OVERSTEP);
        } else {
            JSONArray array = this.generatePayoutRecordJsonArray(list);
            result.getData().put("payoutRecords", array);
        }
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        return result;
    }

    public DataResult findPayoutRecordById(Long id, DataResult result) {
        PayoutRecord payoutRecord = payoutRecordReposity.findOne(id);
        if (payoutRecord == null) {
            result.setMsg(MsgConstants.QUERY_FAIL);
            result.setDataMsg(ErrorCode.PAYOUT_RECORD_ERROR);
            return result;
        }
        List<PayoutRecord> list = new ArrayList<>();
        list.add(payoutRecord);
        JSONArray array = generatePayoutRecordJsonArray(list);
        result.getData().put("payoutRecord", array);
        return result;
    }
}
