package com.school.teachermanage.service;

import com.school.teachermanage.repository.RefundReposity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 退货款服务类
 *
 * @author wudc
 * @Date 2017/11/16.
 */
@Service
public class RefundService {
    @Resource
    private RefundReposity refundReposity;
}
