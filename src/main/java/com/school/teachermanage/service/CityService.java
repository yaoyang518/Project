package com.school.teachermanage.service;

import com.school.teachermanage.repository.CityReposity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *城市服务类
 *
 * @author wudc
 * @Date 2017/11/16.
 */
@Service
public class CityService {
    @Resource
    private CityReposity cityReposity;
}
