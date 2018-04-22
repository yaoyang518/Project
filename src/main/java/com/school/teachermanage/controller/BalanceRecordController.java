package com.school.teachermanage.controller;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.service.BalanceRecordService;
import com.school.teachermanage.service.BitcoinRecordService;
import com.school.teachermanage.service.TicketRecordService;
import com.school.teachermanage.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 余额记录控制器
 *
 * @author zhangsl
 * @date 2017/11/20
 */
@RestController
@RequestMapping("/balanceRecordApi")
@Api(description = "余额接口")
public class BalanceRecordController {
    @Resource
    private UserService userService;
    @Resource
    private BalanceRecordService balanceRecordService;
    @Resource
    private BitcoinRecordService bitcoinRecordService;
    @Resource
    private TicketRecordService ticketRecordService;

    @GetMapping("/records")
    @ApiOperation(value = "获取用户余额记录", notes = "返回码: {'0000'：'操作成功','0010':'令牌错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "令牌", required = true, paramType = "header", dataType = "string"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "记录数", required = true, paramType = "query", dataType = "int", defaultValue = "10")
    })
    public DataResult getBalanceRecords(@RequestHeader String token,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        DataResult result = new DataResult();
        Long userId = userService.getUserId(token);
        return balanceRecordService.findRecordsByUserId(userId, page, size, result);
    }

    /**
     * 获取用户的报单积分列表
     */
    @GetMapping("/bitcoinRecords")
    @ApiOperation(value = "获取用户的报单积分列表", notes = "返回码: {'0000'：'操作成功','0014'：'日期格式错误','0010':'令牌错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "记录数", required = true, paramType = "query", dataType = "int", defaultValue = "10")
    })
    public DataResult findBitcoinRecordsByUserId(@RequestHeader String token,
                                                 @RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        DataResult result = new DataResult();
        Long userId = userService.getUserId(token);
        return bitcoinRecordService.findByUserId(userId, page, size, result);
    }

    /**
     * 获取用户的消费劵列表
     */
    @GetMapping("/ticketRecords")
    @ApiOperation(value = "获取用户的消费劵列表", notes = "返回码: {'0000'：'操作成功','0014'：'日期格式错误','0010':'令牌错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "记录数", required = true, paramType = "query", dataType = "int", defaultValue = "10")
    })
    public DataResult findTicketRecordsByUserId(@RequestHeader String token,
                                                @RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        DataResult result = new DataResult();
        Long userId = userService.getUserId(token);
        return ticketRecordService.findByUserId(userId, page, size, result);
    }

}
