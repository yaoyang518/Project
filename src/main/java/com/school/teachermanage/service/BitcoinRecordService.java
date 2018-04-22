package com.school.teachermanage.service;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.constants.NumConstants;
import com.school.teachermanage.entity.Account;
import com.school.teachermanage.entity.Admin;
import com.school.teachermanage.entity.BitcoinRecord;
import com.school.teachermanage.entity.User;
import com.school.teachermanage.enumeration.BitcoinSource;
import com.school.teachermanage.enumeration.DateStyle;
import com.school.teachermanage.repository.AccountReposity;
import com.school.teachermanage.repository.BitcoinRecordRepository;
import com.school.teachermanage.util.CommonUtil;
import com.school.teachermanage.util.DateUtil;
import com.school.teachermanage.util.QueryUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author wudc
 * @Date 2017/11/28.
 */
@Service
public class BitcoinRecordService {
    @Autowired
    private BitcoinRecordRepository bitcoinRecordRepository;
    @Autowired
    private AccountReposity accountReposity;

    /**
     * @param admin   管理员
     * @param minus   是否减少
     * @param source  来源
     * @param user    用户
     * @param user    直接来源
     * @param amount  金额
     * @param bitcoin 总额
     * @param remark  标记
     */
    public BitcoinRecord generateBitcoinRecord(Admin admin, boolean minus, BitcoinSource source,
                                               User user, User person, BigDecimal amount, BigDecimal bitcoin, String remark) {
        BitcoinRecord balanceRecord = new BitcoinRecord();
        balanceRecord.setCreateDate(new Date());
        balanceRecord.setMinus(minus);
        balanceRecord.setAdmin(admin);
        balanceRecord.setUser(user);
        balanceRecord.setBitcoinSource(source);
        balanceRecord.setAmount(amount);
        balanceRecord.setTotal(bitcoin);
        balanceRecord.setPerson(person);
        balanceRecord.setRemark(remark);
        bitcoinRecordRepository.save(balanceRecord);
        return balanceRecord;
    }

    public DataResult list(String mobile, Date start, Date end, int page, int size, DataResult result) {
        if (mobile != null) {
            mobile = "%" + mobile + "%";
        }else{
            mobile="%"+""+"%";
        }
        PageRequest pageRequest = QueryUtil.buildPageRequest(page, size, "id", true);
        Page<BitcoinRecord>  bitcoinRecorduserPage = bitcoinRecordRepository.findByMobileAndStatusWithDate(mobile, start, end, pageRequest);
        JSONObject data = result.getData();
        data.put("page", CommonUtil.generatePageJSON(bitcoinRecorduserPage));
        List<BitcoinRecord> bitcoinRecords = bitcoinRecorduserPage.getContent();
        JSONArray array = this.generateBitcoinRecordJsonArray(bitcoinRecords);
        data.put("bitcoinRecords", array);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        return result;
    }

    private JSONArray generateBitcoinRecordJsonArray(List<BitcoinRecord> bitcoinRecords) {
        JSONArray array = new JSONArray();
        for (BitcoinRecord bitcoinRecord : bitcoinRecords) {
            JSONObject json = new JSONObject();
            json.put("id", bitcoinRecord.getId());
            Admin admin = bitcoinRecord.getAdmin();
            if(admin!=null){
                json.put("adminId", bitcoinRecord.getAdmin().getId());
                json.put("adminName", bitcoinRecord.getAdmin().getName());
            }
            json.put("amount", bitcoinRecord.getAmount().setScale(NumConstants.TWO, BigDecimal.ROUND_FLOOR));
            json.put("createDate", DateUtil.dateToString(bitcoinRecord.getCreateDate(), DateStyle.YYYY_MM_DD_HH_MM_SS));
            json.put("bitcoinSource", bitcoinRecord.getBitcoinSource().getName());
            User person = bitcoinRecord.getPerson();
            if(person!=null){
                json.put("person", person.getId());
                json.put("personName", person.getUsername());
            }
            json.put("total", bitcoinRecord.getTotal());
            json.put("minus", bitcoinRecord.isMinus());
            json.put("remark", bitcoinRecord.getRemark());
            json.put("username", bitcoinRecord.getUser().getUsername());
            json.put("userId", bitcoinRecord.getUser().getId());
            array.add(json);
        }
        return array;
    }

    public DataResult findByUserId(Long userId, int page, int size, DataResult result) {
        PageRequest pageRequest = QueryUtil.buildPageRequest(page, size, "id", true);
        Page<BitcoinRecord>  bitcoinRecorduserPage = bitcoinRecordRepository.findByUserId(userId, pageRequest);
        Account account = accountReposity.findByUserId(userId);
        JSONObject data = result.getData();
        data.put("bitcoinTotal",account.getBitcoin());
        data.put("page", CommonUtil.generatePageJSON(bitcoinRecorduserPage));
        List<BitcoinRecord> bitcoinRecords = bitcoinRecorduserPage.getContent();
        JSONArray array = this.generateBitcoinRecordJsonArray(bitcoinRecords);
        data.put("bitcoinRecords", array);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        return result;
    }
}
