package com.school.teachermanage.service;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.constants.NumConstants;
import com.school.teachermanage.entity.*;
import com.school.teachermanage.enumeration.BalanceSource;
import com.school.teachermanage.enumeration.DateStyle;
import com.school.teachermanage.enumeration.ErrorCode;
import com.school.teachermanage.repository.BalanceRecordRepository;
import com.school.teachermanage.repository.PayoutConfigReposity;
import com.school.teachermanage.repository.UserRepository;
import com.school.teachermanage.util.CommonUtil;
import com.school.teachermanage.util.DateUtil;
import com.school.teachermanage.util.QueryUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 余额记录服务
 *
 * @author zhangsl
 * @date 2017/11/20
 */
@Service
public class BalanceRecordService {
    @Resource
    private UserRepository userRepository;
    @Resource
    private AccountService accountService;
    @Resource
    private BalanceRecordRepository balanceRecordRepository;
    @Resource
    private PayoutConfigReposity payoutConfigReposity;

    /**
     * @param admin      管理员
     * @param minus      是否减少
     * @param source     来源
     * @param user       用户
     * @param consumer   消费人
     * @param sourceUser 直接来源
     * @param level      层数
     * @param amount     金额
     * @param balance    总额
     * @param remark     标记
     */
    public BalanceRecord generateBalanceRecord(Admin admin, boolean minus, BalanceSource source,
                                               User user, User consumer, User sourceUser, int level,
                                               BigDecimal amount, BigDecimal balance, String remark, PayoutRecord payoutRecord) {
        BalanceRecord balanceRecord = new BalanceRecord();
        balanceRecord.setCreateDate(new Date());
        balanceRecord.setMinus(minus);
        balanceRecord.setAdmin(admin);
        balanceRecord.setUser(user);
        balanceRecord.setBalanceSource(source);
        balanceRecord.setAmount(amount);
        balanceRecord.setBalance(balance);
        balanceRecord.setConsumeUser(consumer);
        balanceRecord.setLevel(level);
        balanceRecord.setSourceUser(sourceUser);
        balanceRecord.setRemark(remark);
        balanceRecord.setPayoutRecord(payoutRecord);
        balanceRecordRepository.save(balanceRecord);
        return balanceRecord;
    }

    public DataResult findRecordsByUserId(Long userId, int page, int size, DataResult result) {
        User user = userRepository.findOne(userId);
        if (user == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
            return result;
        }
        Account account = accountService.findByUser(user);
        PageRequest pageRequest = QueryUtil.buildPageRequest(page, size, "id", true);
        Page<BalanceRecord> balanceRecordPage = balanceRecordRepository.findRecordsByUserId(userId, pageRequest);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        JSONObject data = result.getData();
        List<BalanceRecord> balanceRecords = balanceRecordPage.getContent();
        //获取提现配置
        PayoutConfig payoutConfig = payoutConfigReposity.findEnable();
        boolean weekend = DateUtil.isWeedend(new Date());
        data.put("weekend",weekend);
        data.put("payoutConfig",payoutConfig.getAmount());
        JSONArray array = generateBalanceRecordJsonArray(balanceRecords);
        data.put("balanceRecords", array);
        data.put("balance",account.getBalance());
        data.put("page", CommonUtil.generatePageJSON(balanceRecordPage));
        return result;
    }

    private JSONArray generateBalanceRecordJsonArray(List<BalanceRecord> balanceRecords) {
        JSONArray array = new JSONArray();
        for (BalanceRecord balanceRecord : balanceRecords) {
            JSONObject json = new JSONObject();
            json.put("createDate", DateUtil.dateToString(balanceRecord.getCreateDate(), DateStyle.YYYY_MM_DD_HH_MM_SS));
            json.put("balanceSource",balanceRecord.getBalanceSource().getName());
            PayoutRecord payoutRecord = balanceRecord.getPayoutRecord();

            json.put("minus",balanceRecord.isMinus());
            json.put("amount",balanceRecord.getAmount().setScale(NumConstants.TWO, BigDecimal.ROUND_HALF_DOWN));
            json.put("balance",balanceRecord.getBalance().setScale(NumConstants.TWO, BigDecimal.ROUND_HALF_DOWN));
            array.add(json);
        }
         return array;
    }
}
