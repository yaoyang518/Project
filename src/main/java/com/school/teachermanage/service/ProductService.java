package com.school.teachermanage.service;

import com.school.teachermanage.repository.ProductReposity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 商品服务类
 *
 * @author wudc
 * @Date 2017/11/16.
 */
@Service
public class ProductService {
    @Resource
    private ProductReposity productReposity;
}
