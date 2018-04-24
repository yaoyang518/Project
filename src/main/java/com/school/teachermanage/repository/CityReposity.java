package com.school.teachermanage.repository;

import com.school.teachermanage.entity.City;
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
public interface CityReposity extends PagingAndSortingRepository<City,Long>{

    @Query("select city from City city where city.province.id = :provinceId")
    List<City> findCitiesByProvinceId(@Param("provinceId") Long provinceId);
}
