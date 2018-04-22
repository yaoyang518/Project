package com.school.teachermanage.service;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.entity.RechargeRecord;
import com.school.teachermanage.entity.User;
import com.school.teachermanage.enumeration.DateStyle;
import com.school.teachermanage.enumeration.ErrorCode;
import com.school.teachermanage.repository.RechargeRecordRepository;
import com.school.teachermanage.repository.UserRepository;
import com.school.teachermanage.util.CommonUtil;
import com.school.teachermanage.util.DateUtil;
import com.school.teachermanage.util.QueryUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * recharge 记录数据
 * @author wudc
 * @date 2017-11-06
 */
@Service
public class RechargeRecordService {
    @Autowired
    private RechargeRecordRepository rechargeRecordRepository;
    @Autowired
    private UserRepository UserRepository;
    public DataResult findRechargeRecordsByDate(Date start, Date end, String mobile, int page, int size, DataResult result) {
        if (mobile != null) {
            mobile = "%" + mobile + "%";
        } else {
            mobile = "%" + "" + "%";
        }
        PageRequest pageRequest = QueryUtil.buildPageRequest(page, size, "id", true);
        Page<RechargeRecord> rechargeRecordPage = rechargeRecordRepository.findRechargeRecordsByDateAndMobile(mobile,start, end, pageRequest);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        JSONObject data = result.getData();
        data.put("page", CommonUtil.generatePageJSON(rechargeRecordPage));
        List<RechargeRecord> rechargeRecords = rechargeRecordPage.getContent();
        JSONArray array = new JSONArray();
        if (rechargeRecords != null && rechargeRecords.size() > 0) {
            for (RechargeRecord rechargeRecord : rechargeRecords) {
                JSONObject json = new JSONObject();
                json.put("id", rechargeRecord.getId());
                json.put("amount", rechargeRecord.getAmount());
                json.put("createDate", DateUtil.dateToString(rechargeRecord.getCreateDate(), DateStyle.YYYY_MM_DD_HH_MM_SS));
                json.put("userName", rechargeRecord.getUser().getUsername());
                json.put("mobile", rechargeRecord.getUser().getMobile());
                json.put("userId", rechargeRecord.getUser().getId());
                array.add(json);
            }
        }
        data.put("rechargeRecords", array);
        return result;
    }

    public DataResult findRechargeRecordsByUserIdWithDate(User u, Date start, Date end, int page, int size, DataResult result) {
        PageRequest pageRequest = QueryUtil.buildPageRequest(page, size, "id", true);
        User user = UserRepository.findByMobile(u.getMobile());
        if(user==null){
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
            return result;
        }
        Page<RechargeRecord> rechargeRecordPage = rechargeRecordRepository.findRechargeRecordsByUserIdWithDate(user.getId(),start, end, pageRequest);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        JSONObject data = result.getData();
        data.put("page", CommonUtil.generatePageJSON(rechargeRecordPage));
        List<RechargeRecord> rechargeRecords = rechargeRecordPage.getContent();
        JSONArray array = new JSONArray();
        if (rechargeRecords != null && rechargeRecords.size() > 0) {
            for (RechargeRecord rechargeRecord : rechargeRecords) {
                JSONObject json = new JSONObject();
                json.put("id", rechargeRecord.getId());
                json.put("amount", rechargeRecord.getAmount());
                array.add(json);
            }
        }
        data.put("users", array);
        return result;
    }
}
