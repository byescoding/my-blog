package com.bai.blogadmin.service;

import com.bai.blogadmin.pojo.BlogTag;
import com.bai.blogadmin.pojo.BlogTagCount;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 标签表 服务类
 * </p>
 *
 * @author 小白
 * @since 2020-06-16
 */
public interface BlogTagService extends IService<BlogTag> {
    /*
    获取博客标签总数
     */
    List<BlogTagCount> getBlogTagCountForIndex();
}
