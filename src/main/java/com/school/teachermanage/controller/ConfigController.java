package com.school.teachermanage.controller;

import com.school.teachermanage.annotations.Access;
import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.entity.*;
import com.school.teachermanage.enumeration.*;
import com.school.teachermanage.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 系统配置管理
 *
 * @author zhangsl
 * @date 2017-11-14
 */

@RestController
@RequestMapping("/config")
@Api(description = "系统配置接口")
public class ConfigController {

    @Resource
    private LevelUpService levelUpService;
    @Resource
    private AdminService adminService;
    @Resource
    private ConvertLevelService convertLevelService;
    @Resource
    private ConvertPercentService convertPercentService;
    @Resource
    private PayoutConfigService payoutConfigService;
    @Resource
    private ReplenishPercentService replenishPercentService;
    @Resource
    private TicketPercentService ticketPercentService;

    @GetMapping("/level/up")
    public DataResult getLevelUp() {
        DataResult result = new DataResult();
        return levelUpService.findEnable(result);
    }

    /**
     * 用户等级定义
     */
    @Access(authorities = PermissionEnum.USER_LEVEL_UP)
    @ApiOperation(value = "用户等级定义", notes = "返回码: {'0000'：'操作成功',0012': '用户不存在','0035': '金额错误',0036': '用户等级错误','0010':'令牌错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "levelUp", value = "请求对象", required = true, paramType = "body", dataType = "LevelUp"),
            @ApiImplicitParam(name = "index", value = "等级下标", required = true, paramType = "path", dataType = "int")
    })
    @PostMapping("/level/up/{index}")
    public DataResult save(@RequestHeader String token, @RequestBody LevelUp levelUp, @PathVariable("index") int index) {
        DataResult result = new DataResult();
        Long adminId = adminService.getAdminId(token);
        return levelUpService.save(levelUp, adminId, index, result);
    }

    /**
     * 获取规则
     */
    @GetMapping("/level")
    @ApiOperation(value = "获取当前规则", notes = "返回码: {'0000', '操作成功'}")
    @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String")
    public DataResult findEnableConvertLevel(@RequestHeader String token) {
        return convertLevelService.findEnable();
    }

    /**
     * 获取可用的转换比率
     */
    @GetMapping("/percent")
    @ApiOperation(value = "获取当前转换比率", notes = "返回码: {'0000', '操作成功','0020':'转化率未配置'}")
    @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String")
    public DataResult findEnableConvertPercent(@RequestHeader String token) {
        DataResult result = new DataResult();
        ConvertPercent convertPercent = convertPercentService.findEnable();
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        if (null == convertPercent) {
            result.getData().put("percent", ErrorCode.CONVERT_PERCENT_NULL.getName());
        } else {
            result.getData().put("percent", convertPercent.getPercent());
        }
        result.getData().put("percents", ConvertPercentEnum.getJsonArray());
        return result;
    }


    /**
     * 编辑转换比率
     */
    @Access(authorities = PermissionEnum.PERCENT_EDIT)
    @PostMapping("/percent/save")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "percent", value = "转换率", required = true, paramType = "query", dataType = "int")
    })
    @ApiOperation(value = "编辑当前转换比率", notes = "返回码: {'0000', '操作成功','0016':'比率值错误','0010':'令牌错误'}")
    public DataResult editConvertPercent(@RequestHeader String token, int percent) {
        DataResult result = new DataResult();
        Long adminId = adminService.getAdminId(token);
        return convertPercentService.save(percent, adminId, result);
    }

    /**
     * 编辑补货率
     */
    @Access(authorities = PermissionEnum.REPLENISH_PERCENT_EDIT)
    @PostMapping("/replenishPercent/save")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "percent", value = "转换率", required = true, paramType = "query", dataType = "int")
    })
    @ApiOperation(value = "编辑补货率", notes = "返回码: {'0000', '操作成功','0016':'比率值错误','0010':'令牌错误'}")
    public DataResult editReplenishPercent(@RequestHeader String token, int percent) {
        DataResult result = new DataResult();
        Long adminId = adminService.getAdminId(token);
        return replenishPercentService.save(percent, adminId, result);
    }

    /**
     * 获取可用补货率
     */
    @GetMapping("/replenishPercent")
    @ApiOperation(value = "获取可用补货率", notes = "返回码: {'0000', '操作成功','0020':'转化率未配置'}")
    @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String")
    public DataResult findEnableReplenishPercent(@RequestHeader String token) {
        DataResult result = new DataResult();
        ReplenishPercent replenishPercent = replenishPercentService.findEnable();
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        if (null == replenishPercent) {
            result.getData().put("replenishPercen", ErrorCode.REPLENISHPERCENT_NULL.getName());
        } else {
            result.getData().put("replenishPercen", replenishPercent.getPercent());
        }
        result.getData().put("replenishPercens", ReplenishPercentEnum.getJsonArray());
        return result;
    }

    /**
     * 编辑消费劵
     */
    @Access(authorities = PermissionEnum.REPLENISH_PERCENT_EDIT)
    @PostMapping("/ticketPercent/save")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "percent", value = "转换率", required = true, paramType = "query", dataType = "int")
    })
    @ApiOperation(value = "编辑消费劵", notes = "返回码: {'0000', '操作成功','0055':'比率值错误','0010':'令牌错误'}")
    public DataResult editTicketPercent(@RequestHeader String token, int percent) {
        DataResult result = new DataResult();
        Long adminId = adminService.getAdminId(token);
        return ticketPercentService.save(percent, adminId, result);
    }


    /**
     * 获取可用消费劵
     */
    @GetMapping("/ticketPercent")
    @ApiOperation(value = "获取可用消费劵", notes = "返回码: {'0000', '操作成功','0056':'转化率未配置','0010':'令牌错误'}")
    @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String")
    public DataResult findEnableTicketPercent(@RequestHeader String token) {
        DataResult result = new DataResult();
        TicketPercent ticketPercent = ticketPercentService.findEnable();
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        if (null == ticketPercent) {
            result.getData().put("ticketPercent", ErrorCode.TICKETPERCENT_NULL.getName());
        } else {
            result.getData().put("ticketPercent", ticketPercent.getPercent());
        }
        result.getData().put("ticketPercents", TicketPercentEnum.getJsonArray());
        return result;
    }

    /**
     * 编辑倍率
     */
    @Access(authorities = PermissionEnum.LEVEL_EDIT)
    @PutMapping("/level/save")
    @ApiOperation(value = "获取编辑规则", notes = "返回码: {'0000', '操作成功','0017':'倍数错误','0010':'令牌错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "convertLevel", value = "请求对象", required = true, paramType = "body", dataType = "ConvertLevel")
    })
    public DataResult editConvertLevel(@RequestHeader String token, @RequestBody ConvertLevel convertLevel) {
        DataResult result = new DataResult();
        Long adminId = adminService.getAdminId(token);
        return convertLevelService.save(convertLevel, adminId, result);
    }

    /**
     * 提现配置
     */
    @PostMapping("/payoutConfig/save")
    @ApiOperation(value = "提现", notes = "返回码: {'0000', '操作成功','0043','提现金额配置有误','0010':'令牌错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "amount", value = "请求对象", required = true, paramType = "query", dataType = "BigDecimal")
    })
    public DataResult savePayoutConfig(@RequestHeader String token, @RequestParam BigDecimal amount) {
        DataResult result = new DataResult();
        Long adminId = adminService.getAdminId(token);
        return payoutConfigService.save(adminId, amount, result);
    }

    /**
     * 获取提现可用配置
     */
    @GetMapping("/payoutConfig")
    @ApiOperation(value = "获取提现可用配置", notes = "返回码: {'0000', '操作成功','0010':'令牌错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String")
    })
    public DataResult getPayoutConfigEnable(@RequestHeader String token) {
        DataResult result = new DataResult();
        return payoutConfigService.getPayoutConfigEnable(result);
    }

}
