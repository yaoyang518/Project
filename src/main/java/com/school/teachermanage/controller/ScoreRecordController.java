package com.school.teachermanage.controller;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.constants.NumConstants;
import com.school.teachermanage.entity.ScoreRecord;
import com.school.teachermanage.enumeration.DateStyle;
import com.school.teachermanage.service.ScoreRecordService;
import com.school.teachermanage.service.UserService;
import com.school.teachermanage.util.CommonUtil;
import com.school.teachermanage.util.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 积分记录
 *
 * @author zhangsl
 * @date 2017/11/16
 */
@RestController
@RequestMapping("/scoreRecordApi")
@Api(description = "积分接口")
public class ScoreRecordController {

    @Resource
    private UserService userService;
    @Resource
    private ScoreRecordService scoreRecordService;


    @GetMapping("/user")
    @ApiOperation(value = "获取用户积分记录", notes = "返回码: {'0000'：'操作成功'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "令牌", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "记录数", required = true, paramType = "query", dataType = "int", defaultValue = "15")
    })
    public DataResult getUserScoreRecords(@RequestHeader String token,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "15") int size) {
        Long userId = userService.getUserId(token);
        Page<ScoreRecord> recordPage = scoreRecordService.findByUserId(userId, page, size);
        JSONArray records = new JSONArray();
        for (ScoreRecord scoreRecord : recordPage.getContent()) {
            JSONObject json = new JSONObject();
            json.put("name", scoreRecord.getScoreSource().getName());
            json.put("createDate", DateUtil.dateToString(scoreRecord.getCreateDate(), DateStyle.YYYY_MM_DD_HH_MM_SS));
            json.put("minus", scoreRecord.getMinus());
            json.put("amount", scoreRecord.getAmount().setScale(NumConstants.TWO, BigDecimal.ROUND_HALF_UP));
            json.put("frozen", scoreRecord.getFrozen().setScale(NumConstants.TWO, BigDecimal.ROUND_HALF_UP));
            json.put("score", scoreRecord.getScore().setScale(NumConstants.TWO, BigDecimal.ROUND_HALF_UP));
            records.add(json);
        }
        DataResult result = new DataResult();
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        JSONObject data = result.getData();
        data.put("records", records);
        data.put("page", CommonUtil.generatePageJSON(recordPage));
        return result;
    }

    /**
     * 获取用户兑换积分记录
     */
    @GetMapping("/score")
    @ApiOperation(value = "获取用户兑换积分记录", notes = "返回码: {'0000'：'操作成功','0010':'令牌错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "令牌", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "记录数", required = true, paramType = "query", dataType = "int", defaultValue = "10")
    })
    public DataResult findScoreRecords(@RequestHeader String token,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        DataResult result = new DataResult();
        Long userId = userService.getUserId(token);
        return scoreRecordService.findScoreRecordByUserIdAndType(userId, page, size, false, result);
    }

    /**
     * 获取用户冻结积分记录
     */
    @GetMapping("/frozen")
    @ApiOperation(value = "获取用户冻结积分记录", notes = "返回码: {'0000'：'操作成功','0010':'令牌错误'}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "令牌", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "记录数", required = true, paramType = "query", dataType = "int", defaultValue = "10")
    })
    public DataResult getFrozenedRecords(@RequestHeader String token,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        DataResult result = new DataResult();
        Long userId = userService.getUserId(token);
        return scoreRecordService.findScoreRecordByUserIdAndType(userId, page, size, true, result);
    }

}
