package com.school.teachermanage.repository;

import com.school.teachermanage.entity.BitcoinRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * @author mandy
 * @Date 2017/11/28.
 */
@Repository
public interface BitcoinRecordRepository extends PagingAndSortingRepository<BitcoinRecord, Long> {
    @Query("select bitcoinRecord from BitcoinRecord bitcoinRecord where bitcoinRecord.user.mobile like :mobile and bitcoinRecord.createDate >= :start and bitcoinRecord.createDate <= :end ")
    Page<BitcoinRecord> findByMobileAndStatusWithDate(@Param("mobile") String mobile,
                                                      @Param("start") Date start,
                                                      @Param("end") Date end,
                                                      Pageable pageable);

    @Query("select bitcoinRecord from BitcoinRecord bitcoinRecord where bitcoinRecord.user.id=:userId ")
    Page<BitcoinRecord> findByUserId(@Param("userId") Long userId, Pageable pageable);

}
