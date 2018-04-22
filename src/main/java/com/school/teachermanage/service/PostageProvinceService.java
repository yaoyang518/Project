package com.school.teachermanage.service;

import com.school.teachermanage.repository.PostageRuleReposity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 邮件 省份 服务类
 *
 * @author wudc
 * @Date 2017/11/16.
 */
@Service
public class PostageProvinceService {
    @Resource
    private PostageRuleReposity postageRuleReposity;
}
