package com.school.teachermanage.repository;

import com.school.teachermanage.entity.RechargeRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * recharge 记录数据
 *
 * @author zhangsl
 * @date 2017-11-05
 */
@Repository
public interface RechargeRecordRepository extends CrudRepository<RechargeRecord, Long> {

    @Query("select rechargeRecord from RechargeRecord rechargeRecord where  rechargeRecord.user.mobile like :mobile and rechargeRecord.createDate >= :start and rechargeRecord.createDate <= :end ")
    Page<RechargeRecord> findRechargeRecordsByDateAndMobile(@Param("mobile") String mobile,
                                                            @Param("start") Date start,
                                                            @Param("end") Date end,
                                                            Pageable pageable);

    @Query("select rechargeRecord from RechargeRecord rechargeRecord join rechargeRecord.user user where user.id=:userId and  rechargeRecord.createDate >= :start and rechargeRecord.createDate <= :end ")
    Page<RechargeRecord> findRechargeRecordsByUserIdWithDate(@Param("userId") Long userId,
                                                             @Param("start") Date start,
                                                             @Param("end") Date end,
                                                             Pageable pageable);
}
