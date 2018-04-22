package com.school.teachermanage.service;

import com.school.teachermanage.repository.OrderReposity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 订单服务类
 *
 * @author wudc
 * @Date 2017/11/16.
 */
@Service
public class OrderService {
    @Resource
    private OrderReposity orderReposity;
}
