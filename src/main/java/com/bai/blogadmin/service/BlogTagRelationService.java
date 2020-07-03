package com.bai.blogadmin.service;

import com.bai.blogadmin.pojo.BlogInfo;
import com.bai.blogadmin.pojo.BlogTagRelation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 博客跟标签的关系表 服务类
 * </p>
 *
 * @author 小白
 * @since 2020-06-16
 */
public interface BlogTagRelationService extends IService<BlogTagRelation> {
    /**
     * 移除本来的标签保存新标签
     * @param blogInfo
     * @return boolean
     * @date 2020/1/29 21:31
     */
    void removeAndsaveBatch(List<Integer> blogTagIds, BlogInfo blogInfo);
}
