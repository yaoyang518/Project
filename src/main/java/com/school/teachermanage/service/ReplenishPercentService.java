package com.school.teachermanage.service;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.entity.Admin;
import com.school.teachermanage.entity.ReplenishPercent;
import com.school.teachermanage.enumeration.ErrorCode;
import com.school.teachermanage.enumeration.ReplenishPercentEnum;
import com.school.teachermanage.repository.AdminRepository;
import com.school.teachermanage.repository.ReplenishPercentRepository;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author wucdc
 * @Date 2017/11/27.
 */
@Service
public class ReplenishPercentService {
    @Resource
    private AdminRepository adminRepository;
    @Resource
    private ReplenishPercentRepository replenishPercentRepository;

    /**
     * 添加 转化率
     */
    public DataResult save(int percent, Long adminId, DataResult result) {
        //进行数据判断
        if (!ReplenishPercentEnum.getConvertPercentEnumByIndex(percent)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.CONVERT_PERCENT_ERROR);
            return result;
        }
        Admin admin = adminRepository.findOne(adminId);
        if (admin ==  null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.TOKEN_ERROR);
            return result;
        }
        JSONObject data = result.getData();
        ReplenishPercent ReplenishPercent = replenishPercentRepository.findEnable();
        if (ReplenishPercent != null) {
            ReplenishPercent.setAvailable(false);
            replenishPercentRepository.save(ReplenishPercent);
        }
        ReplenishPercent add = new ReplenishPercent();
        add.setPercent(percent);
        add.setCreateDate(new Date());
        add.setAvailable(true);
        add.setAdmin(admin);
        replenishPercentRepository.save(add);
        data.put("percent", add.getPercent());
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    public ReplenishPercent findEnable() {
        return replenishPercentRepository.findEnable();
    }
}
