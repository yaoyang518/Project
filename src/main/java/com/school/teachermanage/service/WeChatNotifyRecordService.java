package com.school.teachermanage.service;

import com.school.teachermanage.repository.WeChatNotifyRecordReposity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 微信返回信息服务类
 *
 * @author wudc
 * @Date 2017/11/16.
 */
@Service
public class WeChatNotifyRecordService {
    @Resource
    private WeChatNotifyRecordReposity weChatNotifyRecordReposity;
}
