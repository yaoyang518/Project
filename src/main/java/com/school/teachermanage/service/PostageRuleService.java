package com.school.teachermanage.service;

import com.school.teachermanage.repository.PostageRuleReposity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 运费规则服务类
 *
 * @author wudc
 * @Date 2017/11/16.
 */
@Service
public class PostageRuleService {
    @Resource
    private PostageRuleReposity postageRuleReposity;
}
