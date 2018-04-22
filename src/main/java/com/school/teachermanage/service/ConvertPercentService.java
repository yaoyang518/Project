package com.school.teachermanage.service;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.entity.Admin;
import com.school.teachermanage.entity.ConvertPercent;
import com.school.teachermanage.enumeration.ConvertPercentEnum;
import com.school.teachermanage.enumeration.ErrorCode;
import com.school.teachermanage.repository.AdminRepository;
import com.school.teachermanage.repository.ConvertPercentRepository;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 转换比率 service
 *
 * @author wudc
 * @date 2017-11-06
 */
@Service
public class ConvertPercentService {

    @Resource
    private ConvertPercentRepository convertPercentRepository;
    @Resource
    private AdminRepository adminRepository;

    /**
     * 添加 转化率
     */
    public DataResult save(int percent, Long adminId, DataResult result) {
        //进行数据判断
        if (!ConvertPercentEnum.getConvertPercentEnumByIndex(percent)) {
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
        ConvertPercent convertPercent = convertPercentRepository.findEnable();
        if (convertPercent != null) {
            convertPercent.setAvailable(false);
            convertPercentRepository.save(convertPercent);
        }
        ConvertPercent add = new ConvertPercent();
        add.setPercent(percent);
        add.setAvailable(true);
        add.setAdmin(admin);
        convertPercentRepository.save(add);
        data.put("percent", add.getPercent());
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    /**
     * 获取可用的比率
     *
     * @return
     */
    public ConvertPercent findEnable() {
        return convertPercentRepository.findEnable();
    }
}
