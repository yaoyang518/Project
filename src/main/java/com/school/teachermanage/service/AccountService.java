package com.school.teachermanage.service;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.constants.NumConstants;
import com.school.teachermanage.entity.*;
import com.school.teachermanage.enumeration.*;
import com.school.teachermanage.repository.*;
import com.school.teachermanage.util.CommonUtil;
import com.school.teachermanage.util.DateUtil;
import com.school.teachermanage.util.NumberUtil;
import com.school.teachermanage.util.QueryUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 账户积分服务方法
 *
 * @author zhangsl
 * @date 2017-11-06
 */
@Service
public class AccountService {

    private Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountReposity accountReposity;
    @Autowired
    private ScoreRecordService scoreRecordService;
    @Autowired
    private UserService userService;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private ConvertLevelRepository convertLevelRepository;
    @Autowired
    private RechargeRecordRepository rechargeRecordRepository;
    @Autowired
    private BalanceRecordRepository balanceRecordRepository;
    @Autowired
    private AccountService accountService;
    @Resource
    private BalanceRecordService balanceRecordService;
    @Resource
    private BitcoinRecordService bitcoinRecordService;
    @Resource
    private TicketRecordService ticketRecordService;

    public Account save(Account account){
        return accountReposity.save(account);
    }


    public DataResult findBalanceRecordByUserId(Long userId, int page, int size, DataResult result) {
        User user = userService.findById(userId);
        if (user == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
            return result;
        }
        PageRequest pageRequest = QueryUtil.buildPageRequest(page, size, "id", true);
        Page<BalanceRecord> userPage = balanceRecordRepository.findBalanceRecordByUserId(userId, pageRequest);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        JSONObject data = result.getData();
        List<BalanceRecord> balanceRecords = userPage.getContent();
        JSONArray array = new JSONArray();
        if (balanceRecords != null && balanceRecords.size() > 0) {
            for (BalanceRecord balanceRecord : balanceRecords) {
                JSONObject json = new JSONObject();
                json.put("balanceSource", balanceRecord.getBalanceSource().getName());
                json.put("minus", balanceRecord.isMinus());
                json.put("amount", balanceRecord.getAmount());
                json.put("balance", balanceRecord.getBalance());
                json.put("createDate", DateUtil.dateToString(balanceRecord.getCreateDate(), DateStyle.YYYY_MM_DD_HH_MM_SS));
                array.add(json);
            }
        }
        data.put("page", CommonUtil.generatePageJSON(userPage));
        result.getData().put("balanceRecords", array);
        return result;
    }

    /**
     * 获取总积分
     */
    public BigDecimal getTotalScore() {
        return accountReposity.getSumFrozenScore().add(accountReposity.getSumScore());
    }

    public Account regScore(User user) {
        BigDecimal amount = NumConstants.REG_SCORE;
        Account account = findByUser(user);
        account.setScore(BigDecimal.ZERO);
        account.setFrozenScore(account.getFrozenScore().add(amount));
        accountReposity.save(account);
        //赠送积分
        ScoreRecord record = new ScoreRecord();
        record.setScoreSource(ScoreSource.REG);
        record.setMinus(false);
        record.setAmount(amount);
        record.setCreateDate(new Date());
        record.setUser(user);
        record.setRemark("注册赠送");
        record.setFrozen(account.getFrozenScore());
        record.setScore(account.getScore());
        record.setTotal(account.getTotalScore());
        record.setFrozened(true);
        scoreRecordService.save(record);
        return accountReposity.save(account);
    }

    public Account findByUser(User user) {
        Account account = accountReposity.findByUser(user);
        if (account == null) {
            account = new Account();
            account.setCreateDate(user.getCreateDate());
            account.setFrozenScore(BigDecimal.ZERO);
            account.setScore(BigDecimal.ZERO);
            account.setUser(user);
            account.setBalance(BigDecimal.ZERO);
            account.setBitcoin(BigDecimal.ZERO);
            account.setTicket(BigDecimal.ZERO);
            accountReposity.save(account);
        }
        return account;
    }

    public List<Account> findCanConvert(BigDecimal minScore) {
        return accountReposity.findCanConvert(minScore);
    }

    public void convert(Account account, BigDecimal percent) {
        BigDecimal frozenScore = account.getFrozenScore().setScale(NumConstants.SEVEN, BigDecimal.ROUND_HALF_DOWN);
        BigDecimal scoreSrc = account.getScore().setScale(NumConstants.SEVEN, BigDecimal.ROUND_HALF_DOWN);
        BigDecimal amount = frozenScore.multiply(percent).setScale(NumConstants.SEVEN, BigDecimal.ROUND_HALF_DOWN);

        frozenScore = frozenScore.subtract(amount).setScale(NumConstants.SEVEN, BigDecimal.ROUND_HALF_DOWN);
        BigDecimal score = scoreSrc.add(amount).setScale(NumConstants.SEVEN, BigDecimal.ROUND_HALF_DOWN);
        account.setFrozenScore(frozenScore);
        account.setScore(score);
        accountReposity.save(account);
        //减少冻结积分
        ScoreRecord frozenRecord = new ScoreRecord();
        frozenRecord.setAmount(amount);
        frozenRecord.setCreateDate(new Date());
        frozenRecord.setMinus(true);
        frozenRecord.setScoreSource(ScoreSource.CONVERT);
        frozenRecord.setRemark("每日积分转化");
        frozenRecord.setUser(userService.findById(account.getUser().getId()));
        frozenRecord.setFrozen(account.getFrozenScore());
        frozenRecord.setScore(scoreSrc);
        frozenRecord.setTotal(account.getFrozenScore().add(scoreSrc));
        frozenRecord.setFrozened(true);
        scoreRecordService.save(frozenRecord);
        //增加兑换积分
        ScoreRecord scoreRecord = new ScoreRecord();
        scoreRecord.setAmount(amount);
        scoreRecord.setCreateDate(new DateTime().plusSeconds(2).toDate());
        scoreRecord.setMinus(false);
        scoreRecord.setScoreSource(ScoreSource.CONVERT);
        scoreRecord.setRemark("每日积分转化");
        scoreRecord.setUser(userService.findById(account.getUser().getId()));
        scoreRecord.setFrozen(account.getFrozenScore());
        scoreRecord.setScore(account.getScore());
        scoreRecord.setTotal(account.getTotalScore());
        scoreRecord.setFrozened(false);
        scoreRecordService.save(scoreRecord);

    }

    public void recharge(BigDecimal amount, Account account, Long adminId, boolean recharge, boolean range, boolean special) {
        Admin admin = null;
        if (adminId != null) {
            admin = adminRepository.findOne(adminId);
        }
        //amount 取千位整数
        amount = amount.divide(NumConstants.THOUSAND).setScale(NumConstants.ZERO, BigDecimal.ROUND_FLOOR);
        amount = amount.multiply(NumConstants.THOUSAND).setScale(NumConstants.ZERO, BigDecimal.ROUND_FLOOR);
        User user = userService.findById(account.getUser().getId());
        //给自己充值
        BigDecimal frozenScore = account.getFrozenScore().setScale(NumConstants.SEVEN, BigDecimal.ROUND_HALF_DOWN);
        BigDecimal multiply = BigDecimal.ONE;
        if (recharge || special) {
            ConvertLevel convertLevel = convertLevelRepository.findEnable();
            multiply = convertLevel.getMultiple(amount);
        }
        BigDecimal rechargeScore = amount.multiply(multiply).setScale(NumConstants.SEVEN, BigDecimal.ROUND_HALF_DOWN);
        frozenScore = frozenScore.add(rechargeScore).setScale(NumConstants.SEVEN, BigDecimal.ROUND_HALF_DOWN);
        account.setFrozenScore(frozenScore);
        accountReposity.save(account);
        //积分记录
        ScoreRecord record = new ScoreRecord();
        record.setAmount(rechargeScore);
        record.setCreateDate(new Date());
        record.setMinus(false);
        if (recharge) {
            record.setScoreSource(ScoreSource.RECHARGE);
            record.setRemark("积分充值");
        } else {
            record.setScoreSource(ScoreSource.LEVEL_UP);
            record.setRemark("用户升级");
        }
        record.setUser(user);
        record.setFrozen(account.getFrozenScore());
        record.setScore(account.getScore());
        record.setTotal(account.getTotalScore());
        record.setFrozened(true);
        scoreRecordService.save(record);
        if (recharge) {
            //充值记录
            RechargeRecord rechargeRecord = new RechargeRecord();
            rechargeRecord.setAdminId(adminId);
            rechargeRecord.setAmount(amount);
            rechargeRecord.setCreateDate(new Date());
            rechargeRecord.setUser(user);
            rechargeRecordRepository.save(rechargeRecord);
        }

        //存在上级
        if (null != user.getParent()) {
            //上级收益
            logger.info("开始计算上级收益：" + System.currentTimeMillis());
            int level = 1;
            BigDecimal percent = NumConstants.ZERO_POINT_ONE_TWO;
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
                    String remark = "分享用户升级";
                    BalanceSource source = BalanceSource.CHILDREN_LEVEL_UP;
                    if (recharge) {
                        source = BalanceSource.CHILDREN_RECHARGE;
                        remark = "下线充值";
                    }
                    balanceRecordService.generateBalanceRecord(admin, false, source,
                            parent, user, sourceUser, level, benifit,
                            parentAccount.getBalance(), remark,null);
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

            if (range) {
                //上级级差收益
                logger.info("开始计算上级级差收益：" + System.currentTimeMillis());
                parent = user.getParent();
                level = 1;
                while (true) {
                    int levelIndex = parent.getUserLevel().getIndex();
                    if (level == 1) {
                        if (levelIndex == UserLevel.LEVEL_TWENTY_ONE.getIndex()) {
                            //9%
                            BigDecimal unitPercent = new BigDecimal(0.09);
                            benifit = amount.multiply(unitPercent).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN);
                            generateRangeBalanceRecord(admin, user, level, benifit, parent);
                            break;
                        } else if (levelIndex == UserLevel.LEVEL_SIXTEEN.getIndex()) {
                            //6%
                            BigDecimal unitPercent = new BigDecimal(0.06);
                            benifit = amount.multiply(unitPercent).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN);
                            generateRangeBalanceRecord(admin, user, level, benifit, parent);
                            level = 3;
                        } else if (levelIndex == UserLevel.LEVEL_ELEVEN.getIndex()) {
                            //3%
                            BigDecimal unitPercent = new BigDecimal(0.03);
                            benifit = amount.multiply(unitPercent).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN);
                            generateRangeBalanceRecord(admin, user, level, benifit, parent);
                            level++;
                        }
                    } else if (level == 2) {
                        if (levelIndex == UserLevel.LEVEL_TWENTY_ONE.getIndex()) {
                            //6%
                            BigDecimal unitPercent = new BigDecimal(0.06);
                            benifit = amount.multiply(unitPercent).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN);
                            generateRangeBalanceRecord(admin, user, level, benifit, parent);
                            break;
                        } else if (levelIndex == UserLevel.LEVEL_SIXTEEN.getIndex()) {
                            //3%
                            BigDecimal unitPercent = new BigDecimal(0.03);
                            benifit = amount.multiply(unitPercent).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN);
                            generateRangeBalanceRecord(admin, user, level, benifit, parent);
                            level++;
                        }
                    } else if (level == 3) {
                        if (levelIndex == UserLevel.LEVEL_TWENTY_ONE.getIndex()) {
                            //3%
                            BigDecimal unitPercent = new BigDecimal(0.03);
                            benifit = amount.multiply(unitPercent).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN);
                            generateRangeBalanceRecord(admin, user, level, benifit, parent);
                            break;
                        }
                    }
                    //上级，比例转换
                    parent = parent.getParent();
                    if (null == parent) {
                        logger.info("已无上级，级差结束");
                        break;
                    }
                }
                logger.info("计算上级级差收益结束：" + System.currentTimeMillis());
            }

        } else {
            logger.info("没有上级");
        }
    }

    private void generateRangeBalanceRecord(Admin admin, User consumer, int level, BigDecimal benifit, User user) {
        Account account = accountReposity.findByUser(user);
        BigDecimal balance = account.getBalance().setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN);
        balance = balance.add(benifit).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN);
        account.setBalance(balance);
        accountReposity.save(account);
        //生成记录
        balanceRecordService.generateBalanceRecord(admin, false, BalanceSource.CHILDREN_RANGE_RECHARGE,
                user, consumer, consumer, level, benifit,
                account.getBalance(), "下线充值,级差收益",null);

    }

    /**
     * 系统修改积分
     */
    public DataResult updateAccountByUserId(Long userId, BigDecimal amount, boolean minus, Long adminId, boolean frozen, DataResult result) {
        User user = userService.findById(userId);
        if (user == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
            return result;
        }
        if (NumberUtil.isLessOrEqual(amount, BigDecimal.ZERO)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.UPDATE_ACCOUNT_LIMIT);
            return result;
        }
        Admin admin = adminRepository.findOne(adminId);
        Account account = accountReposity.findByUser(user);
        if (frozen) {
            if (minus) {
                if (NumberUtil.isLess(account.getFrozenScore(), amount)) {
                    result.setMsg(MsgConstants.OPT_FAIL);
                    result.setDataMsg(ErrorCode.SCORE_LESS_ERROR);
                    return result;
                }
                account.setFrozenScore(account.getFrozenScore().subtract(amount));
            } else {
                account.setFrozenScore(account.getFrozenScore().add(amount));
            }
        } else {
            if (minus) {
                if (NumberUtil.isLess(account.getScore(), amount)) {
                    result.setMsg(MsgConstants.OPT_FAIL);
                    result.setDataMsg(ErrorCode.SCORE_LESS_ERROR);
                    return result;
                }
                account.setScore(account.getScore().subtract(amount));
            } else {
                account.setScore(account.getScore().add(amount));
            }
        }
        accountReposity.save(account);
        ScoreRecord scoreRecord = new ScoreRecord();
        scoreRecord.setAmount(amount);
        scoreRecord.setMinus(minus);
        scoreRecord.setAdmin(admin);
        scoreRecord.setUser(user);
        scoreRecord.setScoreSource(ScoreSource.SYSTEM_MODIFY);
        scoreRecord.setCreateDate(new Date());
        scoreRecord.setFrozen(account.getFrozenScore());
        scoreRecord.setScore(account.getScore());
        scoreRecord.setTotal(account.getTotalScore());
        scoreRecord.setRemark("系统调整");
        scoreRecord.setFrozened(frozen);
        scoreRecordService.save(scoreRecord);
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    public void userLevelUpAccount(Admin admin, User user, LevelUp levelUp) {
        User parent = user.getParent();
        User sourceUser = user;
        if (null != parent) {
            logger.info("用户升级店主，开始计算上级店主收益：" + System.currentTimeMillis());
            int level = 1;

            BigDecimal limit = NumConstants.SCORE_MIN;
            BigDecimal benifit = levelUp.getDirectAmount();
            while (NumberUtil.isGreaterOrEqual(benifit, limit)) {
                //判读用户级别
                if (parent.getShopKeeper()) {
                    //判断用户是否符合条件 ，查询用户直接下线店主个数是否符合
                    int count = userService.findIsShopKeeperJuniors(parent.getId()) + NumConstants.ONE;
                    if (count > NumConstants.NO_LIMIT_NUMBER || count >= level) {
                        //说明可以拿到
                        Account parentAccount = accountService.findByUser(parent);
                        parentAccount.setBalance(parentAccount.getBalance().add(benifit));
                        accountReposity.save(parentAccount);
                        //生成记录
                        balanceRecordService.generateBalanceRecord(admin, false, BalanceSource.CHILDREN_SHOPKEEPER,
                                parent, user, sourceUser, level, benifit,
                                parentAccount.getBalance(), "下线升级店主",null);
                    }
                    logger.info("级数：" + level + ";收益：" + benifit + ";百分比：" + NumConstants.ZERO_POINT_FIVE);
                    //计算数据
                    benifit = benifit.multiply(NumConstants.ZERO_POINT_FIVE).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN);
                    if (NumberUtil.isLess(benifit, limit)) {
                        //收益低于最小限制，结束
                        break;
                    }
                    level++;
                }
                sourceUser = parent;
                parent = parent.getParent();
                if (null == parent) {
                    logger.info("已无上级，结束");
                    break;
                }
            }
            logger.info("用户升级，计算上级收益结束：" + System.currentTimeMillis());
        } else {
            logger.info("无上级");
        }
    }

    public void findUserBalanceSumInWeek() {
        List<User> users = balanceRecordRepository.findUsersInPreWeek();
        for (User user : users) {
            //进行积分统计
            BigDecimal amount = balanceRecordRepository.getAmoutByUserIdAndBalanceSource(user.getId(), BalanceSource.CHILDREN_RECHARGE);
            if (amount != null && NumberUtil.isGreater(amount, BigDecimal.ZERO)) {
                if (NumberUtil.isGreater(amount, NumConstants.JUNIOR_MIN)) {
                    //查询用户的直接下级 排除普通会员
                    List<User> juniors = userService.findJuniorsWithoutUserLevel(user.getId(), UserLevel.USER_NORMAL);
                    //计算获取的钱
                    if (juniors != null && !juniors.isEmpty()) {
                        BigDecimal benifit = amount.multiply(NumConstants.ZERO_POINT_TWO_FIVE).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN);
                        benifit = benifit.divide(new BigDecimal(juniors.size())).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN);
                        for (User junior : juniors) {
                            Account account = accountService.findByUser(junior);
                            account.setBalance(account.getBalance().add(benifit));
                            accountReposity.save(account);
                            //生成记录
                            balanceRecordService.generateBalanceRecord(null, false, BalanceSource.PARENT_BALANCE,
                                    junior, user, user, 1, benifit,
                                    account.getBalance(), "获得上级提成",null);
                        }
                    }
                }
            }
        }
    }

    public void rechargeBitcoin(BigDecimal amount, Account account, Long adminId,Boolean type) {
        Admin admin = adminRepository.findOne(adminId);
        User user = userService.findById(account.getUser().getId());
        if(type){
            account.setBitcoin(account.getBitcoin().subtract(amount).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN));
        }else{
            account.setBitcoin(account.getBitcoin().add(amount).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN));
        }
        accountReposity.save(account);
        bitcoinRecordService.generateBitcoinRecord(admin, type, BitcoinSource.RECHARGE, user, null, amount, account.getBitcoin(), "充值报单积分");
    }

    public void donateBitcoin(BigDecimal amount, Account userAccount, Account geiveAccount) {
        User user = userAccount.getUser();
        User geiveUser = geiveAccount.getUser();
        userAccount.setBitcoin(userAccount.getBitcoin().subtract(amount).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN));
        geiveAccount.setBitcoin(geiveAccount.getBitcoin().add(amount).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN));
        accountReposity.save(userAccount);
        accountReposity.save(geiveAccount);
        bitcoinRecordService.generateBitcoinRecord(null, true, BitcoinSource.DONATE, user, geiveUser, amount, userAccount.getBitcoin(), "转赠报单积分");
        bitcoinRecordService.generateBitcoinRecord(null, false, BitcoinSource.DONATE, geiveUser, user, amount, geiveAccount.getBitcoin(), "转赠报单积分");
    }

    public void rechargeBalance(BigDecimal amount, Account account, Long adminId, Boolean minus) {
        Admin admin = adminRepository.findOne(adminId);
        User user = userService.findById(account.getUser().getId());
        if(minus){
            account.setBalance(account.getBalance().subtract(amount).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN));
        }else{
            account.setBalance(account.getBalance().add(amount).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN));
        }
        accountReposity.save(account);
        balanceRecordService.generateBalanceRecord(admin,minus,BalanceSource.SYSTEM_TUNING,user,null,null,0,amount,account.getBalance(),"系统调整余额",null);
    }

    public void rechargeTicket(BigDecimal amount, Account account, Long adminId, Boolean minus) {
        Admin admin = adminRepository.findOne(adminId);
        User user = userService.findById(account.getUser().getId());
        if(minus){
            account.setTicket(account.getTicket().subtract(amount).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN));
        }else{
            account.setTicket(account.getTicket().add(amount).setScale(NumConstants.EIGHT, BigDecimal.ROUND_HALF_DOWN));
        }
        accountReposity.save(account);
        ticketRecordService.generateTicketRecord(admin,minus, TicketSource.SYSTEM_MODIFY,user,amount,account.getTicket(),"系统调整");
    }
}
