package com.school.teachermanage.controller;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.entity.User;
import com.school.teachermanage.service.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * 避免冲突controller
 *
 * @author isWeedend
 * @date 2017-11-06
 */
@RestController
@RequestMapping(value = "/wu")
public class WuDcController {
    @Autowired
    private UserService userService;
    @Resource
    private RoleService roleService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private ConvertPercentService convertPercentService;
    @Resource
    private ScoreRecordService scoreRecordService;
    @Resource
    private RechargeRecordService rechargeRecordService;
    @Resource
    private PayoutRecordService payoutRecordService;
    @Resource
    private AdminService adminService;
    @Resource
    private BankAccountService bankAccountService;
    @Resource
    private UserLevelRecordService userLevelRecordService;
    @GetMapping("/user")
    public DataResult findUserByParentId(){
        DataResult result = new DataResult();
        ArrayList<User> users = userService.buildTree(33L);
        JSONObject data = result.getData();
        JSONArray array = new JSONArray();
        for (User user : users) {
            JSONObject json = new JSONObject();
            json.put("id",user.getId());
            array.add(json);
        }
        data.put("ides",array);
        return result;
    }

}
