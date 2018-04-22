package com.school.teachermanage.service;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.constants.NumConstants;
import com.school.teachermanage.entity.*;
import com.school.teachermanage.enumeration.BalanceSource;
import com.school.teachermanage.enumeration.DateStyle;
import com.school.teachermanage.enumeration.UserLevel;
import com.school.teachermanage.repository.AccountReposity;
import com.school.teachermanage.repository.AdminRepository;
import com.school.teachermanage.repository.ReplenishRecordRepository;
import com.school.teachermanage.util.CommonUtil;
import com.school.teachermanage.util.DateUtil;
import com.school.teachermanage.util.NumberUtil;
import com.school.teachermanage.util.QueryUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 补货服务类
 *
 * @author zhangsl
 * @date 2017/11/28
 */
@Service
public class ReplenishService {

    private Logger logger = LoggerFactory.getLogger(ReplenishService.class);

    @Resource
    private ReplenishRecordRepository replenishRecordRepository;
    @Resource
    private AdminRepository adminRepository;
    @Resource
    private UserService userService;
    @Resource
    private AccountReposity accountReposity;
    @Resource
    private BalanceRecordService balanceRecordService;
    @Resource
    private ReplenishPercentService replenishPercentService;

    public void replenish(BigDecimal amount, Account account, Long adminId) {
        Admin admin = adminRepository.findOne(adminId);
        User user = userService.findById(account.getUser().getId());
        //补货记录
        ReplenishRecord record = new ReplenishRecord();
        record.setAdminId(adminId);
        record.setAmount(amount);
        record.setCreateDate(new Date());
        record.setUser(user);
        replenishRecordRepository.save(record);

        //存在上级
        if (null != user.getParent()) {
            //上级收益
            logger.info("开始计算上级收益：" + System.currentTimeMillis());
            int level = 1;
            ReplenishPercent replenishPercent = replenishPercentService.findEnable();

            BigDecimal percent = new BigDecimal(replenishPercent.getPercent()).divide(NumConstants.HUNDRED);
            BigDecimal limit = NumConstants.SCORE_MIN;
            BigDecimal benifit = amount.multiply(percent).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN);
            User parent = user.getParent();
            User sourceUser = user;
            while (NumberUtil.isGreaterOrEqual(benifit, limit)) {
                //过滤消费商以下级别
                if (parent.getUserLevel().getIndex() < UserLevel.LEVEL_SIX.getIndex()) {
                    sourceUser = parent;
                    parent = parent.getParent();
                    if (null == parent) {
                        logger.info("已无上级，结束");
                        break;
                    } else {
                        continue;
                    }
                }
                benifit = amount.multiply(percent).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN);
                if (NumberUtil.isLessOrEqual(benifit, limit)) {
                    //收益低于最小限制，结束
                    break;
                }
                int count = userService.findJuniorCountNotNormal(parent.getId()) + 1;
                if (count > NumConstants.NO_LIMIT_NUMBER || level <= count) {
                    //无限级获取 || 在指定级别可以收益
                    Account parentAccount = accountReposity.findByUser(parent);
                    BigDecimal balance = parentAccount.getBalance().add(benifit).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN);
                    parentAccount.setBalance(balance);
                    accountReposity.save(account);
                    balanceRecordService.generateBalanceRecord(admin, false, BalanceSource.CHILDREN_RECHARGE,
                            parent, user, sourceUser, level, benifit,
                            parentAccount.getBalance(), "下线补货",null);
                }
                //其它情况 沉淀公司收益 不生成记录
                logger.info("级数：" + level + ";收益：" + benifit + ";百分比：" + percent);
                //上级，比例转换
                if (null == parent.getParent()) {
                    logger.info("已无上级，结束");
                    break;
                }
                sourceUser = parent;
                parent = parent.getParent();
                percent = percent.multiply(NumConstants.ZERO_POINT_FIVE);
                level++;
            }
            logger.info("计算上级收益结束：" + System.currentTimeMillis());

        } else {
            logger.info("没有上级");
        }
    }

    public DataResult list(String mobile, Date start, Date end, int page, int size, DataResult result) {
        if (mobile != null) {
            mobile = "%" + mobile + "%";
        } else {
            mobile = "%" + "" + "%";
        }
        PageRequest pageRequest = QueryUtil.buildPageRequest(page, size, "id", true);
        Page<ReplenishRecord> rechargeRecorduserPage = replenishRecordRepository.findByMobileAndStatusWithDate(mobile, start, end, pageRequest);
        JSONObject data = result.getData();
        data.put("page", CommonUtil.generatePageJSON(rechargeRecorduserPage));
        List<ReplenishRecord> replenishRecords = rechargeRecorduserPage.getContent();
        JSONArray array = this.generateTicketRecordJsonArray(replenishRecords);
        data.put("replenishRecords", array);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        return result;
    }

    private JSONArray generateTicketRecordJsonArray(List<ReplenishRecord> replenishRecords) {
        JSONArray array = new JSONArray();
        for (ReplenishRecord replenishRecord : replenishRecords) {
            JSONObject json = new JSONObject();
            json.put("id", replenishRecord.getId());
            Long adminId = replenishRecord.getAdminId();
            if (adminId != null) {
                Admin admin = adminRepository.findOne(adminId);
                if (admin != null) {
                    json.put("adminId", admin.getId());
                    json.put("adminName", admin.getName());
                }
            }
            json.put("amount", replenishRecord.getAmount().setScale(NumConstants.TWO, BigDecimal.ROUND_FLOOR));
            json.put("createDate", DateUtil.dateToString(replenishRecord.getCreateDate(), DateStyle.YYYY_MM_DD_HH_MM_SS));
            json.put("username", replenishRecord.getUser().getUsername());
            json.put("userId", replenishRecord.getUser().getId());
            array.add(json);
        }
        return array;
    }
}
