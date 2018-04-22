package com.school.teachermanage.service;

import com.school.teachermanage.repository.ColorReposity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *颜色服务类
 *
 * @author wudc
 * @Date 2017/11/16.
 */
@Service
public class ColorService {
    @Resource
    private ColorReposity colorReposity;
}
