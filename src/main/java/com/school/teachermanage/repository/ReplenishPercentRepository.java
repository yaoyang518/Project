package com.school.teachermanage.repository;

import com.school.teachermanage.entity.ReplenishPercent;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author wudc
 * @Date 2017/11/27.
 */
public interface ReplenishPercentRepository extends PagingAndSortingRepository<ReplenishPercent,Long> {
    @Query("select  replenishPercent from ReplenishPercent replenishPercent  where replenishPercent.available=true ")
    ReplenishPercent findEnable ();
}
