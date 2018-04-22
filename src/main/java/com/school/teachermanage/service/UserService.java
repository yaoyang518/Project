package com.school.teachermanage.service;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.constants.NumConstants;
import com.school.teachermanage.entity.*;
import com.school.teachermanage.enumeration.DateStyle;
import com.school.teachermanage.enumeration.ErrorCode;
import com.school.teachermanage.enumeration.UserLevel;
import com.school.teachermanage.repository.AdminRepository;
import com.school.teachermanage.repository.LevelUpRepository;
import com.school.teachermanage.repository.UserLevelRecordRepository;
import com.school.teachermanage.repository.UserRepository;
import com.school.teachermanage.util.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户数据
 *
 * @author zhangsl
 * @date 2017-11-03
 */
@Service
public class UserService {

    private Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LevelUpRepository levelUpRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private UserLevelRecordRepository userLevelRecordRepository;
    @Value("${jwt.secret.key}")
    private String secretKey;

    public Long getUserId(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return Long.parseLong(claims.get("id").toString());
    }

    public User findById(Long id) {
        return userRepository.findOne(id);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findByMobile(String mobile) {
        return userRepository.findByMobile(mobile);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public DataResult edit(User edit) {
        DataResult result = new DataResult();
        User user = userRepository.findOne(edit.getId());
        if (this.userExist(user, result)) {
            return result;
        }
        if (StringUtil.isNotEmpty(edit.getMobile()) && !edit.getMobile().equals(user.getMobile())) {
            if (!CommonUtil.isRightPhone(edit.getMobile())) {
                result.setMsg(MsgConstants.OPT_FAIL);
                result.setDataMsg(ErrorCode.MOBILE_FORMAT_ERROR);
                return result;
            }
            if (userRepository.findByMobile(edit.getMobile()) != null) {
                result.setMsg(MsgConstants.OPT_FAIL);
                result.setDataMsg(ErrorCode.USER_EXIST);
                return result;
            }
            user.setMobile(edit.getMobile());
        }

        if (StringUtil.isNotEmpty(edit.getUsername())) {
            user.setUsername(edit.getUsername());
        }

        if (StringUtil.isNotEmpty(edit.getPassword())) {
            if (!CommonUtil.isRightPassword(edit.getPassword())) {
                result.setMsg(MsgConstants.OPT_FAIL);
                result.setDataMsg(ErrorCode.PASSWORD_FORMAT_ERROR);
                return result;
            }
            user.setPassword(Md5Util.doubleMD5(edit.getPassword() + user.getSalt()));
        }
        userRepository.save(user);
        JSONObject data = result.getData();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    public DataResult findUsersByUsernameWithDate(String username, Date start, Date end, int page, int size, String mobile, DataResult result) {
        username = "%" + username + "%";
        mobile = "%" + mobile + "%";
        PageRequest pageRequest = QueryUtil.buildPageRequest(page, size, "id", true);
        Page<User> userPage = userRepository.findByUsernameWithDate(username, mobile, start, end, pageRequest);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        JSONObject data = result.getData();
        data.put("page", CommonUtil.generatePageJSON(userPage));
        List<User> users = userPage.getContent();
        JSONArray array = generateUserJsonArray(users);
        data.put("users", array);
        return result;
    }


    public DataResult userIsavailableOpt(Long id, Boolean flag, DataResult result) {
        User user = userRepository.findOne(id);
        if (this.userExist(user, result)) {
            return result;
        }
        if (flag) {
            user.setState((byte) 1);
        } else {
            user.setState((byte) 0);
        }
        userRepository.save(user);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        return result;
    }

    public List<User> findJuniorsWithoutUserLevel(Long id, UserLevel userLevel) {
        return userRepository.findJuniorsWithoutUserLevel(id, userLevel);
    }

    public DataResult findUserParents(Long id, DataResult result) {
        User user = userRepository.findOne(id);
        if (this.userExist(user, result)) {
            return result;
        }
        JSONObject data = result.getData();
        JSONArray array = new JSONArray();
        User parent = user.getParent();
        while (parent != null) {
            JSONObject json = new JSONObject();
            json.put("id", parent.getId());
            json.put("username", parent.getUsername());
            json.put("mobile", parent.getMobile());
            json.put("userLevel", parent.getUserLevel().getName());
            json.put("juniorCount", userRepository.getJuniorCount(parent.getId()));
            json.put("state", parent.getState());
            json.put("createDate", DateUtil.dateToString(parent.getCreateDate(), DateStyle.YYYY_MM_DD_HH_MM_SS));
            parent = parent.getParent();
            if (parent != null) {
                json.put("parentName", parent.getUsername());
            }
            array.add(json);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total", array.size());
        data.put("page", jsonObject);
        data.put("parents", array);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        return result;
    }

    public DataResult findUserJuniors(Long id, int page, int size, DataResult result) {
        User user = userRepository.findOne(id);
        if (this.userExist(user, result)) {
            return result;
        }
        PageRequest pageRequest = QueryUtil.buildPageRequest(page, size, "id", true);
        Page<User> userPage = userRepository.findJuniorUser(id, pageRequest);
        JSONObject data = result.getData();
        data.put("page", CommonUtil.generatePageJSON(userPage));
        List<User> users = userPage.getContent();
        JSONArray array = this.generateUserJsonArray(users);
        data.put("juniors", array);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        return result;
    }

    public DataResult findQualificationUsers(String username, String mobile, Date start, Date end, int page, int size, DataResult result) {
        username = "%" + username + "%";
        mobile = "%" + mobile + "%";
        PageRequest pageRequest = QueryUtil.buildPageRequest(page, size, "id", true);
        Page<User> userPage = userRepository.findQualificationUsers(username, mobile, start, end, pageRequest);
        JSONObject data = result.getData();
        data.put("page", CommonUtil.generatePageJSON(userPage));
        List<User> users = userPage.getContent();
        JSONArray array = new JSONArray();
        for (User user : users) {
            UserDetail userDetail = userDetailService.findByUser(user);
            JSONObject json = new JSONObject();
            json.put("id", user.getId());
            json.put("username", user.getUsername());
            json.put("mobile", user.getMobile());
            json.put("userLevel", user.getUserLevel().getName());
            json.put("createDate", DateUtil.dateToString(user.getCreateDate(), DateStyle.YYYY_MM_DD_HH_MM_SS));
            json.put("qualification", userDetail.getUserLevel().getName());
            array.add(json);
        }
        data.put("users", array);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        return result;
    }

    public DataResult changeParent(Long userId, Long parentId, Long adminId) {
        DataResult result = new DataResult();
        if (userId.equals(parentId)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_PARENT_SELF);
            return result;
        }
        User user = userRepository.findOne(userId);
        if (userExist(user, result)) {
            return result;
        }
        User changeParent = userRepository.findOne(parentId);
        if (userExist(changeParent, result)) {
            return result;
        }
        User parent = changeParent.getParent();
        while (parent != null) {
            Long id = parent.getId();
            if (id.equals(userId)) {
                result.setMsg(MsgConstants.OPT_FAIL);
                result.setDataMsg(ErrorCode.USER_PARENT_CICLE);
                return result;
            }
            parent = parent.getParent();
        }
        user.setParent(changeParent);
        userRepository.save(user);
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    private boolean userExist(User user, DataResult result) {
        if (user == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
            return true;
        }
        return false;
    }

    public DataResult exportExcel(String username, String mobile, Date start, Date end, DataResult result) throws IOException {
        username = "%" + username + "%";
        mobile = "%" + mobile + "%";
        int account = userRepository.getDownLoadUserAccount(username, mobile, start, end);
        if (account > NumConstants.DOWNLOAD_LIMIT) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.DOWNLOAD_OVERSTEP);
        } else {
            List<User> list = userRepository.findDownLoadUsers(username, mobile, start, end);
            JSONArray array = new JSONArray();
            for (User user : list) {
                JSONObject json = new JSONObject();
                json.put("id", user.getId());
                json.put("shopKeeper", user.getShopKeeper());
                json.put("username", user.getUsername());
                json.put("mobile", user.getMobile());
                json.put("userLevel", user.getUserLevel().getName());
                if (user.getParent() != null) {
                    json.put("parent", "[" + user.getParent().getId() + "]" + user.getParent().getUsername());
                } else {
                    json.put("parent", "无");
                }
                json.put("junior", userRepository.getJuniorCount(user.getId()) + "");
                json.put("createDate", DateUtil.dateToString(user.getCreateDate(), DateStyle.YYYY_MM_DD_HH_MM_SS));
                if (user.getState() == 0) {
                    json.put("state", "锁定");
                } else {
                    json.put("state", "正常");
                }
                array.add(json);
            }
            result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
            result.getData().put("users", array);
        }
        return result;
    }

    public DataResult userLevel(Long userId, UserLevel userLevel, Long adminId, BigDecimal amount, DataResult result) {
        Admin admin = adminRepository.findOne(adminId);
        User user = userRepository.findOne(userId);
        if (user == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
            return result;
        }
        if (userLevel != null && this.isValidateUserLevel(userLevel.getIndex(), result)) {
            return result;
        }
        boolean levelSix = userLevel.getIndex() == UserLevel.LEVEL_SIX.getIndex();
        LevelUp levelUp = levelUpRepository.findByAvailableAndUserLevel(Boolean.TRUE, userLevel);
        if (levelUp == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_UPLEVEL_NOT_EXIST);
            return result;
        }
        if (userLevel.getIndex() <= user.getUserLevel().getIndex()) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USERLEVEL_IS_ERROR);
            return result;
        }

        if (levelSix) {
            if (NumberUtil.isLess(amount, levelUp.getAmount())) {
                result.setMsg(MsgConstants.OPT_FAIL);
                result.setDataMsg(ErrorCode.AMOUNT_ERROR);
                return result;
            }
        }
        UserDetail userDetail = userDetailService.findByUser(user);
        userDetail.setNextLevel(userDetailService.getNextLevel(userLevel));
        userDetailService.save(userDetail);
        //保存升级记录
        UserLevelRecord userLevelRecord = new UserLevelRecord();
        userLevelRecord.setAdmin(admin);
        userLevelRecord.setUser(user);
        userLevelRecord.setChangeFrom(user.getUserLevel());
        userLevelRecord.setLevelUp(levelUp);
        userLevelRecord.setChangeTo(userLevel);
        userLevelRecord.setCreateDate(new Date());
        userLevelRecord.setRemark("用户升级为" + userLevel.getName());
        userLevelRecordRepository.save(userLevelRecord);
        user.setUserLevel(userLevel);
        userRepository.save(user);
        //级别调整为消费商以上，上级拿升级提成
        if (!levelSix) {
            amount = levelUp.getAmount();
        }
        accountService.recharge(amount, accountService.findByUser(user), adminId, false, true, levelSix);
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    public DataResult saveQualificationUserLevel(Long userId, Long adminId, DataResult result) {
        Admin admin = adminRepository.findOne(adminId);
        User user = userRepository.findOne(userId);
        if (user == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
            return result;
        }
        UserDetail userDetail = userDetailService.findByUser(user);
        UserLevel userLevel = userDetail.getUserLevel();
        if (userLevel == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USERLEVEL_IS_ERROR);
            return result;
        }
        LevelUp levelUp = levelUpRepository.findByAvailableAndUserLevel(Boolean.TRUE, userLevel);
        if (levelUp == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_UPLEVEL_NOT_EXIST);
            return result;
        }
        userDetail.setUserLevel(null);
        userDetail.setNextLevel(userDetailService.getNextLevel(userLevel));
        userDetailService.save(userDetail);

        //保存升级记录
        UserLevelRecord userLevelRecord = new UserLevelRecord();
        userLevelRecord.setAdmin(admin);
        userLevelRecord.setUser(user);
        userLevelRecord.setChangeFrom(user.getUserLevel());
        userLevelRecord.setChangeTo(userLevel);
        userLevelRecord.setCreateDate(new Date());
        userLevelRecord.setRemark("用户升级为" + userLevel.getName());
        userLevelRecord.setLevelUp(levelUp);
        userLevelRecordRepository.save(userLevelRecord);
        user.setUserLevel(userLevel);
        userRepository.save(user);
        //上级拿升级提成
        accountService.recharge(levelUp.getAmount(), accountService.findByUser(user), adminId, false, true, false);
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    public DataResult upShopKeeper(Long userId, Long adminId, DataResult result) {

        User user = userRepository.findOne(userId);
        if (user == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
            return result;
        }
        //获取规则
        LevelUp levelUp = levelUpRepository.findByAvailableAndUserLevel(Boolean.TRUE, UserLevel.LEVEL_ONE);
        if (levelUp == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_UPLEVEL_NOT_EXIST);
            return result;
        }
        if (user.getShopKeeper()) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_IS_SHOPKEEPER);
            return result;
        }
        Admin admin = null;
        if (adminId != null) {
            admin = adminRepository.findOne(adminId);
        }
        UserDetail userDetail = userDetailService.findByUser(user);
        userDetail.setShopKeeperDate(new Date());
        user.setShopKeeper(true);
        userDetailService.save(userDetail);
        userRepository.save(user);
        accountService.userLevelUpAccount(admin, user, levelUp);

        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    public DataResult upConsumerBusiness(Long userId, Long adminId, BigDecimal amount, DataResult result) {
        User user = userRepository.findOne(userId);
        if (user == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
            return result;
        }
        //获取规则
        LevelUp levelUp = levelUpRepository.findByAvailableAndUserLevel(Boolean.TRUE, UserLevel.LEVEL_SIX);
        if (levelUp == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_UPLEVEL_NOT_EXIST);
            return result;
        }
        if (user.getUserLevel().getIndex() >= UserLevel.LEVEL_SIX.getIndex()) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USERLEVEL_IS_ERROR);
            return result;
        }
        UserDetail userDetail = userDetailService.findByUser(user);
        userDetail.setNextLevel(UserLevel.LEVEL_ELEVEN);
        userDetailService.save(userDetail);
        user.setUserLevel(UserLevel.LEVEL_SIX);
        userRepository.save(user);
        accountService.recharge(amount, accountService.findByUser(user), adminId, false, true, false);
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    private boolean isValidateUserLevel(int level, DataResult result) {
        if (!UserLevel.isValidate(level)) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USERLEVEL_IS_ERROR);
            return true;
        }
        return false;
    }

    private JSONArray generateUserJsonArray(List<User> users) {
        JSONArray array = new JSONArray();
        for (User user : users) {
            JSONObject json = new JSONObject();
            json.put("id", user.getId());
            json.put("username", user.getUsername());
            json.put("mobile", user.getMobile());
            json.put("userLevel", user.getUserLevel().getName());
            json.put("shopKeeper", user.getShopKeeper());
            User parent = user.getParent();
            if (parent != null) {
                json.put("parentName", parent.getUsername());
                json.put("parentId", parent.getId());
            }
            json.put("juniorCount", userRepository.getJuniorCount(user.getId()));
            json.put("state", user.getState());
            json.put("createDate", DateUtil.dateToString(user.getCreateDate(), DateStyle.YYYY_MM_DD_HH_MM_SS));
            array.add(json);
        }
        return array;
    }

    public void checkUserDetailLevel(final User user) {
        logger.info("开始检查上级是否有代理商资格");
        UserDetail userDetail = userDetailService.findByUser(user);
        if (userDetail.getNextLevel() == null) {
            return;
        }
        int nextLevel = userDetail.getNextLevel().getIndex();
        if (userDetail.getUserLevel() != null) {
            int qualifyLevel = userDetail.getUserLevel().getIndex();
            if (qualifyLevel > nextLevel) {
                if (qualifyLevel == UserLevel.LEVEL_ELEVEN.getIndex()) {
                    nextLevel = UserLevel.LEVEL_SIXTEEN.getIndex();
                } else if (qualifyLevel == UserLevel.LEVEL_SIXTEEN.getIndex()) {
                    nextLevel = UserLevel.LEVEL_TWENTY_ONE.getIndex();
                } else if (qualifyLevel == UserLevel.LEVEL_TWENTY_ONE.getIndex()) {
                    nextLevel = UserLevel.LEVEL_TWENTY_SIX.getIndex();
                } else {
                    return;
                }
            }
        }
        if (nextLevel == UserLevel.LEVEL_ELEVEN.getIndex()) {
            //办事处  直推6个消费商，团队50消费商以上
            int directLimit = 6;
            int limit = 50;
            int count = userRepository.getJuniorCountWithUserLevel(user.getId(), UserLevel.LEVEL_SIX);
            if (count < directLimit) {
                return;
            }
            count = calculate(user, limit, UserLevel.LEVEL_SIX);
            if (count > limit) {
                userDetail.setUserLevel(UserLevel.LEVEL_ELEVEN);
                userDetail.setNextLevel(UserLevel.LEVEL_SIXTEEN);
                userDetailService.save(userDetail);
            }
        } else if (nextLevel == UserLevel.LEVEL_SIXTEEN.getIndex()) {
            //体验中心  直推3个办事处(有资格就行)， 团队200消费商以上
            int directLimit = 3;
            int limit = 200;
            int count = userRepository.getJuniorCountWithUserLevel(user.getId(), UserLevel.LEVEL_ELEVEN);
            count = count + userDetailService.getQualificationCountWithUserLevel(user.getId(), UserLevel.LEVEL_ELEVEN);
            if (count < directLimit) {
                return;
            }
            count = calculate(user, limit, UserLevel.LEVEL_SIX);
            if (count > limit) {
                userDetail.setUserLevel(UserLevel.LEVEL_SIXTEEN);
                userDetail.setNextLevel(UserLevel.LEVEL_TWENTY_ONE);
                userDetailService.save(userDetail);
            }
        } else if (nextLevel == UserLevel.LEVEL_TWENTY_ONE.getIndex()) {
            //商务中心   直推4个体验中心（有资格就行），团队600消费商以上
            int directLimit = 4;
            int limit = 600;
            int count = userRepository.getJuniorCountWithUserLevel(user.getId(), UserLevel.LEVEL_SIXTEEN);
            count = count + userDetailService.getQualificationCountWithUserLevel(user.getId(), UserLevel.LEVEL_SIXTEEN);
            if (count < directLimit) {
                return;
            }

            count = calculate(user, limit, UserLevel.LEVEL_SIX);
            if (count > limit) {
                userDetail.setUserLevel(UserLevel.LEVEL_TWENTY_ONE);
                userDetail.setNextLevel(UserLevel.LEVEL_TWENTY_SIX);
                userDetailService.save(userDetail);
            }
        } else if (nextLevel == UserLevel.LEVEL_TWENTY_SIX.getIndex()) {
            //城市运营商   直推4个商务中心（有资格就行）
            int directLimit = 4;
            int count = userRepository.getJuniorCountWithUserLevel(user.getId(), UserLevel.LEVEL_TWENTY_ONE);
            count = count + userDetailService.getQualificationCountWithUserLevel(user.getId(), UserLevel.LEVEL_TWENTY_ONE);
            if (count > directLimit) {
                userDetail.setUserLevel(UserLevel.LEVEL_TWENTY_SIX);
                userDetail.setNextLevel(null);
                userDetailService.save(userDetail);
            }
        }
        logger.info("检查上级是否有代理商资格结束");
    }


    private int calculate(User user, int limit, UserLevel userLevel) {
        int count = 0;
        List<User> users = userRepository.findJuniorUsers(user.getId());
        for (User temp : users) {
            logger.info("便利用户等级：" + temp.getUserLevel().getIndex());
            if (temp.getUserLevel().getIndex() >= userLevel.getIndex()) {
                count++;
            }
        }
        if (count <= limit) {
            for (User temp : users) {
                count = count + calculate(temp, limit, userLevel);
            }
        }
        return count;
    }

    public  ArrayList<User> buildTree(Long userId) {
        List<User> users = userRepository.findJuniorUsers(userId);
        ArrayList<User> list = new ArrayList<>();
        for (User user : users) {
            list.add(user);
                build(user,list);
        }
        return list;
    }

    private void build(User user, ArrayList<User> list) {
        List<User> children = getChildren(user);
        if (!children.isEmpty()) {
            for (User child : children) {
                list.add(child);
                build(child,list);
            }
        }
    }
    private List<User> getChildren(User node) {
        return userRepository.findJuniorUsers(node.getId());
    }

    public int findIsShopKeeperJuniors(Long id) {
        return userRepository.findIsShopKeeperJuniors(id);
    }

    public int findJuniorCountNotNormal(Long id) {
        return userRepository.findJuniorCountNotNormal(id);
    }

    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    public boolean isUserLocked(Long userId) {
        return findById(userId).getState() != 1;
    }
}
