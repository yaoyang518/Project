package com.school.teachermanage.controller;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.entity.AliPayAccount;
import com.school.teachermanage.service.AliPayAccountService;
import com.school.teachermanage.service.BankAccountService;
import com.school.teachermanage.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 支付宝账户控制器
 *
 * @author wudc
 * @date 2017/11/23
 */
@RestController
@RequestMapping("/aliPayAccountApi")
@Api(description = "支付宝账户接口")
public class AliPayAccountController {

    @Resource
    private UserService userService;
    @Resource
    private BankAccountService bankAccountService;
    @Resource
    private AliPayAccountService aliPayAccountService;


    @PostMapping("/save")
    @ApiOperation(value = "用户添加支付宝账户", notes = "返回码: {'0000'：'操作成功','0062':'添加支付宝信息有误','0063':'支付宝账户已添加','0064':'账户不能为空','0065':'账户名不能为空'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "aliPayAccount", value = "支付宝信息Json", required = true, paramType = "body", dataType = "AliPayAccount")
    })
    public DataResult add(@RequestHeader String token, @RequestBody AliPayAccount aliPayAccount) {
        DataResult result = new DataResult();
        Long userId = userService.getUserId(token);
        return aliPayAccountService.save(aliPayAccount, userId, result);
    }

    @GetMapping("/aliPayAccounts")
    @ApiOperation(value = "查询用户的支付宝账户", notes = "返回码: {'0000'：'操作成功','0012': '用户不存在'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String")
    })
    public DataResult list(@RequestHeader String token) {
        DataResult result = new DataResult();
        Long userId = userService.getUserId(token);
        return aliPayAccountService.findCardsByUserId(userId, result);
    }

    @GetMapping("/default")
    @ApiOperation(value = "根据id查询支付宝账户|或默认账户", notes = "返回码: {'0000'：'操作成功','0048':'用户未绑定银行卡'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", required = true, paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String")
    })
    public DataResult findById(@RequestHeader String token, @RequestParam Long id) {
        DataResult result = new DataResult();
        Long userId = userService.getUserId(token);
        return aliPayAccountService.findById(id, userId, result);
    }


    @DeleteMapping("/delete")
    @ApiOperation(value = "删除用户支付宝账户", notes = "返回码: {'0000'：'操作成功','0046'：'银行卡有误','0010':'令牌错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", required = true, paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String")
    })
    public DataResult deleteByUserId(@RequestHeader String token, @RequestParam Long id) {
        DataResult result = new DataResult();
        Long userId = userService.getUserId(token);
        return aliPayAccountService.deleteByUserId(userId, id, result);
    }

}
