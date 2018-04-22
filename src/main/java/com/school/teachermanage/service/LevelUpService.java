package com.school.teachermanage.service;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.constants.NumConstants;
import com.school.teachermanage.entity.Admin;
import com.school.teachermanage.entity.LevelUp;
import com.school.teachermanage.entity.User;
import com.school.teachermanage.enumeration.ErrorCode;
import com.school.teachermanage.enumeration.UserLevel;
import com.school.teachermanage.repository.AdminRepository;
import com.school.teachermanage.repository.LevelUpRepository;
import com.school.teachermanage.util.NumberUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 升级金额配置
 *
 * @author zhangsl
 * @date 2017-11-13
 */

@Service
public class LevelUpService {

    @Resource
    private LevelUpRepository levelUpRepository;
    @Resource
    private AdminRepository adminRepository;
    @Resource
    private UserService userService;

    public LevelUp getEnableByUserLevel(UserLevel userLevel) {
        return levelUpRepository.findByAvailableAndUserLevel(Boolean.TRUE, userLevel);
    }

    public DataResult findEnable(DataResult result) {
        List<LevelUp> levelUps = levelUpRepository.findByAvailable(Boolean.TRUE);
        JSONArray array = new JSONArray();
        for (LevelUp levelUp : levelUps) {
            JSONObject json = new JSONObject();
            json.put("rule", levelUp.toString());
            array.add(json);
        }
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        result.getData().put("levelUps", array);
        result.getData().put("userLevels", UserLevel.getJsonArray());
        return result;
    }

    public DataResult save(LevelUp save, Long adminId, int index, DataResult result) {
        Admin admin = adminRepository.findOne(adminId);
        if (admin == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
            return result;
        }
        BigDecimal amount = save.getAmount();
        BigDecimal directAmount = save.getDirectAmount();
        if (NumberUtil.isLess(amount, BigDecimal.ZERO)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.AMOUNT_ERROR);
            return result;
        }
        if (NumberUtil.isLess(directAmount, BigDecimal.ZERO)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.AMOUNT_ERROR);
            return result;
        }
        UserLevel userLevel = UserLevel.getEnumByIndex(index);
        if (userLevel == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USERLEVEL_IS_ERROR);
            return result;
        } else if (userLevel.getIndex() > UserLevel.LEVEL_SIX.getIndex()) {
            save.setDirectAmount(BigDecimal.ZERO);
        }
        LevelUp levelUp = getEnableByUserLevel(userLevel);
        if (levelUp != null) {
            levelUp.setAvailable(Boolean.FALSE);
            levelUpRepository.save(levelUp);
        }
        save.setUserLevel(userLevel);
        save.setAdmin(admin);
        save.setAvailable(Boolean.TRUE);
        save.setCreateDate(new Date());
        levelUpRepository.save(save);
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    private boolean isValidateUserLevel(int level, DataResult result) {
        if (!UserLevel.isValidate(level)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USERLEVEL_IS_ERROR);
            return false;
        }
        return true;
    }

    public DataResult findHigherLevel(DataResult result, int index, Long userId) {
        User user = userService.findById(userId);
        LevelUp levelUp = getEnableByUserLevel(UserLevel.LEVEL_SIX);
        JSONArray array = UserLevel.getJsonArray(index, user.getShopKeeper());
        result.getData().put("userLevels", array);
        if (levelUp != null) {
            result.getData().put("amount", levelUp.getAmount());
        }else{
            result.getData().put("amount", NumConstants.TEN_THOUSAND);
        }
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        return result;
    }

    public List<LevelUp> findByAvailable(boolean flag) {
        return levelUpRepository.findByAvailable(flag);
    }
}
