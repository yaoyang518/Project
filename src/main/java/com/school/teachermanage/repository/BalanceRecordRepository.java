package com.school.teachermanage.repository;

import com.school.teachermanage.entity.BalanceRecord;
import com.school.teachermanage.entity.User;
import com.school.teachermanage.enumeration.BalanceSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户升级balance 记录
 *
 * @author wudc
 * @date 2017-11-14
 */

@Repository
public interface BalanceRecordRepository extends PagingAndSortingRepository<BalanceRecord, Long> {
    /**
     * 查询用户余额记录
     *
     * @param userId
     * @param pageable
     * @return
     */
    @Query("select balanceRecord from BalanceRecord balanceRecord where balanceRecord.user.id=:userId ")
    Page<BalanceRecord> findBalanceRecordByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 获取本周用户的
     *
     * @return
     */
    @Query(" SELECT  distinct balanceRecord.user FROM BalanceRecord  balanceRecord  WHERE YEARWEEK(date_format(balanceRecord.createDate,'%Y-%m-%d')) = YEARWEEK(now())-1 ")
    List<User> findUsersInPreWeek();

    /**
     * 获取本周用户的余额总数
     *
     * @param userId
     * @param balanceSource
     * @return
     */
    @Query("SELECT SUM(balanceRecord.amount) FROM BalanceRecord  balanceRecord  WHERE YEARWEEK(date_format(create_date,'%Y-%m-%d')) = YEARWEEK(now())-1 and balanceRecord.user.id =:userId AND balanceRecord.balanceSource=:balanceSource")
    BigDecimal getAmoutByUserIdAndBalanceSource(@Param("userId") Long userId, @Param("balanceSource") BalanceSource balanceSource);

    /**
     * 根据用户id获取分页记录
     *
     * @param userId
     * @param pageable
     * @return
     */
    @Query("select balanceRecord from BalanceRecord balanceRecord where balanceRecord.user.id = :userId ")
    Page<BalanceRecord> findRecordsByUserId(@Param("userId")Long userId, Pageable pageable);
}
