package com.school.teachermanage.repository;

import com.school.teachermanage.entity.LevelUp;
import com.school.teachermanage.enumeration.UserLevel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 升级金额配置
 *
 * @author zhangsl
 * @date 2017-11-13
 */
@Repository
public interface LevelUpRepository extends CrudRepository<LevelUp, Long> {

    /**
     * 获取所有启用配置
     *
     * @param available
     * @return
     */
    List<LevelUp> findByAvailable(Boolean available);

    /**
     * 根据用户等级查询配置
     *
     * @param userLevel
     * @return
     */
    LevelUp findByUserLevel(UserLevel userLevel);

    /**
     * 根据用户等级查询当前配置
     *
     * @param userLevel
     * @return
     */
    LevelUp findByAvailableAndUserLevel(Boolean available, UserLevel userLevel);

    /**
     * 查询别用户更高等级
     *
     * @param userLevel
     * @return
     */
    @Query("SELECT levelUp FROM LevelUp levelUp WHERE levelUp.userLevel > :userLevel AND available=true")
    List<LevelUp> findHigherLevel(@Param("userLevel") UserLevel userLevel);


}
