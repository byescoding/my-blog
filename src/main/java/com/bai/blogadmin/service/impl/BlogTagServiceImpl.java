package com.bai.blogadmin.service.impl;

import com.bai.blogadmin.constants.BlogStatusConstants;
import com.bai.blogadmin.dao.BlogTagRelationMapper;
import com.bai.blogadmin.pojo.BlogTag;
import com.bai.blogadmin.dao.BlogTagMapper;
import com.bai.blogadmin.pojo.BlogTagCount;
import com.bai.blogadmin.pojo.BlogTagRelation;
import com.bai.blogadmin.service.BlogTagService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 标签表 服务实现类
 * </p>
 *
 * @author 小白
 * @since 2020-06-16
 */
@Service
public class BlogTagServiceImpl extends ServiceImpl<BlogTagMapper, BlogTag> implements BlogTagService {
    @Autowired
    private BlogTagMapper blogTagMapper;

    @Autowired
    private BlogTagRelationMapper blogTagRelationMapper;

    /**
     * 获取所有关联标签
     * @return
     */
    @Override
    public List<BlogTagCount> getBlogTagCountForIndex() {
        QueryWrapper<BlogTag>queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(BlogTag::getIsDeleted, BlogStatusConstants.ZERO);
        List<BlogTag> blogTags = blogTagMapper.selectList(queryWrapper);
        List<BlogTagCount> blogTagCounts = blogTags.stream()
                .map(blogTag -> new BlogTagCount()
                        .setTagId(blogTag.getTagId())
                        .setTagName(blogTag.getTagName())
                        .setTagCount(
                                blogTagRelationMapper.selectCount(new QueryWrapper<BlogTagRelation>()
                                        .lambda()
                                        .eq(BlogTagRelation::getBlogId, blogTag.getTagId()))
                        )).collect(Collectors.toList());

        return blogTagCounts;
    }
}
