package com.school.teachermanage.service;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.entity.Admin;
import com.school.teachermanage.entity.ConvertLevel;
import com.school.teachermanage.enumeration.ConvertLevelEnum;
import com.school.teachermanage.enumeration.ConvertMultipleEnum;
import com.school.teachermanage.enumeration.ErrorCode;
import com.school.teachermanage.repository.AdminRepository;
import com.school.teachermanage.repository.ConvertLevelRepository;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 充值积分规则服务
 *
 * @author zhangsl
 * @date 2017-11-01
 */
@Service
public class ConvertLevelService {
    @Resource
    private ConvertLevelRepository convertLevelRepository;
    @Resource
    private AdminRepository adminRepository;

    public DataResult save(ConvertLevel save, Long adminId, DataResult result) {
        Admin admin = adminRepository.findOne(adminId);
        if (admin == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.TOKEN_ERROR);
            return result;
        }
        if (this.checktMultiple(save.getFirstMultiple(), result)) {
            return result;
        }
        if (this.checktMultiple(save.getSecondMultiple(), result)) {
            return result;
        }
        if (!this.isValidateLevel(save.getFirstLevel(), result)) {
            return result;
        }
        JSONObject data = result.getData();
        ConvertLevel convertLevel = convertLevelRepository.findEnable();
        if (convertLevel != null) {
            convertLevel.setAvailable(false);
            convertLevelRepository.save(convertLevel);
        }
        save.setAvailable(true);
        save.setAdmin(admin);
        convertLevelRepository.save(save);
        data.put("rule", save.getRule());
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    public DataResult findEnable() {
        DataResult result = new DataResult();
        JSONObject data = result.getData();
        ConvertLevel convertLevel = convertLevelRepository.findEnable();
        data.put("multiple", ConvertMultipleEnum.getJsonArray());
        data.put("level", ConvertLevelEnum.getJsonArray());
        if (convertLevel == null) {
            data.put("rule", ErrorCode.CONVERT_LEVEL_NULL.getName());
        } else {
            data.put("rule", convertLevel.getRule());
            data.put("firstLevel", convertLevel.getFirstLevel());
            data.put("firstMultiple", convertLevel.getFirstMultiple());
            data.put("secondMultiple", convertLevel.getSecondMultiple());
        }
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        return result;
    }

    public ConvertLevel getEnable() {
        return convertLevelRepository.findEnable();
    }

    private boolean checktMultiple(byte multiple, DataResult result) {
        if (!ConvertMultipleEnum.isValidate(multiple)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.CONVERT_LEVEL_MULTIPLE);
            return true;
        }
        return false;
    }

    private boolean isValidateLevel(int level, DataResult result) {
        if (!ConvertLevelEnum.isValidate(level)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.CONVERT_LEVEL_MULTIPLE);
            return false;
        }
        return true;
    }
}
