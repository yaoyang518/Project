package com.school.teachermanage.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.entity.Account;
import com.school.teachermanage.entity.User;
import com.school.teachermanage.enumeration.ErrorCode;
import com.school.teachermanage.service.AccountService;
import com.school.teachermanage.service.ScoreRecordService;
import com.school.teachermanage.service.UserService;
import com.school.teachermanage.util.CommonUtil;
import com.school.teachermanage.util.Md5Util;
import com.school.teachermanage.util.StringUtil;
import com.school.teachermanage.util.UUIDUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Date;

/**
 * 注册控制器
 *
 * @author zhangsl
 * @date 2017-11-03
 */
@RestController
@RequestMapping("/reg")
@Api(description = "注册接口")
public class RegController {
    @Autowired
    private DefaultKaptcha defaultKaptcha;
    @Autowired
    private UserService userService;
    @Resource
    private AccountService accountService;
    @Autowired
    private ScoreRecordService scoreRecordService;
    @Value("${jwt.secret.key}")
    private String secretKey;
    @Value("${kaptcha.key}")
    private String kaptchaKey;

    @PostMapping(value = "/{code}")
    @ApiOperation(value = "用户注册", notes = "返回码: {'0000': '操作成功','0004: '图片验证码错误','0005': '手机号为空','0007': '手机号格式错误'," +
            "'0006': '密码为空','0013': '密码格式错误','0008': '上级不存在','0001': '用户已存在'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "验证码", required = true, paramType = "path", dataType = "String"),
            @ApiImplicitParam(name = "reg", value = "注册参数", required = true, paramType = "body", dataType = "User")
    })
    public DataResult registerUser(HttpServletRequest request, @PathVariable String code, @RequestBody User reg) {

        DataResult result = new DataResult();
        String verifyCode = StringUtil.toStringNotNull(request.getSession().getAttribute(kaptchaKey));

        if (!verifyCode.equalsIgnoreCase(code)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.VERIFY_CODE_ERROR);
            return result;
        }

        if (StringUtil.isEmpty(reg.getMobile())) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.MOBILE_NULL);
            return result;
        }

        if (!CommonUtil.isRightPhone(reg.getMobile())) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.MOBILE_FORMAT_ERROR);
            return result;
        }

        if (StringUtil.isEmpty(reg.getPassword())) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.PASSWORD_NULL);
            return result;
        }

        if (!CommonUtil.isRightPassword(reg.getPassword())) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.PASSWORD_FORMAT_ERROR);
            return result;
        }


        if (reg.getParent() != null) {
            User parent = userService.findById(reg.getParent().getId());
            if (parent == null) {
                result.setMsg(MsgConstants.OPT_FAIL);
                result.setDataMsg(ErrorCode.UP_USER_NOT_EXIST);
                return result;
            } else {
                reg.setParent(parent);
            }
        }

        User user = userService.findByMobile(reg.getMobile());
        if (user != null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_EXIST);
            return result;
        }

        reg.setId(null);
        if (StringUtil.isEmpty(reg.getUsername())) {
            reg.setUsername(reg.getMobile());
        }
        reg.setCreateDate(new Date());
        reg.setUpdateDate(new Date());
        reg.setSalt(UUIDUtil.createRandomString(32));
        reg.setState((byte) 1);
        reg.setPassword(Md5Util.doubleMD5(reg.getPassword() + reg.getSalt()));
        reg.setShopKeeper(false);
        userService.save(reg);
        Account account = accountService.regScore(reg);
        reg.setAccount(account);
        userService.save(reg);
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }


    @GetMapping("/kaptcha")
    @ApiOperation(value = "生成验证码")
    public void defaultKaptcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        byte[] captchaChallengeAsJpeg = null;
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        try {
            //生产验证码字符串并保存到session中
            String verifyCode = defaultKaptcha.createText();
            request.getSession().setAttribute(kaptchaKey, verifyCode);
            //使用生产的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
            BufferedImage challenge = defaultKaptcha.createImage(verifyCode);
            ImageIO.write(challenge, "jpg", jpegOutputStream);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        //定义response输出类型为image/jpeg类型，使用response输出流输出图片的byte数组
        captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream = response.getOutputStream();
        responseOutputStream.write(captchaChallengeAsJpeg);
        responseOutputStream.flush();
        responseOutputStream.close();
    }
}
