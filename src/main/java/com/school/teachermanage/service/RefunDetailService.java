package com.school.teachermanage.service;

import com.school.teachermanage.repository.RefundDetailReposity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 退货款详情服务类
 *
 * @author wudc
 * @Date 2017/11/16.
 */
@Service
public class RefunDetailService {
    @Resource
    private RefundDetailReposity refundDetailReposity;
}
