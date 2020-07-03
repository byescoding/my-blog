package com.bai.blogadmin.service.impl;

import com.bai.blogadmin.constants.BlogStatusConstants;
import com.bai.blogadmin.controller.vo.simpleBlogListVo;
import com.bai.blogadmin.dao.BlogCommentMapper;
import com.bai.blogadmin.dao.BlogTagRelationMapper;
import com.bai.blogadmin.pojo.BlogComment;
import com.bai.blogadmin.pojo.BlogInfo;
import com.bai.blogadmin.dao.BlogInfoMapper;
import com.bai.blogadmin.pojo.BlogTagRelation;
import com.bai.blogadmin.service.BlogInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 博客信息表 服务实现类
 * </p>
 *
 * @author 小白
 * @since 2020-06-16
 */
@Service
public class BlogInfoServiceImpl extends ServiceImpl<BlogInfoMapper, BlogInfo> implements BlogInfoService {
@Autowired
private BlogInfoMapper blogInfoMapper;
@Autowired
private BlogTagRelationMapper blogTagRelationMapper;

@Autowired
private BlogCommentMapper blogCommentMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean clearBlogInfo(Long blogId) {
        if (SqlHelper.retBool(blogInfoMapper.deleteById(blogId))){
            QueryWrapper<BlogTagRelation> tagRelationWrapper = new QueryWrapper<>();
            tagRelationWrapper.lambda().eq(BlogTagRelation::getBlogId,blogId);
            blogTagRelationMapper.delete(tagRelationWrapper);
            QueryWrapper<BlogComment> commentWrapper = new QueryWrapper<>();
            commentWrapper.lambda().eq(BlogComment::getBlogId,blogId);
            blogCommentMapper.delete(commentWrapper);
            return true;
        }
        return false;
    }



    @Override
    public List<simpleBlogListVo> getNewBlog() {
        List<simpleBlogListVo> simpleBlogListVOS = new ArrayList<>();
        Page<BlogInfo> page = new Page<>(1,5);
        blogInfoMapper.selectPage(page,new QueryWrapper<BlogInfo>()
                .lambda()
                .eq(BlogInfo::getBlogStatus, BlogStatusConstants.ONE)
                .eq(BlogInfo::getIsDeleted,BlogStatusConstants.ZERO)
                .orderByDesc(BlogInfo::getCreateTime));
        for (BlogInfo blogInfo : page.getRecords()){
            simpleBlogListVo simpleBlogListVO = new simpleBlogListVo();
            BeanUtils.copyProperties(blogInfo, simpleBlogListVO);
            simpleBlogListVOS.add(simpleBlogListVO);
        }
        return simpleBlogListVOS;
    }

    @Override
    public List<simpleBlogListVo> getHotBlog() {
        List<simpleBlogListVo> simpleBlogListVOS = new ArrayList<>();
        Page<BlogInfo> page = new Page<>(1,5);
        blogInfoMapper.selectPage(page,new QueryWrapper<BlogInfo>()
                .lambda()
                .eq(BlogInfo::getBlogStatus, BlogStatusConstants.ONE)
                .eq(BlogInfo::getIsDeleted,BlogStatusConstants.ZERO)
                .orderByDesc(BlogInfo::getBlogViews));
        for (BlogInfo blogInfo : page.getRecords()){
            simpleBlogListVo simpleBlogListVO = new simpleBlogListVo();
            BeanUtils.copyProperties(blogInfo, simpleBlogListVO);
            simpleBlogListVOS.add(simpleBlogListVO);
        }
        return simpleBlogListVOS;
    }
}
