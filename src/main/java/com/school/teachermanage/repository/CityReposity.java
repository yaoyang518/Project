package com.school.teachermanage.repository;

import com.school.teachermanage.entity.City;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * @author wudc
 * @Date 2017/11/16.
 */
@Repository
public interface CityReposity extends PagingAndSortingRepository<City,Long>{

}
