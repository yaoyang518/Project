package com.school.teachermanage.controller;

import com.school.teachermanage.annotations.Access;
import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.constants.NumConstants;
import com.school.teachermanage.entity.*;
import com.school.teachermanage.enumeration.*;
import com.school.teachermanage.service.*;
import com.school.teachermanage.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 后台控制器
 *
 * @author zhangsl
 * @date 2017-11-05
 */
@RestController
@RequestMapping(value = "/back")
@Api(description = "后台接口")
public class BackController {

    @Resource
    private AdminService adminService;
    @Resource
    private AccountService accountService;
    @Autowired
    private UserService userService;
    @Resource
    private RoleService roleService;
    @Resource
    private ConvertLevelService convertLevelService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private ConvertPercentService convertPercentService;
    @Resource
    private ScoreRecordService scoreRecordService;
    @Resource
    private RechargeRecordService rechargeRecordService;
    @Resource
    private LevelUpService levelUpService;
    @Resource
    private PayoutRecordService payoutRecordService;
    @Resource
    private BankAccountService bankAccountService;
    @Resource
    private ReplenishPercentService replenishPercentService;
    @Resource
    private ReplenishService replenishService;
    @Resource
    private TicketPercentService ticketPercentService;
    @Resource
    private BitcoinRecordService bitcoinRecordService;
    @Resource
    private PayoutConfigService payoutConfigService;
    @Resource
    private TicketRecordService ticketRecordService;

    /**
     * 后台首页
     *
     * @return
     */
    @GetMapping("/admin")
    @ApiOperation(value = "当前管理员信息", notes = "返回码: {'0000', '操作成功','0012':'用户不存在'}")
    @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String")
    public DataResult index(@RequestHeader String token) {
        DataResult result = new DataResult();
        Long adminId = adminService.getAdminId(token);
        adminService.getAdminInfo(adminId, result);
        ConvertPercent convertPercent = convertPercentService.findEnable();
        if (convertPercent == null) {
            result.getData().put("percent", ErrorCode.CONVERT_PERCENT_NULL.getName());
        } else {
            result.getData().put("percent", convertPercent.getPercent());
        }
        ConvertLevel convertLevel = convertLevelService.getEnable();
        if (convertLevel == null) {
            result.getData().put("rule", ErrorCode.CONVERT_LEVEL_NULL.getName());
        } else {
            result.getData().put("rule", convertLevel.getRule());
        }
        ReplenishPercent replenishPercent = replenishPercentService.findEnable();
        if (replenishPercent == null) {
            result.getData().put("replenishPercent", ErrorCode.REPLENISHPERCENT_NULL.getName());
        } else {
            result.getData().put("replenishPercent", replenishPercent.getRule());
        }
        TicketPercent ticketPercent = ticketPercentService.findEnable();
        if (ticketPercent == null) {
            result.getData().put("ticketPercent", ErrorCode.TICKETPERCENT_NULL.getName());
        } else {
            result.getData().put("ticketPercent", ticketPercent.getRule());
        }
        PayoutConfig payoutConfig = payoutConfigService.findEnable();
        if (payoutConfig == null) {
            result.getData().put("payoutConfig", ErrorCode.WITHDRAW_NOT_EXIST.getName());
        } else {
            result.getData().put("payoutConfig", payoutConfig.getRule());
        }
        List<LevelUp> levelUps = levelUpService.findByAvailable(true);
        JSONArray levelUpArray = new JSONArray();
        if (levelUps != null && levelUps.size() > 0) {
            for (LevelUp levelUp : levelUps) {
                JSONObject json = new JSONObject();
                json.put("rule", levelUp.toString());
                levelUpArray.add(json);
            }
        } else {
            JSONObject json = new JSONObject();
            json.put("rule", ErrorCode.USER_UPLEVEL_NOT_EXIST.getName());
            levelUpArray.add(json);
        }
        result.getData().put("levelUps", levelUpArray);

        return result;
    }

    /**
     * 根据id查询管理员信息
     *
     * @return DataResult
     */
    @Access(authorities = PermissionEnum.ADMIN_VIEW)
    @GetMapping("/admin/{id}")
    @ApiOperation(value = "根据Id查询管理员信息", notes = "返回码: {'0000', '操作成功','0012':'用户不存在'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "id", value = "管理员Id", required = true, paramType = "path", dataType = "Long")
    })
    public DataResult findAdmin(String token, @PathVariable Long id) {
        DataResult result = new DataResult();
        adminService.getAdminInfo(id, result);
        return result;
    }

    /**
     * 添加管理员信息
     *
     * @return
     */
    @Access(authorities = PermissionEnum.ADMIN_ADD)
    @PostMapping("/admin/save")
    @ApiOperation(value = "添加管理员信息", notes = "返回码: {'0000', '操作成功'," +
            "'0019':'用户名称为空','0006':'密码为空','0001':'用户已存在','0019':'角色不存在','0023':'登录名已存在'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "save", value = "修改信息", required = true, paramType = "body", dataType = "Admin")
    })
    public DataResult saveAdmin(@RequestHeader String token, @RequestBody Admin save) {
        DataResult result = new DataResult();
        adminService.save(save, result);
        return result;
    }

    /**
     * 根据id修改管理员信息
     */
    @Access(authorities = PermissionEnum.ADMIN_EDIT)
    @PostMapping("/admin/update")
    @ApiOperation(value = "更新管理员信息", notes = "返回码: {'0000', '操作成功','0022':'Id 为空'" +
            "'0019':'用户名称为空','0006':'密码为空','0012':'用户不存在','0019':'角色不存在'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "update", value = "修改信息", required = true, paramType = "body", dataType = "Admin")
    })
    public DataResult updateAdmin(@RequestHeader String token, @RequestBody Admin update) {
        DataResult result = new DataResult();
        adminService.update(update, result);
        return result;
    }

    /**
     * 管理员列表
     *
     * @param username
     * @param page
     * @param size
     * @return
     */
    @Access(authorities = PermissionEnum.ADMIN_LIST)
    @GetMapping("/admins")
    @ApiOperation(value = "管理员列表信息", notes = "返回码: {'0000', '操作成功'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "username", value = "用户名称", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "显示记录数", required = true, paramType = "query", dataType = "int", defaultValue = "10")
    })
    public DataResult admins(@RequestHeader String token,
                             @RequestParam(required = false) String username,
                             @RequestParam(defaultValue = "1") int page,
                             @RequestParam int size) {
        DataResult result = new DataResult();
        adminService.getAdminList(username, page, size, result);
        return result;
    }


    @Access(authorities = PermissionEnum.USER_RECHARGE)
    @PostMapping("/recharge")
    @ApiOperation(value = "根据用户id充值", notes = "返回码: {'0000', '操作成功','0012', '用户不存在','0030': '充值额不得小于一万'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "id", value = "用户id", required = true, paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "amount", value = "充值额", required = true, paramType = "query", dataType = "BigDecimal")
    })
    public DataResult recharge(@RequestHeader String token, Long id, BigDecimal amount) {

        Long adminId = adminService.getAdminId(token);
        DataResult result = new DataResult();
        User user = userService.findById(id);
        if (user == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
            return result;
        }
        if (NumberUtil.isLess(amount, NumConstants.TEN_THOUSAND)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.RECHARGE_LIMIT);
            return result;
        }
        ConvertLevel convertLevel = convertLevelService.getEnable();
        if (convertLevel == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.CONVERT_LEVEL_NULL);
            return result;
        }
        Account account = accountService.findByUser(user);
        accountService.recharge(amount, account, adminId, true, true, false);
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }


    @Access(authorities = PermissionEnum.USER_REPLENISH)
    @PostMapping("/replenish")
    @ApiOperation(value = "根据用户id补货", notes = "返回码: {'0000', '操作成功','0012'," +
            "'用户不存在','0036': '用户等级错误','0057':'补货额不得小于1','0055','补货率未配置'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "id", value = "用户id", required = true, paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "amount", value = "补货金额", required = true, paramType = "query", dataType = "BigDecimal")
    })
    public DataResult replenish(@RequestHeader String token, Long id, BigDecimal amount) {

        Long adminId = adminService.getAdminId(token);
        DataResult result = new DataResult();
        User user = userService.findById(id);
        if (user == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
            return result;
        } else if (user.getUserLevel().getIndex() < UserLevel.LEVEL_ELEVEN.getIndex()) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USERLEVEL_IS_ERROR);
            return result;
        }
        if (NumberUtil.isLess(amount, BigDecimal.ONE)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.REPLENISH_LIMIT);
            return result;
        }
        ReplenishPercent replenishPercent = replenishPercentService.findEnable();
        if (replenishPercent == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.REPLENISHPERCENT_NULL);
            return result;
        }
        Account account = accountService.findByUser(user);
        replenishService.replenish(amount, account, adminId);
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    /**
     * 查询用户
     *
     * @param id
     * @return
     */
    @Access(authorities = PermissionEnum.USER_VIEW)
    @ApiOperation(value = "根据id查询用户", notes = "返回码: {'0000'：'操作成功','0012': '用户不存在'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "id", value = "用户id", required = true, paramType = "path", dataType = "Long")
    })
    @GetMapping("/user/{id}")
    public DataResult findByUserId(@RequestHeader String token, @PathVariable Long id) {
        DataResult result = new DataResult();
        JSONObject data = result.getData();
        User user = userService.findById(id);
        if (user == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
            return result;
        }
        JSONObject json = new JSONObject();
        json.put("id", user.getId());
        json.put("username", user.getUsername());
        json.put("mobile", user.getMobile());
        json.put("shopKeeper", user.getShopKeeper());
        json.put("userLevel", user.getUserLevel().getName());
        json.put("replenish", user.getUserLevel().getIndex() > UserLevel.LEVEL_SIX.getIndex());
        User parent = user.getParent();
        if (parent != null) {
            json.put("parentName", parent.getUsername());
            json.put("parentId", parent.getId());
        }
        json.put("state", user.getState());
        data.put("user", json);
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    /**
     * 用户列表
     */
    @Access(authorities = PermissionEnum.USER_LIST)
    @ApiOperation(value = "获取用户列表", notes = "返回码: {'0000'：'操作成功','0014'：'日期格式错误','0010':'令牌错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "username", value = "用户名称", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "mobile", value = "手机号码", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "start", value = "日期（2017-11-01）", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "end", value = "终止日期（2017-11-01）", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "显示记录数", required = true, paramType = "query", dataType = "int", defaultValue = "10")
    })
    @GetMapping("/users")
    public DataResult users(@RequestHeader String token,
                            @RequestParam String username,
                            @RequestParam String mobile,
                            @RequestParam String start,
                            @RequestParam String end,
                            @RequestParam int page,
                            @RequestParam int size) {
        DataResult result = new DataResult();
        if (!DateUtil.isRightDate(result, start)) {
            return result;
        }
        if (!DateUtil.isRightDate(result, end)) {
            return result;
        }
        Date startDate = DateUtil.stringToDate(start, DateStyle.YYYY_MM_DD);
        Date endDate = DateUtil.stringToDate(end, DateStyle.YYYY_MM_DD);
        endDate = DateUtil.getMidnight(endDate);
        return userService.findUsersByUsernameWithDate(username, startDate, endDate, page, size, mobile, result);
    }

    /**
     * 根据用户id获取积分列表
     */
    @Access(authorities = PermissionEnum.USER_VIEW)
    @ApiOperation(value = "根据用户id获取积分列表", notes = "返回码: {'0000'：'操作成功','0010':'令牌错误','0012': '用户不存在'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "记录数", required = true, paramType = "query", dataType = "int", defaultValue = "10")
    })
    @GetMapping("/scoreRecords/{userId}")
    public DataResult getScoreRecordsByUserId(@RequestHeader String token,
                                              @PathVariable Long userId,
                                              @RequestParam int page,
                                              @RequestParam int size) {
        DataResult result = new DataResult();
        User user = userService.findById(userId);
        if (user == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
            return result;
        }
        Page<ScoreRecord> scoreRecordPage = scoreRecordService.findByUserId(userId, page, size);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        JSONObject data = result.getData();
        data.put("page", CommonUtil.generatePageJSON(scoreRecordPage));
        List<ScoreRecord> scoreRecords = scoreRecordPage.getContent();
        Account account = accountService.findByUser(user);
        BigDecimal score = account.getScore().setScale(NumConstants.TWO, BigDecimal.ROUND_FLOOR);
        BigDecimal frozenScore = account.getFrozenScore().setScale(NumConstants.TWO, BigDecimal.ROUND_FLOOR);
        data.put("frozenScore", frozenScore);
        data.put("score", score);
        data.put("totalScore", frozenScore.add(score).setScale(NumConstants.TWO, BigDecimal.ROUND_FLOOR));
        JSONArray array = new JSONArray();
        for (ScoreRecord scoreRecord : scoreRecords) {
            JSONObject json = new JSONObject();
            json.put("id", scoreRecord.getId());
            if (scoreRecord.getUser() != null) {
                json.put("username", scoreRecord.getUser().getUsername());
            } else {
                json.put("username", "");
            }
            json.put("amount", scoreRecord.getAmount().setScale(NumConstants.TWO, BigDecimal.ROUND_FLOOR));
            json.put("frozen", scoreRecord.getFrozen().setScale(NumConstants.TWO, BigDecimal.ROUND_FLOOR));
            json.put("score", scoreRecord.getScore().setScale(NumConstants.TWO, BigDecimal.ROUND_FLOOR));
            json.put("minus", scoreRecord.getMinus());
            json.put("total", scoreRecord.getTotal().setScale(NumConstants.TWO, BigDecimal.ROUND_FLOOR));
            json.put("remark", scoreRecord.getRemark());
            json.put("scoreSource", scoreRecord.getScoreSource().getName());
            json.put("createDate", DateUtil.dateToString(scoreRecord.getCreateDate(), DateStyle.YYYY_MM_DD_HH_MM_SS));
            array.add(json);
        }
        data.put("scoreRecords", array);
        return result;

    }


    /**
     * 积分列表
     */
    @Access(authorities = PermissionEnum.SCORE_LIST)
    @ApiOperation(value = "获取积分列表", notes = "返回码: {'0000'：'操作成功','0014'：'日期格式错误','0010':'令牌错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "start", value = "日期（2017-11-01）", paramType = "query", dataType = "String", defaultValue = "2017-11-01"),
            @ApiImplicitParam(name = "end", value = "终止日期（2017-11-01）", paramType = "query", dataType = "String", defaultValue = "2017-11-30"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "记录数", required = true, paramType = "query", dataType = "int", defaultValue = "10")
    })
    @GetMapping("/scoreRecords")
    public DataResult scoreRecords(@RequestHeader String token,
                                   @RequestParam String start,
                                   @RequestParam String end,
                                   @RequestParam int page,
                                   @RequestParam int size) {
        DataResult result = new DataResult();
        if (!DateUtil.isRightDate(result, start)) {
            return result;
        }
        if (!DateUtil.isRightDate(result, end)) {
            return result;
        }
        Date startDate = DateUtil.stringToDate(start, DateStyle.YYYY_MM_DD);

        Date endDate = DateUtil.stringToDate(end, DateStyle.YYYY_MM_DD);
        endDate = DateUtil.getMidnight(endDate);
        return scoreRecordService.findScoreRecordsByDate(startDate, endDate, page, size, result);
    }

    /**
     * 角色列表
     */
    @Access(authorities = PermissionEnum.ROLE_LIST)
    @ApiOperation(value = "获取角色列表", notes = "返回码: {'0000'：'操作成功','0010':'令牌错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "记录数", required = true, paramType = "query", dataType = "int", defaultValue = "10")
    })
    @GetMapping("/roles")
    public DataResult roles(@RequestHeader String token,
                            @RequestParam int page,
                            @RequestParam int size) {
        DataResult result = new DataResult();
        return roleService.list(result, page, size);
    }

    /**
     * 权限列表
     */
    @Access(authorities = PermissionEnum.PERMISSION_LIST)
    @ApiOperation(value = "权限列表", notes = "返回码: {'0000'：'操作成功','0010':'令牌错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "记录数", required = true, paramType = "query", dataType = "int", defaultValue = "10")
    })
    @GetMapping("/permissions")
    public DataResult permissions(@RequestHeader String token,
                                  @RequestParam int page,
                                  @RequestParam int size) {
        DataResult result = new DataResult();
        return permissionService.list(result, page, size);
    }

    /**
     * 获取编辑权限信息
     */
    @Access(authorities = PermissionEnum.PERMISSION_VIEW)
    @ApiOperation(value = "获取编辑权限信息", notes = "返回码: {'0000'：'操作成功','0018': '权限不存在'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "权限id", required = true, paramType = "path", dataType = "Long")
    })
    @GetMapping("/permission/{id}")
    public DataResult findByPermissionId(@PathVariable Long id) {
        return permissionService.findById(id);
    }

    /**
     * 编辑权限
     *
     * @param permission
     * @return
     */
    @Access(authorities = PermissionEnum.PERMISSION_EIDT)
    @PutMapping("/permission/edit")
    @ApiOperation(value = "编辑权限", notes = "返回码: {'0000'：'操作成功','0018': '权限不存在','0018': '权限参数错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "permission", value = "实体类", required = true, paramType = "body", dataType = "Permission")
    })
    public DataResult editPermission(@RequestBody Permission permission) {
        return permissionService.edit(permission);
    }

    /**
     * 添加权限
     *
     * @param permission
     * @return
     */
    @Access(authorities = PermissionEnum.PERMISSION_ADD)
    @ApiOperation(value = "添加权限", notes = "返回码: {'0000'：'操作成功','0018': '权限不存在','0018': '权限参数错误','0025': '权限名称为空'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "permission", value = "实体类", required = true, paramType = "body", dataType = "Permission")
    })
    @PutMapping("/permission/add")
    public DataResult addPermission(@RequestBody Permission permission) {
        DataResult result = new DataResult();
        return permissionService.save(permission, result);
    }

    /**
     * 充值列表
     */
    @Access(authorities = PermissionEnum.SCORE_LIST)
    @ApiOperation(value = "充值记录列表", notes = "返回码: {'0000'：'操作成功','0014'：'日期格式错误','0010':'令牌错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "mobile", value = "手机号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "start", value = "日期（2017-11-01）", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "end", value = "终止日期（2017-11-01）", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "记录数", required = true, paramType = "query", dataType = "int", defaultValue = "10")
    })
    @GetMapping("/rechargeRecords")
    public DataResult rechargeRecords(@RequestHeader String token,
                                      @RequestParam(required = false) String mobile,
                                      @RequestParam String start,
                                      @RequestParam String end,
                                      @RequestParam int page,
                                      @RequestParam int size) {
        DataResult result = new DataResult();
        if (!DateUtil.isRightDate(result, start)) {
            return result;
        }
        if (!DateUtil.isRightDate(result, end)) {
            return result;
        }
        Date startDate = DateUtil.stringToDate(start, DateStyle.YYYY_MM_DD);
        Date endDate = DateUtil.stringToDate(end, DateStyle.YYYY_MM_DD);
        endDate = DateUtil.getMidnight(endDate);
        return rechargeRecordService.findRechargeRecordsByDate(startDate, endDate, mobile, page, size, result);
    }

    /**
     * 获取用户充值信息
     */
    @Access(authorities = PermissionEnum.USER_VIEW)
    @ApiOperation(value = "用户充值信息", notes = "返回码: {'0000'：'操作成功','0014'：'日期格式错误','0012'：'用户不存在','0010':'令牌错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "user", value = "请求对象", required = true, paramType = "body", dataType = "User"),
            @ApiImplicitParam(name = "start", value = "日期（2017-11-01）", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "end", value = "终止日期（2017-11-01）", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "记录数", required = true, paramType = "query", dataType = "int", defaultValue = "10")
    })
    @GetMapping("/user/rechargeRecords")
    public DataResult findRechargeRecordsByUserId(@RequestHeader String token,
                                                  @RequestBody User user,
                                                  @RequestParam String start,
                                                  @RequestParam String end,
                                                  @RequestParam int page,
                                                  @RequestParam int size) {
        DataResult result = new DataResult();
        if (!DateUtil.isRightDate(result, start)) {
            return result;
        }
        if (!DateUtil.isRightDate(result, end)) {
            return result;
        }
        Date startDate = DateUtil.stringToDate(start, DateStyle.YYYY_MM_DD);
        Date endDate = DateUtil.stringToDate(end, DateStyle.YYYY_MM_DD);
        endDate = DateUtil.getMidnight(endDate);
        return rechargeRecordService.findRechargeRecordsByUserIdWithDate(user, startDate, endDate, page, size, result);
    }

    /**
     * 编辑用户
     */
    @Access(authorities = PermissionEnum.USER_EDIT)
    @ApiOperation(value = "编辑用户", notes = "返回码: {'0000'：'操作成功','0002'：'用户未注册','0007', '手机号格式错误','0010':'令牌错误'," +
            "'0001', '用户已存在','0007', '手机号格式错误','0013', '密码格式错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "user", value = "请求对象", required = true, paramType = "body", dataType = "User")
    })
    @PutMapping("/user/edit")
    public DataResult editUser(@RequestHeader String token, @RequestBody User user) {
        DataResult result = userService.edit(user);
        return result;
    }

    /**
     * 添加用户
     */
    @Access(authorities = PermissionEnum.USER_ADD)
    @ApiOperation(value = "添加用户", notes = "返回码: {'0000'：'操作成功','0005': '手机号为空,'0002'：'用户未注册','0007':'手机号格式错误','0010':'令牌错误'," +
            "'0001', '用户已存在','0007', '手机号格式错误','0013', '密码格式错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "user", value = "请求对象", required = true, paramType = "body", dataType = "User")
    })
    @PostMapping("/user/add")
    public DataResult addUser(@RequestHeader String token, @RequestBody User user) {
        DataResult result = new DataResult();
        if (StringUtil.isEmpty(user.getMobile())) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.MOBILE_NULL);
            return result;
        }

        if (!CommonUtil.isRightPhone(user.getMobile())) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.MOBILE_FORMAT_ERROR);
            return result;
        }
        if (userService.findByMobile(user.getMobile()) != null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_EXIST);
            return result;
        }
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setSalt(UUIDUtil.createRandomString(32));
        user.setPassword(Md5Util.doubleMD5(user.getPassword() + user.getSalt()));
        user.setShopKeeper(false);
        user.setState((byte) 1);
        userService.save(user);
        Account account = accountService.regScore(user);
        user.setAccount(account);
        userService.save(user);
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    /**
     * 用户 available 操作
     *
     * @param token
     * @param id
     * @return
     */
    @Access(authorities = PermissionEnum.USER_LOCK)
    @PutMapping("/user/available/{id}")
    @ApiOperation(value = "锁定用户", notes = "返回码: {'0000'：'操作成功','0010':'令牌错误','0012: '用户不存在'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "id", value = "用户id", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "flag", value = "true启用，false 停用", required = true, paramType = "query", dataType = "Boolean")
    })
    public DataResult userIsavailableOpt(@RequestHeader String token, @PathVariable Long id, @RequestParam Boolean flag) {
        DataResult result = new DataResult();
        return userService.userIsavailableOpt(id, flag, result);
    }


    /**
     * 管理员 available 操作
     *
     * @param token
     * @param id
     * @return
     */
    @Access(authorities = PermissionEnum.ADMIN_LOCK)
    @PutMapping("/admin/available/{id}")
    @ApiOperation(value = "管理员 available 操作", notes = "返回码: {'0000'：'操作成功','0010':'令牌错误','0012: '用户不存在','0060':'初始管理员'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "id", value = "管理员id", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "flag", value = "true启用，false 停用", required = true, paramType = "query", dataType = "Boolean")
    })
    public DataResult adminIsavailableOpt(@RequestHeader String token, @PathVariable Long id, @RequestParam Boolean flag) {
        DataResult result = new DataResult();
        return adminService.adminIsavailableOpt(id, flag, result);
    }

    /**
     * 查询编辑角色
     *
     * @param id
     * @return
     */
    @Access(authorities = PermissionEnum.ROLE_VIEW)
    @GetMapping("/role/{id}")
    @ApiOperation(value = "查询编辑角色", notes = "返回码: {'0000'：'操作成功','0010':'令牌错误','0019': '角色不存在'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "id", value = "角色id", required = true, paramType = "path", dataType = "Long")
    })
    public DataResult findByRoleId(@RequestHeader String token, @PathVariable Long id) {
        return roleService.findById(id);
    }

    /**
     * 编辑角色
     *
     * @param role
     * @return
     */
    @Access(authorities = PermissionEnum.ROLE_EDIT)
    @PutMapping("/role/edit")
    @ApiOperation(value = "编辑角色", notes = "返回码: {'0000'：'操作成功','0010':'令牌错误','0019': '角色不存在','0026': '角色名称已存在'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "role", value = "角色json", required = true, paramType = "body", dataType = "Role")
    })
    public DataResult editRole(@RequestBody Role role) {
        return roleService.edit(role);
    }

    /**
     * 添加角色
     *
     * @param role
     * @return
     */
    @Access(authorities = PermissionEnum.ROLE_ADD)
    @PostMapping("/role/add")
    @ApiOperation(value = "添加角色", notes = "返回码: {'0000'：'操作成功','0010':'令牌错误','0019': '角色不存在','0026': '角色名称已存在'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "role", value = "角色json", required = true, paramType = "body", dataType = "Role")
    })
    public DataResult addRole(@RequestBody Role role) {
        DataResult result = new DataResult();
        return roleService.save(role, result);
    }

    /**
     * 权限available 操作
     */
    @Access(authorities = PermissionEnum.PERMISSION_LOCK)
    @PutMapping("/permission/available/{id}")
    @ApiOperation(value = "权限available 操作", notes = "返回码: {'0000'：'操作成功','0010':'令牌错误','0018': '权限不存在'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "id", value = "权限id", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "flag", value = "true启用，false 停用", required = true, paramType = "query", dataType = "Boolean")
    })
    public DataResult permissionIsavailableOpt(@RequestHeader String token, @PathVariable Long id, @RequestParam Boolean flag) {
        DataResult result = new DataResult();
        return permissionService.permissionIsavailableOpt(id, flag, result);
    }

    /**
     * 角色available 操作
     */
    @Access(authorities = PermissionEnum.ROLE_LOCK)
    @PutMapping("/role/available/{id}")
    @ApiOperation(value = "角色available 操作", notes = "返回码: {'0000'：'操作成功','0010':'令牌错误','0019': '角色不存在'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "id", value = "角色id", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "flag", value = "true启用，false 停用", required = true, paramType = "query", dataType = "Boolean")
    })
    public DataResult roleIsavailableOpt(@RequestHeader String token, @PathVariable Long id, @RequestParam Boolean flag) {
        DataResult result = new DataResult();
        return roleService.roleIsavailableOpt(id, flag, result);
    }

    /**
     * 获取用户所有上级
     */
    @Access(authorities = PermissionEnum.USER_VIEW)
    @GetMapping("/user/parent/{id}")
    @ApiOperation(value = "获取用户所有上级 操作", notes = "返回码: {'0000'：'操作成功','0010':'令牌错误','0012', '用户不存在'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "id", value = "用户id", required = true, paramType = "path", dataType = "Long")
    })
    public DataResult getUserParent(@RequestHeader String token, @PathVariable Long id) {
        DataResult result = new DataResult();
        return userService.findUserParents(id, result);
    }

    /**
     * 获取用户所有下级
     */
    @Access(authorities = PermissionEnum.USER_VIEW)
    @GetMapping("/user/junior/{id}")
    @ApiOperation(value = "获取用户所有下级 操作", notes = "返回码: {'0000'：'操作成功','0010':'令牌错误','0012', '用户不存在'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "id", value = "用户id", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "记录数", required = true, paramType = "query", dataType = "int", defaultValue = "10")
    })
    public DataResult getUserJunior(@RequestHeader String token, @PathVariable Long id,
                                    @RequestParam int page, @RequestParam int size) {
        DataResult result = new DataResult();
        return userService.findUserJuniors(id, page, size, result);
    }

    /**
     * 系统修改用户积分
     */
    @Access(authorities = PermissionEnum.USER_SCORE)
    @PostMapping("/modify/score/{userId}")
    @ApiOperation(value = "系统修改用户积分 操作", notes = "返回码: {'0000'：'操作成功','0010':'令牌错误','0012', '用户不存在',0031': '积分修改额大于0'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "amount", value = "修改积分数", required = true, paramType = "query", dataType = "BigDecimal"),
            @ApiImplicitParam(name = "frozen", value = "true 冻结积分，false 兑换积分", required = true, paramType = "query", dataType = "Boolean"),
            @ApiImplicitParam(name = "minus", value = "true 减，false 加", required = true, paramType = "query", dataType = "Boolean"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "path", dataType = "Long")
    })
    public DataResult modifyScore(@RequestHeader String token, @PathVariable Long userId, @RequestParam BigDecimal amount, @RequestParam boolean frozen, @RequestParam boolean minus) {
        DataResult result = new DataResult();
        Long adminId = adminService.getAdminId(token);
        return accountService.updateAccountByUserId(userId, amount, minus, adminId, frozen, result);
    }

    /**
     * 调整上级
     *
     * @param token
     * @param userId
     * @param parentId
     * @return
     */
    @Access(authorities = PermissionEnum.USER_PARENT)
    @PostMapping("/modify/parent/{userId}")
    @ApiOperation(value = "调整上级", notes = "返回码: {'0000'：'操作成功','0010':'令牌错误','0028': '上级不能为自己', '0029': '不能为循环上级'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "parentId", value = "上级id", required = true, paramType = "query", dataType = "Long")
    })
    public DataResult changeParent(@RequestHeader String token, @PathVariable Long userId, @RequestParam Long parentId) {
        Long adminId = adminService.getAdminId(token);
        return userService.changeParent(userId, parentId, adminId);
    }


    /**
     * 用户导出
     *
     * @return
     */
    @Access(authorities = PermissionEnum.USER_EXPORT)
    @GetMapping("/download/user")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名称", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "mobile", value = "手机号码", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "start", value = "日期（2017-11-01）", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "end", value = "终止日期（2017-11-30）", required = false, paramType = "query", dataType = "String")
    })
    public DataResult downLoadUsers(@RequestParam String username,
                                    @RequestParam String mobile,
                                    @RequestParam String start,
                                    @RequestParam String end) throws IOException {

        DataResult result = new DataResult();
        if (!DateUtil.isRightDate(result, start) || !DateUtil.isRightDate(result, end)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.DATE_FORMAT_ERROR);
            return result;
        }
        Date startDate = DateUtil.stringToDate(start, DateStyle.YYYY_MM_DD);
        Date endDate = DateUtil.stringToDate(end, DateStyle.YYYY_MM_DD);
        return userService.exportExcel(username, mobile, startDate, endDate, result);
    }

    /**
     * 用户升级
     *
     * @param token
     * @param userId
     * @param index
     * @return
     */
    @Access(authorities = PermissionEnum.USER_LEVEL_UP)
    @PostMapping("/userLevel/{userId}")
    @ApiOperation(value = "用户升级", notes = "返回码: {'0000'：'操作成功','0010':'令牌错误','0012': '用户不存在', '0037': '用户升级金额未配置',0036': '用户等级错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "amount", value = "升级金额", required = false, paramType = "query", dataType = "BigDecimal"),
            @ApiImplicitParam(name = "index", value = "用户级别", required = true, paramType = "query", dataType = "int")
    })
    public DataResult saveUserLevelRecord(@RequestHeader String token,
                                          @PathVariable Long userId,
                                          @RequestParam int index,
                                          @RequestParam(required = false, defaultValue = "0") BigDecimal amount) {
        DataResult result = new DataResult();
        Long adminId = adminService.getAdminId(token);
        return userService.userLevel(userId, UserLevel.getEnumByIndex(index), adminId, amount, result);
    }

    /**
     * 用户成为店主
     *
     * @param token
     * @param userId
     * @return
     */
    @Access(authorities = PermissionEnum.USER_LEVEL_UP)
    @PostMapping("/shopKeeper/{userId}")
    @ApiOperation(value = "用户成为店主", notes = "返回码: {'0000'：'操作成功','0010':'令牌错误','0012': '用户不存在', '0037': '用户升级金额未配置','0042':'用户已是店主'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "path", dataType = "Long")
    })
    public DataResult upShopKeeper(@RequestHeader String token, @PathVariable Long userId) {
        DataResult result = new DataResult();
        Long adminId = adminService.getAdminId(token);
        return userService.upShopKeeper(userId, adminId, result);
    }

    /**
     * 获取比用户高的级别
     */
    @Access(authorities = PermissionEnum.USER_VIEW)
    @GetMapping("/high/level/{userId}")
    @ApiOperation(value = "获取比用户高的级别", notes = "返回码: {'0000', '操作成功','0010':'令牌错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "name", value = "当前级别", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, paramType = "path", dataType = "Long")
    })
    public DataResult findHigherLevel(@RequestHeader String token, @RequestParam("name") String name, @PathVariable("userId") Long userId) {
        DataResult result = new DataResult();
        return levelUpService.findHigherLevel(result, UserLevel.getEnumByName(name).getIndex(), userId);
    }

    /**
     * 根据类型获取用户的积分记录
     */
    @Access(authorities = PermissionEnum.BALANCE_LIST)
    @GetMapping("/userLevelRecords/isFrozened/{userId}")
    @ApiOperation(value = "获取用户升级记录", notes = "返回码: {'0000'：'操作成功','0014'：'日期格式错误','0010':'令牌错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "isFrozened", value = "积分类型", required = true, paramType = "query", dataType = "Boolean"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "记录数", required = true, paramType = "query", dataType = "int", defaultValue = "10")
    })
    public DataResult findIsFrozenedRecordByUserId(@RequestHeader String token, @PathVariable Long userId,
                                                   @RequestParam boolean isFrozened,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        DataResult result = new DataResult();
        return scoreRecordService.findScoreRecordByUserIdAndType(userId, page, size, isFrozened, result);
    }

    /**
     * 获取用户余额
     */
    @Access(authorities = PermissionEnum.USER_VIEW)
    @GetMapping("/balanceRecords/{userId}")
    @ApiOperation(value = "获取用户余额", notes = "返回码: {'0000'：'操作成功','0012'：'用户不存在','0010':'令牌错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "记录数", required = true, paramType = "query", dataType = "int", defaultValue = "10")
    })
    public DataResult findBalanceRecordByUserId(@RequestHeader String token,
                                                @PathVariable Long userId,
                                                @RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        DataResult result = new DataResult();
        return accountService.findBalanceRecordByUserId(userId, page, size, result);
    }

    /**
     * 获取资格列表
     */
    @Access(authorities = PermissionEnum.QUALIFICATION_LIST)
    @GetMapping("/qualification/users")
    @ApiOperation(value = "用户资格列表", notes = "返回码: {'0000'：'操作成功','0010':'令牌错误','0012': '用户不存在',}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名称", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "mobile", value = "手机号码", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "start", value = "日期（2017-11-01）", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "end", value = "终止日期（2017-11-30）", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "记录数", required = true, paramType = "query", dataType = "int", defaultValue = "10")
    })
    public DataResult findQualificationUsers(@RequestHeader String token,
                                             @RequestParam String username,
                                             @RequestParam String mobile,
                                             @RequestParam String start,
                                             @RequestParam String end,
                                             @RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        DataResult result = new DataResult();
        if (!DateUtil.isRightDate(result, start) || !DateUtil.isRightDate(result, end)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.DATE_FORMAT_ERROR);
            return result;
        }
        Date startDate = DateUtil.stringToDate(start, DateStyle.YYYY_MM_DD);
        Date endDate = DateUtil.stringToDate(end, DateStyle.YYYY_MM_DD);
        return userService.findQualificationUsers(username, mobile, startDate, endDate, page, size, result);
    }

    /**
     * 用户资格升级
     *
     * @param token
     * @param userId
     * @return
     */
    @Access(authorities = PermissionEnum.QUALIFICATION_EDIT)
    @PostMapping("/qualification/{userId}")
    @ApiOperation(value = "用户资格升级", notes = "返回码: {'0000'：'操作成功','0010':'令牌错误','0012': '用户不存在',}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "path", dataType = "Long")
    })
    public DataResult saveQualificationUserLevel(@RequestHeader String token, @PathVariable Long userId) {
        DataResult result = new DataResult();
        Long adminId = adminService.getAdminId(token);
        return userService.saveQualificationUserLevel(userId, adminId, result);
    }

    /**
     * 同意用户申请
     */
    @Access(authorities = PermissionEnum.PAYOUT_RECORDS_PASS)
    @PutMapping("/payoutRecord/approve/{payoutRecordId}")
    @ApiOperation(value = "同意用户申请", notes = "返回码: {'0000'：'操作成功', '0049':'用户提现异常','0050':'流水账号不能为空'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "payoutRecordId", value = "申请记录ID", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "tradeNo", value = "提现流水号", required = true, paramType = "query", dataType = "String")
    })
    public DataResult payoutRecordApprove(@RequestHeader String token,
                                          @PathVariable Long payoutRecordId,
                                          @RequestParam String tradeNo) {
        Long adminId = adminService.getAdminId(token);
        DataResult result = new DataResult();
        return payoutRecordService.payoutRecordApprove(adminId, payoutRecordId, tradeNo, result);
    }

    /**
     * 拒绝用户申请
     */
    @Access(authorities = PermissionEnum.PAYOUT_RECORDS_REFUSE)
    @PutMapping("/payoutRecord/reject/{payoutRecordId}")
    @ApiOperation(value = "拒绝用户申请", notes = "返回码: {'0000'：'操作成功', '0049':'用户提现异常','0050':'流水账号不能为空'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "payoutRecordId", value = "申请记录ID", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "remark", value = "拒绝的理由", required = true, paramType = "query", dataType = "String")
    })
    public DataResult payoutRecordReject(@RequestHeader String token,
                                         @PathVariable Long payoutRecordId,
                                         @RequestParam String remark) {
        Long adminId = adminService.getAdminId(token);
        DataResult result = new DataResult();
        return payoutRecordService.payoutRecordReject(adminId, payoutRecordId, remark, result);
    }

    /**
     * 获取用户提现信息列表
     */
    @Access(authorities = PermissionEnum.PAYOUT_RECORDS_LIST)
    @GetMapping("/payoutRecords")
    @ApiOperation(value = "获取用户提现信息列表", notes = "返回码: {'0000'：'操作成功','0014'：'日期格式错误','0010':'令牌错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "mobile", value = "手机号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "cardNo", value = "银行卡号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "applyStatus", value = "模糊查询用户号码", paramType = "query", dataType = "ApplyStatus"),
            @ApiImplicitParam(name = "start", value = "日期（2017-11-01）", paramType = "query", dataType = "String", defaultValue = "2017-11-01"),
            @ApiImplicitParam(name = "end", value = "终止日期（2017-11-01）", paramType = "query", dataType = "String", defaultValue = "2017-11-30"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "记录数", required = true, paramType = "query", dataType = "int", defaultValue = "10")
    })
    public DataResult findPayoutRecords(@RequestHeader String token,
                                        @RequestParam(required = false) String mobile,
                                        @RequestParam(required = false) String cardNo,
                                        @RequestParam(required = false) ApplyStatus applyStatus,
                                        @RequestParam(defaultValue = "2017-11-01") String start,
                                        @RequestParam(defaultValue = "2017-11-30") String end,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        DataResult result = new DataResult();
        if (!DateUtil.isRightDate(result, start) || !DateUtil.isRightDate(result, end)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.DATE_FORMAT_ERROR);
            return result;
        }
        Date startDate = DateUtil.stringToDate(start, DateStyle.YYYY_MM_DD);
        Date endDate = DateUtil.stringToDate(end, DateStyle.YYYY_MM_DD);
        endDate = DateUtil.getMidnight(endDate);
        return payoutRecordService.list(startDate, endDate, page, size, mobile, cardNo, applyStatus, result);
    }

    /**
     * 获取获取提现信息详情
     */
    @Access(authorities = PermissionEnum.PAYOUT_RECORDS_LIST)
    @GetMapping("/payoutRecord/{id}")
    @ApiOperation(value = "获取用户提现信息列表", notes = "返回码: {'0000'：'操作成功','0010':'令牌错误','0051':'用户提现异常'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "id", value = "记录id", required = true, paramType = "path", dataType = "Long")
    })
    public DataResult findPayoutRecordById(@RequestHeader String token, @PathVariable Long id
    ) {
        DataResult result = new DataResult();
        return payoutRecordService.findPayoutRecordById(id, result);
    }

    /**
     * 提现列表导出
     *
     * @return
     */
    @Access(authorities = PermissionEnum.USER_EXPORT)
    @GetMapping("/download/payoutRecords")
    public DataResult downloadPayoutRecords(@RequestHeader String token,
                                            @RequestParam(required = false) String mobile,
                                            @RequestParam(required = false) String cardNo,
                                            @RequestParam(required = false) ApplyStatus applyStatus,
                                            @RequestParam(defaultValue = "2017-11-01") String start,
                                            @RequestParam(defaultValue = "2017-11-30") String end,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size) {

        DataResult result = new DataResult();
        if (!DateUtil.isRightDate(result, start) || !DateUtil.isRightDate(result, end)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.DATE_FORMAT_ERROR);
            return result;
        }
        Date startDate = DateUtil.stringToDate(start, DateStyle.YYYY_MM_DD);
        Date endDate = DateUtil.stringToDate(end, DateStyle.YYYY_MM_DD);
        return payoutRecordService.exportExcel(mobile, cardNo, startDate, endDate, applyStatus, result);
    }

    /**
     * 根据用户Id充值报单积分
     *
     * @param id
     * @param amount
     * @return DataResult
     */
    @Access(authorities = PermissionEnum.BITCOIN_RECORD_ADD)
    @PostMapping("/recharge/bitcoin")
    @ApiOperation(value = "根据用户Id充值报单积分", notes = "返回码: {'0000', '操作成功','0012', '用户不存在','0030': '充值额不得小于一万'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "id", value = "用户id", required = true, paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "amount", value = "充值额", required = true, paramType = "query", dataType = "BigDecimal"),
            @ApiImplicitParam(name = "type", value = "增减", required = true, paramType = "query", dataType = "Boolean")
    })
    public DataResult rechargeBitcoin(@RequestHeader String token,
                                      @RequestParam Long id,
                                      @RequestParam BigDecimal amount,
                                      @RequestParam Boolean type) {

        Long adminId = adminService.getAdminId(token);
        DataResult result = new DataResult();
        User user = userService.findById(id);
        if (user == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
            return result;
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.BITCOIN_LIMIT);
            return result;
        }
        if (type == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.UNKNOW_ERROR);
            return result;
        }
        Account account = accountService.findByUser(user);
        if (type) {
            if (account.getBitcoin().compareTo(amount) < 0) {
                result.setMsg(MsgConstants.OPT_FAIL);
                result.setDataMsg(ErrorCode.BITCOIN_LIMIT);
                return result;
            }
        }
        accountService.rechargeBitcoin(amount, account, adminId, type);
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    /**
     * 获取报单积分列表
     */
    @Access(authorities = PermissionEnum.BITCOIN_RECORD_LIST)
    @GetMapping("/bitcoinRecords")
    @ApiOperation(value = "活跃报单积分列表", notes = "返回码: {'0000'：'操作成功','0014'：'日期格式错误','0010':'令牌错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "mobile", value = "手机号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "start", value = "日期（2017-11-01）", paramType = "query", dataType = "String", defaultValue = "2017-11-01"),
            @ApiImplicitParam(name = "end", value = "终止日期（2017-11-30）", paramType = "query", dataType = "String", defaultValue = "2017-11-30"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "记录数", required = true, paramType = "query", dataType = "int", defaultValue = "10")
    })
    public DataResult findBitcoinRecords(@RequestHeader String token,
                                         @RequestParam(required = false) String mobile,
                                         @RequestParam(defaultValue = "2017-11-01") String start,
                                         @RequestParam(defaultValue = "2017-11-30") String end,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        DataResult result = new DataResult();
        if (!DateUtil.isRightDate(result, start) || !DateUtil.isRightDate(result, end)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.DATE_FORMAT_ERROR);
            return result;
        }
        Date startDate = DateUtil.stringToDate(start, DateStyle.YYYY_MM_DD);
        Date endDate = DateUtil.stringToDate(end, DateStyle.YYYY_MM_DD);
        endDate = DateUtil.getMidnight(endDate);
        return bitcoinRecordService.list(mobile, startDate, endDate, page, size, result);
    }

    /**
     * 获取用户的报单积分列表
     */
    @Access(authorities = PermissionEnum.BITCOIN_RECORD_LIST)
    @GetMapping("/bitcoinRecords/{userId}")
    @ApiOperation(value = "活跃报单积分列表", notes = "返回码: {'0000'：'操作成功','0014'：'日期格式错误','0010':'令牌错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "记录数", required = true, paramType = "query", dataType = "int", defaultValue = "10")
    })
    public DataResult findBitcoinRecordsByUserId(@RequestHeader String token,
                                                 @PathVariable Long userId,
                                                 @RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        DataResult result = new DataResult();

        return bitcoinRecordService.findByUserId(userId, page, size, result);
    }

    /**
     * 获取用户的消费劵列表
     */
    @Access(authorities = PermissionEnum.TICKETH_PERCENT_LIST)
    @GetMapping("/ticketRecords/{userId}")
    @ApiOperation(value = "获取用户的消费劵列表", notes = "返回码: {'0000'：'操作成功','0014'：'日期格式错误','0010':'令牌错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "记录数", required = true, paramType = "query", dataType = "int", defaultValue = "10")
    })
    public DataResult findTicketRecordsByUserId(@RequestHeader String token,
                                                @PathVariable Long userId,
                                                @RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        DataResult result = new DataResult();
        return ticketRecordService.findByUserId(userId, page, size, result);
    }

    /**
     * 获取报消费劵列表
     */
    @Access(authorities = PermissionEnum.TICKETH_PERCENT_LIST)
    @GetMapping("/ticketRecords")
    @ApiOperation(value = "获取报消费劵列表", notes = "返回码: {'0000'：'操作成功','0014'：'日期格式错误','0010':'令牌错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "mobile", value = "手机号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "start", value = "日期（2017-11-01）", paramType = "query", dataType = "String", defaultValue = "2017-11-01"),
            @ApiImplicitParam(name = "end", value = "终止日期（2017-11-30）", paramType = "query", dataType = "String", defaultValue = "2017-11-30"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "记录数", required = true, paramType = "query", dataType = "int", defaultValue = "10")
    })
    public DataResult findTicketRecords(@RequestHeader String token,
                                        @RequestParam(required = false) String mobile,
                                        @RequestParam(defaultValue = "2017-11-01") String start,
                                        @RequestParam(defaultValue = "2017-11-30") String end,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        DataResult result = new DataResult();
        if (!DateUtil.isRightDate(result, start) || !DateUtil.isRightDate(result, end)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.DATE_FORMAT_ERROR);
            return result;
        }
        Date startDate = DateUtil.stringToDate(start, DateStyle.YYYY_MM_DD);
        Date endDate = DateUtil.stringToDate(end, DateStyle.YYYY_MM_DD);
        endDate = DateUtil.getMidnight(endDate);
        return ticketRecordService.list(mobile, startDate, endDate, page, size, result);
    }

    /**
     * 获取补货列表
     */
    @Access(authorities = PermissionEnum.REPLENISH_RECORD_LIST)
    @GetMapping("/replenishRecords")
    @ApiOperation(value = "获取补货列表", notes = "返回码: {'0000'：'操作成功','0014'：'日期格式错误','0010':'令牌错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "mobile", value = "手机号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "start", value = "日期（2017-11-01）", paramType = "query", dataType = "String", defaultValue = "2017-11-01"),
            @ApiImplicitParam(name = "end", value = "终止日期（2017-11-30）", paramType = "query", dataType = "String", defaultValue = "2017-11-30"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "记录数", required = true, paramType = "query", dataType = "int", defaultValue = "10")
    })
    public DataResult findReplenishRecords(@RequestHeader String token,
                                           @RequestParam(required = false) String mobile,
                                           @RequestParam(defaultValue = "2017-11-01") String start,
                                           @RequestParam(defaultValue = "2017-11-30") String end,
                                           @RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        DataResult result = new DataResult();
        if (!DateUtil.isRightDate(result, start) || !DateUtil.isRightDate(result, end)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.DATE_FORMAT_ERROR);
            return result;
        }
        Date startDate = DateUtil.stringToDate(start, DateStyle.YYYY_MM_DD);
        Date endDate = DateUtil.stringToDate(end, DateStyle.YYYY_MM_DD);
        endDate = DateUtil.getMidnight(endDate);
        return replenishService.list(mobile, startDate, endDate, page, size, result);
    }

    /**
     * 根据用户Id调整余额
     *
     * @param id
     * @param amount
     * @return DataResult
     */
    @Access(authorities = PermissionEnum.USER_BALANCE)
    @PostMapping("/recharge/balance")
    @ApiOperation(value = "根据用户Id调整余额", notes = "返回码: {'0000', '操作成功','0012', '用户不存在',0049':'用户余额不足','0058': '充值额度错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "id", value = "用户id", required = true, paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "amount", value = "充值额", required = true, paramType = "query", dataType = "BigDecimal"),
            @ApiImplicitParam(name = "minus", value = "增减", required = true, paramType = "query", dataType = "Boolean")
    })
    public DataResult rechargeBalance(@RequestHeader String token,
                                      @RequestParam Long id,
                                      @RequestParam BigDecimal amount,
                                      @RequestParam Boolean minus) {

        Long adminId = adminService.getAdminId(token);
        DataResult result = new DataResult();
        User user = userService.findById(id);
        if (user == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
            return result;
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.BITCOIN_LIMIT);
            return result;
        }
        if (minus == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.UNKNOW_ERROR);
            return result;
        }
        Account account = accountService.findByUser(user);
        if (minus) {
            if (account.getBalance().compareTo(amount) < 0) {
                result.setMsg(MsgConstants.OPT_FAIL);
                result.setDataMsg(ErrorCode.BALANCE_LESS_ERROR);
                return result;
            }
        }
        accountService.rechargeBalance(amount, account, adminId, minus);
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    /**
     * 调整用户的消费劵
     *
     * @param id
     * @param amount
     * @return DataResult
     */
    @Access(authorities = PermissionEnum.TICKETH_PERCENT_EDIT)
    @PostMapping("/recharge/ticket")
    @ApiOperation(value = "调整用户的消费劵", notes = "返回码: {'0000', '操作成功','0012', '用户不存在',0049':'用户余额不足','0058': '充值额度错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "id", value = "用户id", required = true, paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "amount", value = "充值额", required = true, paramType = "query", dataType = "BigDecimal"),
            @ApiImplicitParam(name = "minus", value = "增减", required = true, paramType = "query", dataType = "Boolean")
    })
    public DataResult rechargeTicket(@RequestHeader String token,
                                     @RequestParam Long id,
                                     @RequestParam BigDecimal amount,
                                     @RequestParam Boolean minus) {

        Long adminId = adminService.getAdminId(token);
        DataResult result = new DataResult();
        User user = userService.findById(id);
        if (user == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
            return result;
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.BITCOIN_LIMIT);
            return result;
        }
        if (minus == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.UNKNOW_ERROR);
            return result;
        }
        Account account = accountService.findByUser(user);
        if (minus) {
            if (account.getTicket().compareTo(amount) < 0) {
                result.setMsg(MsgConstants.OPT_FAIL);
                result.setDataMsg(ErrorCode.BALANCE_LESS_ERROR);
                return result;
            }
        }
        accountService.rechargeTicket(amount, account, adminId, minus);
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }
}
