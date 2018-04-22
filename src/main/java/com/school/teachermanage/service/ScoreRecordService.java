package com.school.teachermanage.service;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.constants.NumConstants;
import com.school.teachermanage.entity.Account;
import com.school.teachermanage.entity.ScoreRecord;
import com.school.teachermanage.entity.User;
import com.school.teachermanage.enumeration.DateStyle;
import com.school.teachermanage.enumeration.ErrorCode;
import com.school.teachermanage.repository.AccountReposity;
import com.school.teachermanage.repository.ScoreRecordRepository;
import com.school.teachermanage.repository.UserRepository;
import com.school.teachermanage.util.CommonUtil;
import com.school.teachermanage.util.DateUtil;
import com.school.teachermanage.util.QueryUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 积分记录
 *
 * @author wudc
 * @date 2017-11-06
 */
@Service
public class ScoreRecordService {

    @Autowired
    private ScoreRecordRepository scoreRecordRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountReposity accountReposity;

    public ScoreRecord save(ScoreRecord scoreRecord) {
        return scoreRecordRepository.save(scoreRecord);
    }

    public Page<ScoreRecord> findByUserId(Long userId, int page, int size) {
        PageRequest pageRequest = QueryUtil.buildPageRequest(page, size, "createDate", true);
        return scoreRecordRepository.findByUserId(userId, pageRequest);
    }

    public DataResult findScoreRecordsByDate(Date start, Date end, int page, int size, DataResult result) {
        PageRequest pageRequest = QueryUtil.buildPageRequest(page, size, "id", true);
        Page<ScoreRecord> scoreRecordPage = scoreRecordRepository.findScoreRecordsByDate(start, end, pageRequest);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        JSONObject data = result.getData();
        List<ScoreRecord> scoreRecords = scoreRecordPage.getContent();
        BigDecimal frozenScore = accountReposity.getSumFrozenScore();
        if (frozenScore == null) {
            frozenScore = BigDecimal.ZERO;
        }
        frozenScore = frozenScore.setScale(NumConstants.TWO, BigDecimal.ROUND_FLOOR);
        BigDecimal score = accountReposity.getSumScore();
        if (score == null) {
            score = BigDecimal.ZERO;
        }
        score = score.setScale(NumConstants.TWO, BigDecimal.ROUND_FLOOR);
        JSONArray array = generateScoreRecordJsonArray(scoreRecords);
        data.put("scoreRecords", array);
        data.put("frozenScore", frozenScore);
        data.put("score", score);
        data.put("totalScore", frozenScore.add(score).setScale(NumConstants.TWO, BigDecimal.ROUND_FLOOR));
        data.put("page", CommonUtil.generatePageJSON(scoreRecordPage));
        return result;
    }

    public DataResult findScoreRecordByUserIdAndType(Long userId, int page, int size, boolean frozened, DataResult result) {
        User user = userRepository.findOne(userId);
        if (user == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
            return result;
        }
        Account account = accountReposity.findByUser(user);
        PageRequest pageRequest = QueryUtil.buildPageRequest(page, size, "id", true);
        Page<ScoreRecord> scoreRecordPage = scoreRecordRepository.findScoreRecordByUserIdAndType(userId, frozened, pageRequest);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        JSONObject data = result.getData();
        List<ScoreRecord> scoreRecords = scoreRecordPage.getContent();
        JSONArray array = generateScoreRecordJsonArray(scoreRecords);
        data.put("scoreRecords", array);
        if (frozened){
            data.put("frozenScore", account.getFrozenScore().setScale(NumConstants.TWO, BigDecimal.ROUND_HALF_UP));
        }else{
            data.put("score", account.getScore().setScale(NumConstants.TWO, BigDecimal.ROUND_HALF_UP));
        }
        data.put("page", CommonUtil.generatePageJSON(scoreRecordPage));
        return result;
    }

    private JSONArray generateScoreRecordJsonArray(List<ScoreRecord> scoreRecords) {
        JSONArray array = new JSONArray();
        for (ScoreRecord scoreRecord : scoreRecords) {
            JSONObject json = new JSONObject();
            json.put("id", scoreRecord.getId());
            json.put("username", scoreRecord.getUser().getUsername());
            json.put("userId", scoreRecord.getUser().getId());
            json.put("minus", scoreRecord.getMinus());
            json.put("amount", scoreRecord.getAmount().setScale(NumConstants.TWO, BigDecimal.ROUND_FLOOR));
            json.put("frozen", scoreRecord.getFrozen().setScale(NumConstants.TWO, BigDecimal.ROUND_FLOOR));
            json.put("score", scoreRecord.getScore().setScale(NumConstants.TWO, BigDecimal.ROUND_FLOOR));
            json.put("total", scoreRecord.getTotal().setScale(NumConstants.TWO, BigDecimal.ROUND_FLOOR));
            json.put("remark", scoreRecord.getRemark());
            json.put("scoreSource", scoreRecord.getScoreSource().getName());
            json.put("createDate", DateUtil.dateToString(scoreRecord.getCreateDate(), DateStyle.YYYY_MM_DD_HH_MM_SS));
            array.add(json);
        }
        return array;
    }
}
