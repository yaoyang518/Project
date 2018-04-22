package com.school.teachermanage.task;

import com.school.teachermanage.constants.NumConstants;
import com.school.teachermanage.entity.Account;
import com.school.teachermanage.entity.ConvertPercent;
import com.school.teachermanage.entity.User;
import com.school.teachermanage.service.AccountService;
import com.school.teachermanage.service.ConvertPercentService;
import com.school.teachermanage.service.UserService;
import com.school.teachermanage.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 定时任务
 *
 * @author zhangsl
 * @date 2017-11-06
 */
@Component
public class ScheduleTask {

    private Logger logger = LoggerFactory.getLogger(ScheduleTask.class);

    @Autowired
    private AccountService accountService;
    @Autowired
    private UserService userService;
    @Autowired
    private ConvertPercentService convertPercentService;


    /**
     * 每日凌晨转化积分
     */
    @Scheduled(cron = "0 0 0 * * ? ")
    public void convertScore() {
        boolean weekend = DateUtil.isWeedend(new Date());
        if (!weekend) {
            logger.info("工作日每日积分转化开始" + System.currentTimeMillis());
            ConvertPercent convertPercent = convertPercentService.findEnable();
            if (convertPercent == null) {
                logger.info("转化率未配置");
                return;
            }
            BigDecimal percent = new BigDecimal(convertPercent.getPercent()).divide(NumConstants.THOUSAND).setScale(NumConstants.SEVEN, BigDecimal.ROUND_HALF_DOWN);
            List<Account> accounts = accountService.findCanConvert(NumConstants.CONVERT_MIN_SCORE);
            for (Account account : accounts) {
                accountService.convert(account, percent);
            }
            logger.info("工作日每日积分转化完毕" + System.currentTimeMillis());
        }
    }

    /**
     * 统计用户本周积分情况完毕
     */
    @Scheduled(cron = "0 0 0 ? * MON  ")
    public void countScore() {
        logger.info("统计用户本周积分情况开始" + System.currentTimeMillis());
        accountService.findUserBalanceSumInWeek();
        logger.info("统计用户本周积分情况完毕" + System.currentTimeMillis());
    }

    /**
     * 检查资格
     */
    @Scheduled(cron = "0 0 0/3 * * ? ")
    public void checkUserDetailLevel() {
        Iterator<User> users = userService.findAll().iterator();
        while (users.hasNext()){
            userService.checkUserDetailLevel(users.next());
        }
    }
}
