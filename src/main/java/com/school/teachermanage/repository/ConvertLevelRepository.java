package com.school.teachermanage.repository;

import com.school.teachermanage.entity.ConvertLevel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * 收益档次数据
 *
 * @author zhangsl
 * @date 2017-11-05
 */
@Repository
public interface ConvertLevelRepository extends CrudRepository<ConvertLevel, Long> {

    @Query("select convertLevel from ConvertLevel convertLevel where convertLevel.available=true")
    ConvertLevel findEnable();
}
