package com.school.teachermanage.service;

import com.school.teachermanage.repository.SizeReposity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 尺寸服务类
 *
 * @author wudc
 * @Date 2017/11/16.
 */
@Service
public class SizeService {
    @Resource
    private SizeReposity sizeReposity;
}
