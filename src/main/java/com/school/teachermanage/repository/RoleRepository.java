package com.school.teachermanage.repository;

import com.school.teachermanage.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色数据
 *
 * @author zhangsl
 * @date 2017-11-05
 */
@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {

    @Query("select role from Role role")
    Page<Role> list(Pageable pageable);

    @Query("select role from Role role join role.admins admin where admin.id= :adminId")
    List<Role> findByAdminId(@Param("adminId") Long adminId);

    List<Role> findByAvailable(Boolean available);

    @Query("select role from Role role where role.role =:role ")
    Role findByRole(@Param("role")String role);
}
