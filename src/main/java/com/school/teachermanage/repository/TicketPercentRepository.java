package com.school.teachermanage.repository;

import com.school.teachermanage.entity.TicketPercent;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author wudc
 * @Date 2017/11/27.
 */
public interface TicketPercentRepository extends PagingAndSortingRepository<TicketPercent, Long> {
    @Query("select  ticketPercent from TicketPercent ticketPercent  where ticketPercent.available=true ")
    TicketPercent findEnable();
}
