package com.school.teachermanage.repository;

import com.school.teachermanage.entity.ReplenishRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * replenish 记录数据
 *
 * @author zhangsl
 * @date 2017-11-28
 */
@Repository
public interface ReplenishRecordRepository extends CrudRepository<ReplenishRecord, Long> {

    @Query("select replenishRecord from ReplenishRecord replenishRecord where replenishRecord.user.mobile like :mobile and replenishRecord.createDate >= :start " +
            "and replenishRecord.createDate <= :end ")
    Page<ReplenishRecord> findByMobileAndStatusWithDate(@Param("mobile") String mobile,
                                                       @Param("start") Date start,
                                                       @Param("end") Date end,
                                                       Pageable pageable);
}
