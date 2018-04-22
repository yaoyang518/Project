package com.school.teachermanage.service;

import com.school.teachermanage.repository.AddressReposity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 地址服务类
 *
 * @author wudc
 * @Date 2017/11/16.
 */
@Service
public class AddressService {
    @Resource
    private AddressReposity addressReposity;

}
