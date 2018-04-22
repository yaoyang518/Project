package com.school.teachermanage.repository;

import com.school.teachermanage.entity.PayoutConfig;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * @author wudc
 * @Date 2017/11/20.
 */
@Repository
public interface PayoutConfigReposity extends PagingAndSortingRepository<PayoutConfig,Long>{
    @Query("select payoutConfig from PayoutConfig payoutConfig where payoutConfig.available=true ")
    PayoutConfig findEnable();
}
