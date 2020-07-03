package com.bai.blogadmin.controller.admin;


import com.bai.blogadmin.constants.HttpStatusConstants;
import com.bai.blogadmin.dto.AjaxPutPage;
import com.bai.blogadmin.dto.AjaxResultPage;
import com.bai.blogadmin.dto.Result;
import com.bai.blogadmin.pojo.BlogComment;
import com.bai.blogadmin.service.BlogCommentService;
import com.bai.blogadmin.util.DateUtils;
import com.bai.blogadmin.util.ResultGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 * 评论信息表 前端控制器
 * </p>
 *
 * @author 小白
 * @since 2020-06-16
 */
@Controller
@RequestMapping("/admin")
public class BlogCommentController {
  @Autowired
  private BlogCommentService blogCommentService;

    /**
     * 跳转至评论区
     * @return
     */
    @RequestMapping("/v1/comment")
    public String gotoComment(){
        return "adminLayui/comment-list";
    }

    /**
     * 评论列表分页
     */
  @ResponseBody
    @GetMapping("/v1/comment/paging")
    public AjaxResultPage<BlogComment> getLinkList(AjaxPutPage<BlogComment> ajaxPutPage ,BlogComment condition){
      QueryWrapper<BlogComment> queryWrapper = new QueryWrapper<>(condition);
      Page<BlogComment> page = ajaxPutPage.putPageToPage();
      blogCommentService.page(page,queryWrapper);
      AjaxResultPage<BlogComment> result = new AjaxResultPage<>();
      result.setCount(page.getTotal());
      result.setData(page.getRecords());
      return result;

  }

    /**
     * 删除评论跟新状态
     * @param blogComment
     * @return
     */
    @ResponseBody
    @PostMapping(value = {"/v1/comment/isDel","/v1/comment/commentStatus"})
    public Result updateLinkStatus(BlogComment blogComment){
        boolean flag= blogCommentService.updateById(blogComment);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusConstants.OK);
        }else {
            return ResultGenerator.getResultByHttp(HttpStatusConstants.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     *
     * 修改评论
     * @param blogComment
     * @return
     */
    @ResponseBody
    @PostMapping("/v1/comment/edit")
    public Result updateBlogComment(BlogComment blogComment){
        blogComment.setReplyCreateTime(DateUtils.getLocalCurrentDate());
        boolean flag = blogCommentService.updateById(blogComment);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusConstants.OK);
        }else {
            return ResultGenerator.getResultByHttp(HttpStatusConstants.INTERNAL_SERVER_ERROR);
        }

    }

}

