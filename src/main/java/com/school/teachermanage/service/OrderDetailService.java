package com.school.teachermanage.service;

import com.school.teachermanage.repository.OrderDetailReposity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 订单详细服务类
 *
 * @author wudc
 * @Date 2017/11/16.
 */
@Service
public class OrderDetailService {
    @Resource
    private OrderDetailReposity orderDetailReposity;
}
