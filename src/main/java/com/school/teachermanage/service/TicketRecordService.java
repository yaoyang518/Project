package com.school.teachermanage.service;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.constants.NumConstants;
import com.school.teachermanage.entity.Account;
import com.school.teachermanage.entity.Admin;
import com.school.teachermanage.entity.TicketRecord;
import com.school.teachermanage.entity.User;
import com.school.teachermanage.enumeration.DateStyle;
import com.school.teachermanage.enumeration.TicketSource;
import com.school.teachermanage.repository.AccountReposity;
import com.school.teachermanage.repository.TicketRecordRepository;
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
public class TicketRecordService {
    @Autowired
    private TicketRecordRepository ticketRecordRepository;
    @Autowired
    private AccountReposity accountReposity;
    /**
     * @param admin   管理员
     * @param minus   是否减少
     * @param source  来源
     * @param user    用户
     * @param user    直接来源
     * @param amount  金额
     * @param ticket 总额
     * @param remark  标记
     */
    public TicketRecord generateTicketRecord(Admin admin, boolean minus, TicketSource source,
                                             User user, BigDecimal amount, BigDecimal ticket, String remark) {
        TicketRecord ticketRecord = new TicketRecord();
        ticketRecord.setCreateDate(new Date());
        ticketRecord.setMinus(minus);
        ticketRecord.setAdmin(admin);
        ticketRecord.setUser(user);
        ticketRecord.setTicketSource(source);
        ticketRecord.setAmount(amount);
        ticketRecord.setTotal(ticket);
        ticketRecord.setRemark(remark);
        ticketRecordRepository.save(ticketRecord);
        return ticketRecord;
    }

    public DataResult list(String mobile, Date start, Date end, int page, int size, DataResult result) {
        if (mobile != null) {
            mobile = "%" + mobile + "%";
        }else{
            mobile="%"+""+"%";
        }
        PageRequest pageRequest = QueryUtil.buildPageRequest(page, size, "id", true);
        Page<TicketRecord> ticketRecordRecorduserPage = ticketRecordRepository.findByMobileAndStatusWithDate(mobile, start, end, pageRequest);
        JSONObject data = result.getData();
        data.put("page", CommonUtil.generatePageJSON(ticketRecordRecorduserPage));
        List<TicketRecord> ticketRecords = ticketRecordRecorduserPage.getContent();
        JSONArray array = this.generateTicketRecordJsonArray(ticketRecords);
        data.put("ticketRecords", array);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        return result;
    }

    private JSONArray generateTicketRecordJsonArray(List<TicketRecord> ticketRecords) {
        JSONArray array = new JSONArray();
        for (TicketRecord ticketRecord : ticketRecords) {
            JSONObject json = new JSONObject();
            json.put("id", ticketRecord.getId());
            Admin admin = ticketRecord.getAdmin();
            if(admin!=null){
                json.put("adminId", ticketRecord.getAdmin().getId());
                json.put("adminName", ticketRecord.getAdmin().getName());
            }
            json.put("amount", ticketRecord.getAmount().setScale(NumConstants.TWO, BigDecimal.ROUND_FLOOR));
            json.put("createDate", DateUtil.dateToString(ticketRecord.getCreateDate(), DateStyle.YYYY_MM_DD_HH_MM_SS));
            json.put("bitcoinSource", ticketRecord.getTicketSource().getName());
            json.put("total", ticketRecord.getTotal());
            json.put("minus", ticketRecord.isMinus());
            json.put("remark", ticketRecord.getRemark());
            json.put("username", ticketRecord.getUser().getUsername());
            json.put("userId", ticketRecord.getUser().getId());
            array.add(json);
        }
        return array;
    }

    public DataResult findByUserId(Long userId, int page, int size, DataResult result) {
        PageRequest pageRequest = QueryUtil.buildPageRequest(page, size, "id", true);
        Page<TicketRecord>  ticketRecorduserPage = ticketRecordRepository.findByUserId(userId, pageRequest);
        Account account = accountReposity.findByUserId(userId);
        JSONObject data = result.getData();
        data.put("ticketTotal",account.getTicket());
        data.put("page", CommonUtil.generatePageJSON(ticketRecorduserPage));
        List<TicketRecord> ticketRecords = ticketRecorduserPage.getContent();
        JSONArray array = this.generateTicketRecordJsonArray(ticketRecords);
        data.put("ticketRecords", array);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        return result;
    }
}
