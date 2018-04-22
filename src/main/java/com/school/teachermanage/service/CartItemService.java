package com.school.teachermanage.service;

import com.school.teachermanage.repository.CartItemReposity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 购物车服务类
 *
 * @author wudc
 * @Date 2017/11/16.
 */
@Service
public class CartItemService {
    @Resource
    private CartItemReposity cartItemReposity;
}
