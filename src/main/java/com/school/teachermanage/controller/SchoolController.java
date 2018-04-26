package com.school.teachermanage.controller;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.entity.School;
import com.school.teachermanage.enumeration.ErrorCode;
import com.school.teachermanage.service.SchoolService;
import com.school.teachermanage.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author yaoyang·
 * @Description:
 * @date 2018/4/25
 */
@RestController
@Api(description = "学校接口")
@RequestMapping("/schoolApi")
public class SchoolController {

    @Resource
    private SchoolService schoolService;

    @PostMapping("/createSchool")
    @ApiOperation(value="创建学校",notes="结构为{districtId:11,name=镇海中学}")
    @ApiImplicitParams({@ApiImplicitParam(name="token", value = "请求token",required = true,paramType = "header",dataType = "String"),
        @ApiImplicitParam(name="school",value = "学校的参数",required = true,paramType = "body",dataType = "JSONObject")})
    public DataResult createSchool(@RequestHeader String token , @RequestBody JSONObject school){
        DataResult result = new DataResult();
        return schoolService.createSchool(school,result);
    }

    @GetMapping("/listSchool")
    @ApiOperation(value="获取学校列表")
    @ApiImplicitParams(
            {@ApiImplicitParam(name="token",value="请求token",required = true,paramType = "Header",dataType = "String"),
            @ApiImplicitParam(name="page",value = "页码",required = true,paramType = "query",dataType = "int",defaultValue = "1"),
            @ApiImplicitParam(name="size",value = "显示数",required = true,paramType = "query",dataType = "int",defaultValue = "10")}
    )
    public DataResult listSchool(@RequestHeader String token,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size){
        DataResult result = new DataResult();
        return schoolService.getAllSchool(page,size,result);
    }
}
