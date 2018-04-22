package com.school.teachermanage.repository;

import com.school.teachermanage.entity.User;
import com.school.teachermanage.enumeration.UserLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 用户数据
 *
 * @author zhangsl
 * @date 2017-11-03
 */

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    /**
     * 通过username查找用户信息;
     *
     * @param username
     * @return
     */
    User findByUsername(String username);

    /**
     * mobile;
     *
     * @param mobile
     * @return
     */
    User findByMobile(String mobile);

    /**
     * 查询用户下级个数
     *
     * @param userId
     * @return
     */
    @Query("SELECT count(user) from User user  where user.parent.id =:userId ")
    int getJuniorCount(@Param("userId") Long userId);

    /**
     * 查询用户下级指定级别个数
     *
     * @param userId
     * @param userLevel
     * @return
     */
    @Query("SELECT count(user) from User user  where user.parent.id =:userId and user.userLevel = :userLevel")
    int getJuniorCountWithUserLevel(@Param("userId") Long userId, @Param("userLevel") UserLevel userLevel);

    /**
     * 查询用户下级非普通用户个数
     *
     * @param userId
     * @return
     */
    @Query("SELECT count(user) from User user  where user.parent.id =:userId and user.userLevel <> 'USER_NORMAL' ")
    int findJuniorCountNotNormal(@Param("userId") Long userId);

    /**
     * 查询用户直接下级，非指定级别
     *
     * @param userId
     * @param userLevel
     * @return
     */
    @Query("SELECT user from User user  where user.parent.id =:userId and user.userLevel <> :userLevel ")
    List<User> findJuniorsWithoutUserLevel(@Param("userId") Long userId, @Param("userLevel") UserLevel userLevel);

    /**
     * 查询用户直接下级
     *
     * @param userId
     * @param pageable
     * @return
     */
    @Query("SELECT user from User user where user.parent.id =:userId")
    List<User> findJuniorUsers(@Param("userId") Long userId);

    /**
     * 查询用户直接下级
     *
     * @param userId
     * @param pageable
     * @return
     */
    @Query("SELECT user from User user where user.parent.id =:userId")
    Page<User> findJuniorUser(@Param("userId") Long userId, Pageable pageable);

    /**
     * 根据条件查询数据
     *
     * @param username
     * @param mobile
     * @param start
     * @param end
     * @param pageable
     * @return
     */
    @Query("select user from User user where user.username like :username and user.mobile like :mobile " +
            " and user.createDate >= :start and user.createDate <= :end ")
    Page<User> findByUsernameWithDate(@Param("username") String username,
                                      @Param("mobile") String mobile,
                                      @Param("start") Date start,
                                      @Param("end") Date end,
                                      Pageable pageable);

    /**
     * 查询导出用户的数量
     *
     * @param username
     * @param mobile
     * @param start
     * @param end
     * @return
     */
    @Query("select count (user) from User user where user.username like :username and user.mobile like :mobile " +
            " and user.createDate >= :start and user.createDate <= :end ")
    int getDownLoadUserAccount(@Param("username") String username,
                               @Param("mobile") String mobile,
                               @Param("start") Date start,
                               @Param("end") Date end);

    /**
     * 查询导出用
     *
     * @param username
     * @param mobile
     * @param start
     * @param end
     * @return
     */
    @Query("select user from User user where user.username like :username and user.mobile like :mobile " +
            " and user.createDate >= :start and user.createDate <= :end ")
    List<User> findDownLoadUsers(@Param("username") String username,
                                 @Param("mobile") String mobile,
                                 @Param("start") Date start,
                                 @Param("end") Date end);

    /**
     * 查询下线是店主的个数
     *
     * @param userId
     * @return
     */
    @Query("select count(user) from User user where user.parent.id=:userId and user.shopKeeper=true ")
    int findIsShopKeeperJuniors(@Param("userId") Long userId);

    /**
     * 查询用户资格列表
     *
     * @param username
     * @param mobile
     * @param start
     * @param end
     * @param pageable
     * @return
     */
    @Query("select userDetail.user from UserDetail userDetail where userDetail.userLevel is not null and userDetail.user.username like :username and userDetail.user.mobile like :mobile " +
            " and userDetail.user.createDate >= :start and userDetail.user.createDate <= :end ")
    Page<User> findQualificationUsers(@Param("username") String username,
                                      @Param("mobile") String mobile,
                                      @Param("start") Date start,
                                      @Param("end") Date end, Pageable pageable);
}
