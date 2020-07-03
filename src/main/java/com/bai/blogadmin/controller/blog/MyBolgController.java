package com.bai.blogadmin.controller.blog;

import com.bai.blogadmin.constants.BlogStatusConstants;
import com.bai.blogadmin.constants.HttpStatusConstants;
import com.bai.blogadmin.constants.LinkConstants;
import com.bai.blogadmin.controller.vo.BlogdetailVo;
import com.bai.blogadmin.dto.AjaxPutPage;
import com.bai.blogadmin.dto.AjaxResultPage;
import com.bai.blogadmin.dto.Result;
import com.bai.blogadmin.pojo.*;
import com.bai.blogadmin.service.*;
import com.bai.blogadmin.util.PageResult;
import com.bai.blogadmin.util.ResultGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jdk.nashorn.internal.ir.CallNode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MyBolgController {
    public static String theme = "amaze";

    @Autowired
    private BlogInfoService blogInfoService;

    @Autowired
    private BlogTagService blogTagService;

    @Autowired
    private BlogConfigService blogConfigService;

    @Autowired
    private BlogTagRelationService blogTagRelationService;

    @Autowired
    private BlogCommentService blogCommentService;

    @Autowired
    private BlogLinkService blogLinkService;

    /**
     * 博客首页
     * @param request
     * @return
     */
    @GetMapping({"/", "/index", "index.html"})
    public String index(HttpServletRequest request) {
        return this.page(request, 1);
    }

    /**
     * 分页
     * @param request
     * @param pageNum
     * @return
     */
   @GetMapping("/page/{pageNum}")
    public String page(HttpServletRequest request, @PathVariable("pageNum") int pageNum){
       Page<BlogInfo> page = new Page<>(pageNum,8);
       blogInfoService.page(page,new QueryWrapper<BlogInfo>()
       .lambda()
       .eq(BlogInfo::getBlogStatus, BlogStatusConstants.ONE)
       .eq(BlogInfo::getIsDeleted,BlogStatusConstants.ZERO)
       .orderByDesc(BlogInfo::getCreateTime));

       PageResult pageResult = new PageResult(page.getRecords(), page.getTotal(), 8, pageNum);
       request.setAttribute("blogPageResult", pageResult);
       request.setAttribute("newBlogs", blogInfoService.getNewBlog());
       request.setAttribute("hotBlogs", blogInfoService.getHotBlog());
       request.setAttribute("hotTags", blogTagService.getBlogTagCountForIndex());
       request.setAttribute("pageName", "首页");
       request.setAttribute("configurations", blogConfigService.getAllConfigs());
      return "blog/" + theme + "/index";
   }


    /**\
     * 搜索
     * @param request
     * @param keyword
     * @return
     */

   @GetMapping("/search/{keyword}")
    public String search(HttpServletRequest request,@PathVariable("keyword") String keyword){
       return search(request, keyword, 1);
   }

   @GetMapping("/search/{keyword}/{pageNum}")
    public String search(HttpServletRequest request,@PathVariable("keyword") String keyword,
                         @PathVariable("pageNum") int pageNum){
       Page<BlogInfo> page = new Page<BlogInfo>(pageNum,8);
         blogInfoService.page(page, new QueryWrapper<BlogInfo>()
               .lambda().like(BlogInfo::getBlogTitle, keyword)
               .eq(BlogInfo::getBlogStatus, BlogStatusConstants.ZERO)
               .eq(BlogInfo::getIsDeleted, BlogStatusConstants.ZERO)
                 .orderByDesc(BlogInfo::getCreateTime)
       );
//          * @param list       列表数据
//     * @param totalCount 总记录数
//     * @param pageSize   每页记录数
//     * @param currPage   当前页数
       PageResult pageResult = new PageResult(page.getRecords(), page.getTotal(), 8, pageNum);
       request.setAttribute("blogPageResult", pageResult);//当前页数的结果集
       request.setAttribute("pageName", "搜索");
       request.setAttribute("pageUrl", "search");
       request.setAttribute("keyword", keyword);
       request.setAttribute("newBlogs", blogInfoService.getNewBlog());
       request.setAttribute("hotBlogs", blogInfoService.getHotBlog());
       request.setAttribute("hotTags", blogTagService.getBlogTagCountForIndex());
       request.setAttribute("configurations", blogConfigService.getAllConfigs());
       return "blog/" + theme + "/list";
   }

    /**
     * 标签
     * @param request
     * @param tagId
     * @return
     */
   @GetMapping("/tag/{tagId}")
   public String tag(HttpServletRequest request, @PathVariable("tagId") String tagId) {
       return tag(request, tagId, 1);
   }

    /**
     * 标签分类
     * @param request
     * @param tagId
     * @param pageNum
     * @return
     */
    @GetMapping({"/tag/{tagId}/{pageNum}"})
    public String tag(HttpServletRequest request, @PathVariable("tagId") String tagId, @PathVariable("pageNum") Integer pageNum){
        List<BlogTagRelation> list = blogTagRelationService.list(new QueryWrapper<BlogTagRelation>()
                .lambda()
                .eq(BlogTagRelation::getBlogId, tagId));
        PageResult blogPageResult = null;
        if (!list.isEmpty()){
            Page<BlogInfo> page = new Page<BlogInfo>(pageNum, 8);
            blogInfoService.page(page,new QueryWrapper<BlogInfo>()
            .lambda()
            .eq(BlogInfo::getBlogStatus,BlogStatusConstants.ZERO)
            .eq(BlogInfo::getIsDeleted,BlogStatusConstants.ZERO)
             .in(BlogInfo::getBlogId,list.stream().map(BlogTagRelation::getBlogId).toArray())
             .orderByDesc(BlogInfo::getCreateTime));
            PageResult pageResult = new PageResult(page.getRecords(), page.getTotal(), 8, pageNum);

        }
        request.setAttribute("blogPageResult", blogPageResult);
        request.setAttribute("pageName", "标签");
        request.setAttribute("pageUrl", "tag");
        request.setAttribute("keyword", tagId);
        request.setAttribute("newBlogs", blogInfoService.getNewBlog());
        request.setAttribute("hotBlogs", blogInfoService.getHotBlog());
        request.setAttribute("hotTags", blogTagService.getBlogTagCountForIndex());
        request.setAttribute("configurations", blogConfigService.getAllConfigs());
        return "blog/" + theme + "/list";
    }

    /**
     * 获取分类的名字
     * @param request
     * @param categoryName
     * @return
     */
    @GetMapping({"/category/{categoryName}"})
    public String category(HttpServletRequest request, @PathVariable("categoryName") String categoryName) {
        return category(request, categoryName, 1);
    }

    /**
     * f分类的列表分页
     * @param request
     * @param categoryName
     * @param pageNum
     * @return
     */
    @GetMapping({"/category/{categoryName}/{pageNum}"})
    public String category(HttpServletRequest request, @PathVariable("categoryName") String categoryName, @PathVariable("pageNum") Integer pageNum){
        Page<BlogInfo> page = new Page<>(pageNum, 8);
        blogInfoService.page(page,new QueryWrapper<BlogInfo>()
        .lambda()
         .eq(BlogInfo::getBlogStatus,BlogStatusConstants.ZERO)
         .eq(BlogInfo::getIsDeleted,BlogStatusConstants.ZERO)
         .eq(BlogInfo::getBlogCategoryName,categoryName)
         .orderByDesc(BlogInfo::getCreateTime));

        PageResult pageResult = new PageResult(page.getRecords(), page.getTotal(),8,pageNum);
        request.setAttribute("blogPageResult", pageResult);
        request.setAttribute("pageName", "分类");
        request.setAttribute("pageUrl", "category");
        request.setAttribute("keyword", categoryName);
        request.setAttribute("newBlogs", blogInfoService.getNewBlog());
        request.setAttribute("hotBlogs", blogInfoService.getHotBlog());
        request.setAttribute("hotTags", blogTagService.getBlogTagCountForIndex());
        request.setAttribute("configurations", blogConfigService.getAllConfigs());
        return "blog/" + theme + "/list";

    }

    /**
     * 文章详情
     * @param request
     * @param blogId
     * @return
     */
    @GetMapping({"/blog/{blogId}", "/article/{blogId}"})
    public String detail(HttpServletRequest request, @PathVariable("blogId") Long blogId){
        //获取文章信息
        BlogInfo blogInfo = blogInfoService.getById(blogId);
        //获取当前博客的关联标签
        List<BlogTagRelation> blogTagRelations= blogTagRelationService.list(new QueryWrapper<BlogTagRelation>()
                .lambda()
                .eq(BlogTagRelation::getBlogId, blogId));
        //实时更新观看人数
    blogInfo.setBlogViews(blogInfo.getBlogViews()+1);
    blogInfoService.updateById(blogInfo);
    //获得关联标签的列表
        List<Integer> tagIds = new ArrayList<>();
        List<BlogTag> tagList = new ArrayList<>();
        if (blogTagRelations.isEmpty()){
            tagIds = blogTagRelations.stream()
                    .map(BlogTagRelation::getTagId).collect(Collectors.toList());
            tagList = blogTagService.list(new QueryWrapper<BlogTag>().lambda().in(BlogTag::getTagId, tagIds));
        }
      //关联评论
        int count = blogCommentService.count(new QueryWrapper<BlogComment>()
        .lambda()
        .eq(BlogComment::getBlogId,blogId)
        .eq(BlogComment::getCommentStatus,BlogStatusConstants.ZERO)
        .eq(BlogComment::getIsDeleted,BlogStatusConstants.ZERO));

        BlogdetailVo blogdetailVo = new BlogdetailVo();
        BeanUtils.copyProperties(blogInfo,blogdetailVo);
        blogdetailVo.setCommentCount(count);
        request.setAttribute("blogDetailVO", blogdetailVo);
        request.setAttribute("tagList", tagList);
        request.setAttribute("pageName", "详情");
        request.setAttribute("configurations", blogConfigService.getAllConfigs());
        return "blog/" + theme + "/detail";
    }

    /**
     * 返评论列表
     * @param ajaxPutPage
     * @param blogId
     * @return
     */
    @GetMapping("/blog/listComment")
    @ResponseBody
    public AjaxResultPage<BlogComment> listComment(AjaxPutPage<BlogComment> ajaxPutPage, Integer blogId){
        Page<BlogComment> page = ajaxPutPage.putPageToPage();
        blogCommentService.page(page,new QueryWrapper<BlogComment>().lambda()
        .eq(BlogComment::getBlogId,blogId)
         .eq(BlogComment::getCommentStatus,BlogStatusConstants.ZERO)
         .eq(BlogComment::getIsDeleted,BlogStatusConstants.ZERO)
         .orderByDesc(BlogComment::getCommentCreateTime)
        );
        AjaxResultPage<BlogComment> result = new AjaxResultPage<>();
        result.setData(page.getRecords());
        result.setCount(page.getTotal());
        return result;
    }

    /**
     * 友链
     * @param request
     * @return
     */
    @GetMapping({"/link"})
    public String link(HttpServletRequest request){
        request.setAttribute("pageName", "友情链接");
        List<BlogLink> favoriteLinks = blogLinkService.list(new QueryWrapper<BlogLink>()
                .lambda().eq(BlogLink::getLinkType, LinkConstants.LINK_TYPE_FRIENDSHIP.getLinkTypeId())
        );
        List<BlogLink> recommendLinks = blogLinkService.list(new QueryWrapper<BlogLink>()
                .lambda().eq(BlogLink::getLinkType, LinkConstants.LINK_TYPE_RECOMMEND.getLinkTypeId())
        );
        List<BlogLink> personalLinks = blogLinkService.list(new QueryWrapper<BlogLink>()
                .lambda().eq(BlogLink::getLinkType, LinkConstants.LINK_TYPE_PRIVATE.getLinkTypeId())
        );
        //判断友链类别并封装数据 0-友链 1-推荐 2-个人网站
        request.setAttribute("favoriteLinks", favoriteLinks);
        request.setAttribute("recommendLinks", recommendLinks);
        request.setAttribute("personalLinks", personalLinks);
        request.setAttribute("configurations", blogConfigService.getAllConfigs());
        return "blog/" + theme + "/link";
    }

    /**
     * 提交评论
     *
     * @param request
     * @param blogComment
     * @return
     */
    @PostMapping(value = "/blog/comment")
    @ResponseBody
    public Result comment(HttpServletRequest request, @Validated BlogComment blogComment){
       String ref= request.getHeader("Referer");
        if (StringUtils.isEmpty(ref)){
            return ResultGenerator.genFailResult("非法请求");
        }
        boolean flag = blogCommentService.save(blogComment);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusConstants.OK);
        }else {
            return ResultGenerator.getResultByHttp(HttpStatusConstants.INTERNAL_SERVER_ERROR);
        }
    }
}
