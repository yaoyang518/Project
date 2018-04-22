package com.school.teachermanage.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * 分页查询工具类
 * @author zhangsl
 * @date 2017-11-01
 */
public class QueryUtil {

    public static PageRequest buildPageRequest(int page, int size, String field,boolean desc){
        Sort sort = null;
        if(StringUtil.isEmpty(field)) {
            sort = new Sort(Sort.Direction.DESC, "id");
        } else {
            if (desc){
                sort = new Sort(Sort.Direction.DESC, field);
            }else{
                sort = new Sort(Sort.Direction.ASC, field);
            }
        }
        return new PageRequest(page-1, size, sort);
    }

}
