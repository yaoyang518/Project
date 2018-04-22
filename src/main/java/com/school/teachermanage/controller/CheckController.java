package com.school.teachermanage.controller;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.entity.User;
import com.school.teachermanage.enumeration.ErrorCode;
import com.school.teachermanage.service.UserService;
import com.school.teachermanage.util.CommonUtil;
import com.school.teachermanage.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 校验控制类
 *
 * @author zhangsl
 * @date 2017-11-03
 */
@RestController
@RequestMapping("/check")
@Api(description = "校验接口")
public class CheckController {

    @Autowired
    private UserService userService;

    @Value("${kaptcha.key}")
    private String kaptchaKey;

    @GetMapping("/mobile/{mobile}")
    @ApiOperation(value = "校验手机号", notes = "返回码：0001:用户已存在;0005:手机号为空；0007:手机号格式错误；0012:用户不存在")
    @ApiImplicitParam(name = "mobile", value = "手机号", required = true, paramType = "path", dataType = "String")
    public DataResult checkMobile(@PathVariable String mobile) {
        DataResult result = new DataResult();
        if (StringUtil.isEmpty(mobile)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.MOBILE_NULL);
            return result;
        }

        if (!CommonUtil.isRightPhone(mobile)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.MOBILE_FORMAT_ERROR);
            return result;
        }
        User user = userService.findByMobile(mobile);
        result.setSuccess(user != null);
        result.setMsg(MsgConstants.QUERY_SUCCESS);
        if (user != null) {
            result.setDataMsg(ErrorCode.USER_EXIST);
        } else {
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
        }
        return result;
    }


    @GetMapping("/verifyCode/{code}")
    @ApiOperation(value = "校验验证码", notes = "'0000': '操作成功返回码','0004':'图片验证码错误")
    @ApiImplicitParam(name = "code", value = "验证码", required = true, paramType = "path", dataType = "String")
    public DataResult checkVerifyCode(HttpServletRequest request, @PathVariable String code) {
        DataResult result = new DataResult();
        String verifyCode = request.getSession().getAttribute(kaptchaKey).toString();
        if (!verifyCode.equalsIgnoreCase(code)){
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.VERIFY_CODE_ERROR);
            return result;
        }
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        return result;
    }

    /**
     *校验用户是否存在
     * @param userId
     * @return
     */
    @GetMapping("/user/{userId}")
    @ApiOperation(value = "校验用户是否存在", notes = "返回码: {'0000'：'操作成功','0001': '用户已存在','0012', '用户不存在'}")
    @ApiImplicitParam(name = "userId", value = "上级id", required = true, paramType = "path", dataType = "Long")
    public DataResult findUserByParentId(@PathVariable Long userId){
        DataResult result = new DataResult();
        User user = userService.findById(userId);
        if (user != null) {
            result.getData().put("username",user.getMobile());
            result.setDataMsg(ErrorCode.USER_EXIST);
        } else {
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
        }
        result.setMsg(MsgConstants.QUERY_SUCCESS);
        return result;
    }
}
