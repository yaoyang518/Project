package com.school.teachermanage.repository;

import com.school.teachermanage.entity.TicketRecord;
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
public interface TicketRecordRepository extends PagingAndSortingRepository<TicketRecord, Long> {

    @Query("select ticketRecord from TicketRecord ticketRecord where ticketRecord.user.mobile like :mobile and ticketRecord.createDate >= :start " +
            "and ticketRecord.createDate <= :end ")
    Page<TicketRecord> findByMobileAndStatusWithDate(@Param("mobile") String mobile,
                                                     @Param("start") Date start,
                                                     @Param("end") Date end,
                                                     Pageable pageable);
    @Query("select ticketRecord from TicketRecord ticketRecord where ticketRecord.user.id=:userId")
    Page<TicketRecord> findByUserId(@Param("userId") Long userId, Pageable pageable);
}
