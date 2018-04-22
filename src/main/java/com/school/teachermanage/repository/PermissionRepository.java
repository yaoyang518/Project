package com.school.teachermanage.repository;

import com.school.teachermanage.entity.Permission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 权限数据
 *
 * @author zhangsl
 * @date 2017-11-05
 */
@Repository
public interface PermissionRepository extends PagingAndSortingRepository<Permission, Long> {

    @Query(value = "select permission  from  Permission permission join permission.roles role where role.id=:roleId ")
    List<Permission> findByRoleId(@Param("roleId") Long roleId);

    List<Permission> findByAvailable(Boolean available);

    Permission findByPermission(String permission);



}
