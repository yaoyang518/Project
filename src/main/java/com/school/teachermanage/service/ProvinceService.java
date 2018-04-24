package com.school.teachermanage.service;

import com.school.teachermanage.entity.Province;
import com.school.teachermanage.repository.ProvinceReposity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 省份服务类
 *
 * @author wudc
 * @Date 2017/11/16.
 */
@Service
public class ProvinceService {
    @Resource
    private ProvinceReposity provinceReposity;

    public Province save(Province province){ return provinceReposity.save(province); }
}
