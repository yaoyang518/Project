package com.school.teachermanage.repository;

import com.school.teachermanage.entity.UserDetail;
import com.school.teachermanage.enumeration.UserLevel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author wudc
 * @Date 2017/11/17.
 */
@Repository
public interface UserDetailReposity extends PagingAndSortingRepository<UserDetail, Long> {
    /**
     * 查找用户userDetail
     *
     * @param UserId
     * @return
     */
    @Query("select userDetail from UserDetail userDetail where userDetail.user.id=:userId  ")
    UserDetail findByUserId(@Param("userId") Long UserId);

    /**
     * 根据用户id，查询下级具有指定等级资格数量
     * @param id
     * @param userLevel
     * @return
     */
    @Query("select count(userDetail) from UserDetail userDetail " +
            " where userDetail.user.parent.id = :id and userDetail.userLevel = :userLevel")
    int getQualificationCountWithUserLevel(@Param("id") Long id, @Param("userLevel") UserLevel userLevel);
}
