package com.school.teachermanage.repository;

import com.school.teachermanage.entity.ConvertPercent;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
/**
 * 转换比率数据
 * @author wudc
 * @date 2017-11-06
 */
public interface ConvertPercentRepository extends CrudRepository<ConvertPercent, Long> {

    @Query("select  convertPercent from ConvertPercent convertPercent  where convertPercent.available=true ")
    ConvertPercent findEnable ();
}
