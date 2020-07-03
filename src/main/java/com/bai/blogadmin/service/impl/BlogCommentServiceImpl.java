package com.bai.blogadmin.service.impl;

import com.bai.blogadmin.pojo.BlogComment;
import com.bai.blogadmin.dao.BlogCommentMapper;
import com.bai.blogadmin.service.BlogCommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 评论信息表 服务实现类
 * </p>
 *
 * @author 小白
 * @since 2020-06-16
 */
@Service
public class BlogCommentServiceImpl extends ServiceImpl<BlogCommentMapper, BlogComment> implements BlogCommentService {

}
