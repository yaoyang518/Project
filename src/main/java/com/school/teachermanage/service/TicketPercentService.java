package com.school.teachermanage.service;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.entity.Admin;
import com.school.teachermanage.entity.TicketPercent;
import com.school.teachermanage.enumeration.ErrorCode;
import com.school.teachermanage.enumeration.TicketPercentEnum;
import com.school.teachermanage.repository.AdminRepository;
import com.school.teachermanage.repository.TicketPercentRepository;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author wucdc
 * @Date 2017/11/27.
 */
@Service
public class TicketPercentService {
    @Resource
    private AdminRepository adminRepository;
    @Resource
    private TicketPercentRepository ticketPercentRepository;

    /**
     * 添加 转化率
     */
    public DataResult save(int percent, Long adminId, DataResult result) {
        //进行数据判断
        if (!TicketPercentEnum.getConvertPercentEnumByIndex(percent)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.TICKETPERCENT_NULL);
            return result;
        }
        Admin admin = adminRepository.findOne(adminId);
        if (admin ==  null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.TOKEN_ERROR);
            return result;
        }
        JSONObject data = result.getData();
        TicketPercent ticketPercent = ticketPercentRepository.findEnable();
        if (ticketPercent != null) {
            ticketPercent.setAvailable(false);
            ticketPercentRepository.save(ticketPercent);
        }
        TicketPercent add = new TicketPercent();
        add.setPercent(percent);
        add.setCreateDate(new Date());
        add.setAvailable(true);
        add.setAdmin(admin);
        ticketPercentRepository.save(add);
        data.put("percent", add.getPercent());
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    public TicketPercent findEnable() {
        return ticketPercentRepository.findEnable();
    }
}
