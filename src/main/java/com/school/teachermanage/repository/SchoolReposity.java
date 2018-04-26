package com.school.teachermanage.repository;

import com.school.teachermanage.entity.School;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author yaoyang
 * @Description:
 * @date 2018/4/25
 */
@Repository
public interface SchoolReposity extends CrudRepository<School, Long> {

    /**
     * 查找学校
     * @param pageable
     * @return
     */
    @Query("select school from School school")
    Page<School> findSchools(Pageable pageable);
}
