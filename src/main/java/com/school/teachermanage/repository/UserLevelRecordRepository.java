package com.school.teachermanage.repository;

import com.school.teachermanage.entity.UserLevelRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * 用户升级记录
 *
 * @author wudc
 * @date 2017-11-14
 */

@Repository
public interface UserLevelRecordRepository extends CrudRepository<UserLevelRecord, Long> {


    @Query("select userLevelRecord from UserLevelRecord userLevelRecord where userLevelRecord.user.mobile like :mobile " +
            " and userLevelRecord.createDate >= :start and userLevelRecord.createDate <= :end ")
    Page<UserLevelRecord> findByMobileWithDate(@Param("mobile") String mobile,
                                               @Param("start") Date start,
                                               @Param("end") Date end,
                                               Pageable pageable);

}
