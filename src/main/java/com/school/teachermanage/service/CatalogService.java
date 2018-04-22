package com.school.teachermanage.service;

import com.school.teachermanage.repository.CatalogReposity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 类目服务类
 * @author wudc
 * @Date 2017/11/16.
 */
@Service
public class CatalogService {
    @Resource
    private CatalogReposity catalogReposity;
}
