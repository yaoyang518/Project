package com.school.teachermanage.controller;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.repository.ConvertLevelRepository;
import com.school.teachermanage.repository.UserRepository;
import com.school.teachermanage.service.AccountService;
import com.school.teachermanage.service.UserService;
import io.swagger.annotations.Api;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 测试控制器
 * @author zhangsl
 * @date 2017-11-01
 */
@RestController
@Api(description = "测试接口")
public class TestController {

    private Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private AccountService accountService;
    @Value("${jwt.secret.key}")
    private String secretKey;
    @Resource
    private ConvertLevelRepository convertLevelRepository;

    @Resource
    private UserRepository userRepository;
    @Resource
    private UserService userService;

    @GetMapping(value = "test")
    public DataResult hello(BigDecimal amount) {
        logger.debug("This is a debug message");
        logger.info("This is an info message");
        logger.warn("This is a warn message");
        logger.error("This is an error message");
        DataResult result = new DataResult();
        result.setSuccessMsg("测试成功");
        JSONObject data = new JSONObject();
        data.put("id", 1);
        data.put("name", "zhang");
        return result;
    }


}
