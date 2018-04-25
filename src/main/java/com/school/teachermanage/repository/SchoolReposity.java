package com.school.teachermanage.repository;

import com.school.teachermanage.entity.School;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author yaoyang
 * @Description:
 * @date 2018/4/25
 */
@Repository
public interface SchoolReposity extends CrudRepository<School, Long> {

}
