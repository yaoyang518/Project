package com.school.teachermanage.controller;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.constants.NumConstants;
import com.school.teachermanage.entity.Account;
import com.school.teachermanage.entity.LevelUp;
import com.school.teachermanage.entity.User;
import com.school.teachermanage.enumeration.BitcoinSource;
import com.school.teachermanage.enumeration.ErrorCode;
import com.school.teachermanage.enumeration.UserLevel;
import com.school.teachermanage.util.DateUtil;
import com.school.teachermanage.util.NumberUtil;
import com.school.teachermanage.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户管理控制类
 *
 * @author zhangsl
 * @date 2017-11-16
 */
@RestController
@RequestMapping("/userApi")
@Api(description = "用户接口")
public class UserController {

    @Resource
    private AccountService accountService;
    @Autowired
    private UserService userService;
    @Resource
    private BankAccountService bankAccountService;
    @Resource
    private PayoutRecordService payoutRecordService;
    @Resource
    private LevelUpService levelUpService;
    @Resource
    private BitcoinRecordService bitcoinRecordService;
    @Resource
    private AliPayAccountService aliPayAccountService;


    @GetMapping("/info")
    @ApiOperation(value = "用户信息", notes = "返回码: {'0000'：'操作成功'}")
    @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String")
    public DataResult getUser(@RequestHeader String token, HttpServletRequest request) {
        Long userId = userService.getUserId(token);
        User user = userService.findById(userId);
        Account account = accountService.findByUser(user);
        int bankAccountSize = bankAccountService.getBankAccountSizeByUserId(userId);
        int aliPayAccounttSize = aliPayAccountService.getAliPayAccounttSizeByUserId(userId);
        DataResult result = new DataResult();
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        JSONObject data = result.getData();
        data.put("id", user.getId());
        data.put("bankAcountTotal", bankAccountSize);
        data.put("aliPayAcountTotal", aliPayAccounttSize);
        data.put("username", user.getUsername());
        data.put("shopKeeper", user.getShopKeeper());
        data.put("upgrade", user.getUserLevel().getIndex() < UserLevel.LEVEL_SIX.getIndex());
        data.put("mobile", user.getMobile());
        data.put("totalScore", account.getTotalScore().setScale(NumConstants.TWO, BigDecimal.ROUND_HALF_DOWN));
        data.put("frozenScore", account.getFrozenScore().setScale(NumConstants.TWO, BigDecimal.ROUND_HALF_DOWN));
        data.put("balance", account.getBalance().setScale(NumConstants.TWO, BigDecimal.ROUND_HALF_DOWN));
        data.put("bitcoin", account.getBitcoin().setScale(NumConstants.TWO, BigDecimal.ROUND_HALF_DOWN));
        data.put("ticket", account.getTicket().setScale(NumConstants.TWO, BigDecimal.ROUND_HALF_DOWN));
        data.put("score", account.getScore().setScale(NumConstants.TWO, BigDecimal.ROUND_HALF_DOWN));
        data.put("UserLevelName", user.getUserLevel().getName());
        if (user.getParent() != null) {
            JSONObject parent = new JSONObject();
            parent.put("mobile", user.getParent().getMobile());
            parent.put("id", user.getParent().getId());
            data.put("parent", parent);
        }
        return result;
    }

    /**
     * 用户申请提现
     */
    @PostMapping("/payoutRecord/apply")
    @ApiOperation(value = "用户申请提现", notes = "返回码: {'0000'：'操作成功', '0012': '用户不存在','0046':'银行卡有误'," +
            "'0045':'提现金额有误','0047':'用户余额不足'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "amount", value = "提现数量", required = true, paramType = "query", dataType = "BigDecimal"),
            @ApiImplicitParam(name = "type", value = "提现账户类型", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "id", value = "卡号id | 支付宝id", required = true, paramType = "query", dataType = "Long")
    })
    public DataResult payoutApply(@RequestHeader String token,
                                  @RequestParam BigDecimal amount,
                                  @RequestParam Long id,
                                  @RequestParam String type) {
        DataResult result = new DataResult();
        //判断周末
        if (!DateUtil.isWeedend(new Date())) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.IS_WEEKEND);
            return result;
        }
        Long userId = userService.getUserId(token);
        return payoutRecordService.appay(userId, id, amount,type, result);
    }

    /**
     * 升级店主信息
     */
    @GetMapping("/shopkeeperInfo")
    @ApiOperation(value = "升级店主信息", notes = "返回码: {'0000'：'操作成功'")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String")
    })
    public DataResult shopkeeperInfo(@RequestHeader String token) {
        DataResult result = new DataResult();
        Long userId = userService.getUserId(token);
        User user = userService.findById(userId);
        Account account = accountService.findByUser(user);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        LevelUp levelUp = levelUpService.getEnableByUserLevel(UserLevel.LEVEL_ONE);
        result.getData().put("id", userId);
        result.getData().put("username", user.getUsername());
        result.getData().put("price", levelUp.getAmount());
        result.getData().put("bitcoin", account.getBitcoin());
        return result;
    }

    /**
     * 升级店主信息
     */
    @PostMapping("/shopkeeper")
    @ApiOperation(value = "升级店主", notes = "返回码: {'0000'：'操作成功','0035':'余额不足'")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String")
    })
    public DataResult shopkeeper(@RequestHeader String token) {
        DataResult result = new DataResult();
        Long userId = userService.getUserId(token);
        User user = userService.findById(userId);
        Account account = accountService.findByUser(user);
        LevelUp levelUp = levelUpService.getEnableByUserLevel(UserLevel.LEVEL_ONE);
        BigDecimal amount = levelUp.getAmount();
        BigDecimal bitcoin = account.getBitcoin();
        if (NumberUtil.isLess(bitcoin, amount)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.AMOUNT_ERROR);
            return result;
        }
        userService.upShopKeeper(userId, null, result);
        account.setBitcoin(bitcoin.subtract(amount));
        bitcoinRecordService.generateBitcoinRecord(null, true, BitcoinSource.CONSUME, user, null, levelUp.getAmount(), account.getBitcoin(), "用户升级店主");
        accountService.save(account);
        return result;
    }

    /**
     * 升级消费商信息
     */
    @GetMapping("/consumerBusinessInfo")
    @ApiOperation(value = "升级消费商信息", notes = "返回码: {'0000'：'操作成功'")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String")
    })
    public DataResult consumerBusinessInfo(@RequestHeader String token) {
        DataResult result = new DataResult();
        Long userId = userService.getUserId(token);
        User user = userService.findById(userId);
        Account account = accountService.findByUser(user);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        LevelUp levelUp = levelUpService.getEnableByUserLevel(UserLevel.LEVEL_SIX);
        result.getData().put("id", userId);
        result.getData().put("username", user.getUsername());
        result.getData().put("price", levelUp.getAmount());
        result.getData().put("bitcoin", account.getBitcoin());
        return result;
    }

    /**
     * 升级消费商
     */
    @PostMapping("/consumerBusiness")
    @ApiOperation(value = "升级消费商", notes = "返回码: {'0000'：'操作成功','0035':'余额不足'")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "amount", value = "升级金额", required = true, paramType = "query", dataType = "BigDecimal")
    })
    public DataResult consumerBusiness(@RequestHeader String token, @RequestParam BigDecimal amount) {
        DataResult result = new DataResult();
        Long userId = userService.getUserId(token);
        User user = userService.findById(userId);
        Account account = accountService.findByUser(user);
        LevelUp levelUp = levelUpService.getEnableByUserLevel(UserLevel.LEVEL_SIX);
        BigDecimal levelUpAmount = levelUp.getAmount();
        BigDecimal bitcoin = account.getBitcoin();
        if (NumberUtil.isLess(amount, levelUpAmount) || NumberUtil.isLess(bitcoin, amount)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.AMOUNT_ERROR);
            return result;
        }
        userService.upConsumerBusiness(userId, null, amount, result);
        account.setBitcoin(bitcoin.subtract(amount));
        bitcoinRecordService.generateBitcoinRecord(null, true, BitcoinSource.CONSUME, user, null, amount, account.getBitcoin(), "用户升级消费商");
        accountService.save(account);
        return result;
    }


    /**
     * 赠送报单积分
     */
    @PostMapping("/bitcoinRecord/give")
    @ApiOperation(value = "赠送报单积分", notes = "返回码: {'0000'：'操作成功', '0012': '用户不存在'," +
            "'0058', '充值额度错误','0047':'用户余额不足','0059': '不可赠送给自己'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "amount", value = "提现数量", required = true, paramType = "query", dataType = "BigDecimal"),
            @ApiImplicitParam(name = "giveId", value = "转增人", required = true, paramType = "query", dataType = "Long")
    })
    public DataResult giveBitcoinRecord(@RequestHeader String token,
                                        @RequestParam BigDecimal amount,
                                        @RequestParam Long giveId) {
        DataResult result = new DataResult();
        Long userId = userService.getUserId(token);
        User user = userService.findById(userId);
        if (userId.equals(giveId)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.NOT_ALLOW_GIVE_SELF);
            return result;
        }
        if (user == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
            return result;
        }

        User giveUser = userService.findById(giveId);
        if (giveUser == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
            return result;
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.BITCOIN_LIMIT);
            return result;
        }
        Account userAccount = accountService.findByUser(user);
        Account geiveAccount = accountService.findByUser(giveUser);

        if (userAccount.getBitcoin().compareTo(amount) < 0) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.BALANCE_LESS_ERROR);
            return result;
        }
        accountService.donateBitcoin(amount, userAccount, geiveAccount);
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

}
