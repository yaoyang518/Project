package com.school.teachermanage.controller;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.constants.NumConstants;
import com.school.teachermanage.entity.Admin;
import com.school.teachermanage.entity.User;
import com.school.teachermanage.enumeration.ErrorCode;
import com.school.teachermanage.service.AdminService;
import com.school.teachermanage.service.UserService;
import com.school.teachermanage.util.CommonUtil;
import com.school.teachermanage.util.Md5Util;
import com.school.teachermanage.util.StringUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 登录控制器
 *
 * @author zhangsl
 * @date 2017-11-03
 */
@RestController
@RequestMapping("/login")
@Api(description = "登录接口")
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private AdminService adminService;
    @Value("${jwt.secret.key}")
    private String secretKey;

    /**
     * 用户登录
     *
     * @param login
     * @return 数据
     */
    @PostMapping(value = "/user")
    @ApiOperation(value = "用户登录", notes = "返回码: {'0000': '操作成功','0005': '手机号为空','0007': '手机号格式错误','0006': '密码为空'," +
            "'0002': '用户未注册','0009': '密码错误'}")
    @ApiImplicitParam(name = "login", value = "请求参数", required = true, paramType = "body", dataType = "User")
    public DataResult userLogin(@RequestBody User login) {
        String mobile = login.getMobile();
        String password = login.getPassword();
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

        if (StringUtil.isEmpty(password)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.PASSWORD_NULL);
            return result;
        }

        User user = userService.findByMobile(mobile);
        if (user == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_UNREGISTER);
            return result;
        }

        if (user.getState() == NumConstants.ZERO) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_IS_LOCK);
            return result;
        }

        password = Md5Util.doubleMD5(password + user.getSalt());
        String pwd = user.getPassword();

        if (!password.equals(pwd)) {
            result.setDataMsg(ErrorCode.PASSWORD_ERROR);
            result.setMsg(MsgConstants.OPT_FAIL);
            return result;
        }

        String jwtToken = Jwts.builder()
                .setSubject("user")
                .claim("id", user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new DateTime().plusHours(8).toDate())
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        JSONObject data = result.getData();
        data.put("token", jwtToken);
        return result;
    }

    /**
     * 管理员登录
     *
     * @param login
     * @return 数据
     */
    @PostMapping(value = "/admin")
    @ApiOperation(value = "管理员登录", notes = "返回码: {'0000': '操作成功','0011: '登录名称为空','0006: '密码为空','0012': '用户不存在','0009': '密码错误'}")
    @ApiImplicitParam(name = "login", value = "请求参数", required = true, paramType = "body", dataType = "Admin")
    public DataResult adminLogin(@RequestBody Admin login) {
        String username = login.getUsername();
        String password = login.getPassword();
        DataResult result = new DataResult();
        if (StringUtil.isEmpty(username)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.LOGIN_NAME_NULL);
            return result;
        }

        if (StringUtil.isEmpty(password)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.PASSWORD_NULL);
            return result;
        }

        Admin admin = adminService.findByUsername(username);
        if (admin == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
            return result;
        }
        if (admin.getState() != NumConstants.ONE) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.ADMIN_LOCK);
            return result;
        }
        password = Md5Util.doubleMD5(password + admin.getSalt());
        String pwd = admin.getPassword();

        if (!password.equals(pwd)) {
            result.setDataMsg(ErrorCode.PASSWORD_ERROR);
            result.setMsg(MsgConstants.OPT_FAIL);
            return result;
        }

        String jwtToken = Jwts.builder()
                .setSubject("admin")
                .claim("id", admin.getId())
                .setIssuedAt(new Date())
                .setExpiration(new DateTime().plusHours(8).toDate())
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        JSONObject data = result.getData();
        data.put("token", jwtToken);
        return result;
    }

    public static void main(String[] args) {
        System.out.println(Md5Util.doubleMD5("111111" + "1BmwJAA99GE8NCluFxm7OIrb5IWLcXTD"));
    }

}
