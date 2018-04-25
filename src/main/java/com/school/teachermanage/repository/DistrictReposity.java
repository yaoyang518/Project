package com.school.teachermanage.repository;

import com.school.teachermanage.entity.District;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wudc
 * @Date 2017/11/16.
 */
@Repository
public interface DistrictReposity extends PagingAndSortingRepository<District,Long>{

    @Query("select district from District district where district.city.id = :cityId ")
    List<District> findDistrictsByProvinceId(@Param("cityId") Long cityId);
}
