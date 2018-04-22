package com.school.teachermanage.controller;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.entity.BankAccount;
import com.school.teachermanage.service.BankAccountService;
import com.school.teachermanage.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 银行账户控制器
 *
 * @author zhangsl
 * @date 2017/11/23
 */
@RestController
@RequestMapping("/bankAccountApi")
@Api(description = "银行账户接口")
public class BankAccountController {

    @Resource
    private UserService userService;
    @Resource
    private BankAccountService bankAccountService;

    @GetMapping("/bankAccounts")
    @ApiOperation(value = "查询用户的银行卡", notes = "返回码: {'0000'：'操作成功','0048':'用户未绑定银行卡','0012': '用户不存在'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "token", required = true, paramType = "heard", dataType = "String")
    })
    public DataResult list(@RequestHeader String token) {
        DataResult result = new DataResult();
        Long userId = userService.getUserId(token);
        return bankAccountService.findCardsByUserId(userId, result);
    }

    @GetMapping("/default")
    @ApiOperation(value = "根据id查询银行卡|或默认账户", notes = "返回码: {'0000'：'操作成功','0048':'用户未绑定银行卡'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bankAccountId", value = "Id", required = true, paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "token", value = "token", required = true, paramType = "heard", dataType = "String")
    })
    public DataResult findById(@RequestHeader String token,@RequestParam Long bankAccountId) {
        DataResult result = new DataResult();
        Long userId = userService.getUserId(token);
        return bankAccountService.findBankCardById(bankAccountId,userId, result);
    }


    @PostMapping("/add")
    @ApiOperation(value = "用户添加银行卡", notes = "返回码: {'0000'：'操作成功','0046':'银行卡有误',0047':'银行卡已添加','0012': '用户不存在','0048':'添加银行卡信息有误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "bankAccount", value = "银行卡信息Json", required = true, paramType = "body", dataType = "BankAccount")
    })
    public DataResult add(@RequestHeader String token, @RequestBody BankAccount bankAccount) {
        DataResult result = new DataResult();
        Long userId = userService.getUserId(token);
        return bankAccountService.save(bankAccount, userId, result);
    }


    @DeleteMapping("/delete")
    @ApiOperation(value = "删除用户银行卡", notes = "返回码: {'0000'：'操作成功','0046'：'银行卡有误','0010':'令牌错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bankAccountId", value = "Id", required = true, paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "token", value = "token", required = true, paramType = "heard", dataType = "String")
    })
    public DataResult deleteByUserId(@RequestHeader String token, @RequestParam Long bankAccountId) {
        DataResult result = new DataResult();
        Long userId = userService.getUserId(token);
        return bankAccountService.deleteByUserId(userId, bankAccountId, result);
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改用户的银行卡", notes = "返回码: {'0000'：'操作成功','0048':'用户未绑定银行卡'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bankAccount", value = "银行卡信息Json", required = true, paramType = "body", dataType = "BankAccount"),
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String")
    })
    public DataResult updateByUserId(@RequestHeader String token, @RequestBody BankAccount bankAccount) {
        DataResult result = new DataResult();
        Long userId = userService.getUserId(token);
        return bankAccountService.update(bankAccount,userId, result);
    }

    @GetMapping("/card")
    @ApiOperation(value = "根据id查询银行卡|或默认账户", notes = "返回码: {'0000'：'操作成功'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "请求TOKEN", required = true, paramType = "header", dataType = "String")
    })
    public DataResult findUserCard(@RequestHeader String token) {
        DataResult result = new DataResult();
        Long userId = userService.getUserId(token);
        return bankAccountService.findBankCardDefualt(userId,result);
    }
}
