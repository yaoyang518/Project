package com.school.teachermanage.service;

import com.school.teachermanage.repository.ItemReposity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * item 服务类
 *
 * @author wudc
 * @Date 2017/11/16.
 */
@Service
public class ItemService {
    @Resource
    private ItemReposity itemReposity;
}
