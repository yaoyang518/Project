package com.school.teachermanage.repository;

import com.school.teachermanage.entity.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * 管理员数据
 *
 * @author zhangsl
 * @date 2017-11-05
 */
@Repository
public interface AdminRepository extends PagingAndSortingRepository<Admin, Long> {
    /**
     * 通过username查找用户信息;
     *
     * @param username
     * @return 管理员
     */
    Admin findByUsername(String username);

    /**
     * 根据条件查询数据
     *
     * @param username
     * @param pageable
     * @return 管理员列表
     */
    Page<Admin> findByUsernameContains(String username, Pageable pageable);


}