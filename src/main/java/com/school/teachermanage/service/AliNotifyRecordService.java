package com.school.teachermanage.service;

import com.school.teachermanage.repository.AliNotifyRecordReposity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Ali 记录服务类
 *
 * @author wudc
 * @Date 2017/11/16.
 */
@Service
public class AliNotifyRecordService {
    @Resource
    private AliNotifyRecordReposity aliNotifyRecordReposity;
}
