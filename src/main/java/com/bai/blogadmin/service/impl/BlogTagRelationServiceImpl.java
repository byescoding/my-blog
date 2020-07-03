package com.bai.blogadmin.service.impl;

import com.bai.blogadmin.pojo.BlogInfo;
import com.bai.blogadmin.pojo.BlogTagRelation;
import com.bai.blogadmin.dao.BlogTagRelationMapper;
import com.bai.blogadmin.service.BlogTagRelationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 博客跟标签的关系表 服务实现类
 * </p>
 *
 * @author 小白
 * @since 2020-06-16
 */
@Service
public class BlogTagRelationServiceImpl extends ServiceImpl<BlogTagRelationMapper, BlogTagRelation> implements BlogTagRelationService {

    @Autowired
    private BlogTagRelationMapper blogTagRelationMapper;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeAndsaveBatch(List<Integer> blogTagIds, BlogInfo blogInfo) {
        Long blogId = blogInfo.getBlogId();
        List<BlogTagRelation> list = blogTagIds.stream().map(blogTagId -> new BlogTagRelation()
                .setTagId(blogTagId)
                .setBlogId(blogId)).collect(Collectors.toList());
        blogTagRelationMapper.delete(new QueryWrapper<BlogTagRelation>()
                .lambda()
                .eq(BlogTagRelation::getBlogId, blogInfo.getBlogId()));
        for (BlogTagRelation item : list) {
            blogTagRelationMapper.insert(item);
        }
    }
}

