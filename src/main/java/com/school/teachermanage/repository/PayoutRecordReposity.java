package com.school.teachermanage.repository;

import com.school.teachermanage.entity.PayoutRecord;
import com.school.teachermanage.enumeration.ApplyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author wudc
 * @Date 2017/11/20.
 */
@Repository
public interface PayoutRecordReposity extends PagingAndSortingRepository<PayoutRecord, Long> {

    @Query("select payoutRecord from PayoutRecord payoutRecord where payoutRecord.mobile like :mobile and payoutRecord.cardNo like :cardNo and payoutRecord.applyDate >= :start and payoutRecord.applyDate <= :end ")
    Page<PayoutRecord> findByMobileWithDate(@Param("mobile") String mobile,
                                            @Param("cardNo") String cardNo,
                                            @Param("start") Date start,
                                            @Param("end") Date end,
                                            Pageable pageable);

    @Query("select payoutRecord from PayoutRecord payoutRecord where payoutRecord.mobile like :mobile and payoutRecord.cardNo like :cardNo and payoutRecord.applyStatus =:applyStatus " +
            "and payoutRecord.applyDate >= :start and payoutRecord.applyDate <= :end ")
    Page<PayoutRecord> findByMobileAndApplyStatusWithDate(@Param("mobile") String mobile,
                                                          @Param("cardNo") String cardNo,
                                                          @Param("applyStatus") ApplyStatus applyStatus,
                                                          @Param("start") Date start,
                                                          @Param("end") Date end,
                                                          Pageable pageable);

    @Query("select count(payoutRecord) from PayoutRecord payoutRecord where payoutRecord.mobile like :mobile and payoutRecord.cardNo like :cardNo and payoutRecord.applyStatus =:applyStatus " +
            "and payoutRecord.applyDate >= :start and payoutRecord.applyDate <= :end ")
    int getDownLoadUserByAppStatusAccount(@Param("mobile") String mobile,
                                          @Param("cardNo") String cardNo,
                                          @Param("applyStatus") ApplyStatus applyStatus,
                                          @Param("start") Date start,
                                          @Param("end") Date end);

    @Query("select count(payoutRecord) from PayoutRecord payoutRecord where payoutRecord.mobile like :mobile and payoutRecord.cardNo like :cardNo and payoutRecord.applyDate >= :start and payoutRecord.applyDate <= :end ")
    int getDownLoadUserAccount(@Param("mobile") String mobile,
                               @Param("cardNo") String cardNo,
                               @Param("start") Date start,
                               @Param("end") Date end);

    @Query("select count(payoutRecord) from PayoutRecord payoutRecord where payoutRecord.mobile like :mobile and payoutRecord.cardNo like :cardNo and payoutRecord.applyDate >= :start and payoutRecord.applyDate <= :end ")
    List<PayoutRecord> findDownLoadUsers(@Param("mobile") String mobile,
                                         @Param("cardNo") String cardNo,
                                         @Param("start") Date start,
                                         @Param("end") Date end);

    @Query("select count(payoutRecord) from PayoutRecord payoutRecord where payoutRecord.mobile like :mobile and payoutRecord.cardNo like :cardNo and payoutRecord.applyStatus =:applyStatus " +
            "and payoutRecord.applyDate >= :start and payoutRecord.applyDate <= :end ")
    List<PayoutRecord> findDownLoadByAppStatusUsers(@Param("mobile") String mobile,
                                                    @Param("cardNo") String cardNo,
                                                    @Param("applyStatus") ApplyStatus applyStatus,
                                                    @Param("start") Date start,
                                                    @Param("end") Date end);
}
