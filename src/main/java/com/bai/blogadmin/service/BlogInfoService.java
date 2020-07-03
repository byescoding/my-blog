package com.bai.blogadmin.service;

import com.bai.blogadmin.controller.vo.simpleBlogListVo;
import com.bai.blogadmin.pojo.BlogInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 博客信息表 服务类
 * </p>
 *
 * @author 小白
 * @since 2020-06-16
 */
public interface BlogInfoService extends IService<BlogInfo> {
    /**
     * 清除文章
     * @param blogId
     * @return boolean
     * @date 2020/1/29 21:54
     */
    boolean clearBlogInfo(Long blogId);

    /**
     * 返回最新的五条文章列表
     * @param
     * @return java.util.List<com.site.blog.controller.vo.SimpleBlogListVO>
     * @date 2019/9/4 9:04
     */
    List<simpleBlogListVo> getNewBlog();

    /**
     * 返回点击量最多的五条文章
     * @param
     * @return java.util.List<com.site.blog.controller.vo.SimpleBlogListVO>
     * @date 2019/9/4 9:15
     */
    List<simpleBlogListVo> getHotBlog();
}
