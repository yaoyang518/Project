package com.school.teachermanage.service;

import com.school.teachermanage.repository.PostageTemplateReposity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 运费模板服务类
 *
 * @author wudc
 * @Date 2017/11/16.
 */
@Service
public class PostageTemplateService {
    @Resource
    private PostageTemplateReposity postageTemplateReposity;
}
