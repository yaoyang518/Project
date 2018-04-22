package com.school.teachermanage.repository;

import com.school.teachermanage.entity.WeChatNotifyRecord;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author mandy
 * @Date 2017/11/16.
 */
public interface WeChatNotifyRecordReposity extends PagingAndSortingRepository<WeChatNotifyRecord,Long> {
}
