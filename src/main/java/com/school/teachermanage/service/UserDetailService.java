package com.school.teachermanage.service;

import com.school.teachermanage.entity.User;
import com.school.teachermanage.entity.UserDetail;
import com.school.teachermanage.enumeration.UserLevel;
import com.school.teachermanage.repository.UserDetailReposity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户详情服务类
 *
 * @author wudc
 * @Date 2017/11/17.
 */
@Service
public class UserDetailService {
    @Resource
    private UserDetailReposity userDetailReposity;

    public UserDetail findByUser(User user) {
        UserDetail userDetail = userDetailReposity.findByUserId(user.getId());
        if (userDetail == null) {
            userDetail = new UserDetail();
            userDetail.setUser(user);
            userDetail.setNextLevel(getNextLevel(user.getUserLevel()));
            userDetailReposity.save(userDetail);
        }
        return userDetail;
    }

    public UserDetail save(UserDetail userDetail) {
        return userDetailReposity.save(userDetail);
    }

    public int getQualificationCountWithUserLevel(Long id, UserLevel userLevel) {
        return userDetailReposity.getQualificationCountWithUserLevel(id, userLevel);
    }

    public UserLevel getNextLevel(UserLevel userLevel) {
        int level = userLevel.getIndex();
        if (level < UserLevel.LEVEL_SIX.getIndex()) {
            return UserLevel.LEVEL_SIX;
        } else if (level < UserLevel.LEVEL_ELEVEN.getIndex()) {
            return UserLevel.LEVEL_ELEVEN;
        } else if (level < UserLevel.LEVEL_SIXTEEN.getIndex()) {
            return UserLevel.LEVEL_SIXTEEN;
        } else if (level < UserLevel.LEVEL_TWENTY_ONE.getIndex()) {
            return UserLevel.LEVEL_TWENTY_ONE;
        } else if (level < UserLevel.LEVEL_TWENTY_SIX.getIndex()) {
            return UserLevel.LEVEL_TWENTY_SIX;
        } else {
            return null;
        }
    }
}
