package com.school.teachermanage.service;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.util.CommonUtil;
import com.school.teachermanage.util.DateUtil;
import com.school.teachermanage.util.QueryUtil;
import com.school.teachermanage.entity.UserLevelRecord;
import com.school.teachermanage.enumeration.DateStyle;
import com.school.teachermanage.repository.UserLevelRecordRepository;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 用户升级记录
 *
 * @author wudc
 * @date 2017-11-14
 */
@Service
public class UserLevelRecordService {
    private Logger logger = LoggerFactory.getLogger(UserLevelRecordService.class);
    @Autowired
    private UserLevelRecordRepository userLevelRecordRepository;

    public DataResult list(Date start, Date end, int page, int size, String mobile, DataResult result) {
        mobile = "%" + mobile + "%";
        PageRequest pageRequest = QueryUtil.buildPageRequest(page, size, "id", true);
        Page<UserLevelRecord> userPage = userLevelRecordRepository.findByMobileWithDate(mobile, start, end, pageRequest);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        JSONObject data = result.getData();
        data.put("page", CommonUtil.generatePageJSON(userPage));
        List<UserLevelRecord> userLevelRecords = userPage.getContent();
        JSONArray array = new JSONArray();
        if (userLevelRecords != null && userLevelRecords.size() > 0) {
            for (UserLevelRecord userLevelRecord : userLevelRecords) {
                JSONObject json = new JSONObject();
                json.put("id", userLevelRecord.getId());
                json.put("username", userLevelRecord.getUser().getUsername());
                json.put("changeFrom", userLevelRecord.getChangeFrom().getName());
                json.put("changeTo", userLevelRecord.getChangeTo().getName());
                json.put("remark", userLevelRecord.getRemark());
                json.put("adminName", userLevelRecord.getAdmin().getName());
                json.put("adminId", userLevelRecord.getAdmin().getId());
                json.put("userId", userLevelRecord.getUser().getId());
                json.put("createDate", DateUtil.dateToString(userLevelRecord.getCreateDate(), DateStyle.YYYY_MM_DD_HH_MM_SS));
                array.add(json);
            }
        }
        data.put("userLevelRecords", array);
        return result;
    }
}
