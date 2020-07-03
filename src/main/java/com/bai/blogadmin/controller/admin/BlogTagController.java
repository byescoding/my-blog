package com.bai.blogadmin.controller.admin;


import com.bai.blogadmin.constants.BlogStatusConstants;
import com.bai.blogadmin.constants.HttpStatusConstants;
import com.bai.blogadmin.constants.SysConfigConstants;
import com.bai.blogadmin.dto.AjaxPutPage;
import com.bai.blogadmin.dto.AjaxResultPage;
import com.bai.blogadmin.dto.Result;
import com.bai.blogadmin.pojo.BlogInfo;
import com.bai.blogadmin.pojo.BlogTag;
import com.bai.blogadmin.pojo.BlogTagRelation;
import com.bai.blogadmin.service.BlogInfoService;
import com.bai.blogadmin.service.BlogTagRelationService;
import com.bai.blogadmin.service.BlogTagService;
import com.bai.blogadmin.util.DateUtils;
import com.bai.blogadmin.util.ResultGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 标签表 前端控制器
 * </p>
 *
 * @author 小白
 * @since 2020-06-16
 */
@Controller
@RequestMapping("/admin")
public class BlogTagController {
    @Autowired
    private BlogTagService blogTagService;

    @Autowired
    private BlogInfoService blogInfoService;

    @Autowired
    private BlogTagRelationService blogTagRelationService;

    /**
     *跳转至标签区
     * @return
     */

    @GetMapping("/v1/tags")
    public String gotoTag(){
        return "adminLayui/tag-list";
    }
    /**
     * 返回未删除状态下的所有标签
     * @return
     */
    @ResponseBody
    @GetMapping("/v1/tags/list")
    public Result<BlogTag> tagsList(){
        QueryWrapper<BlogTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BlogTag::getIsDeleted, BlogStatusConstants.ZERO);
        List<BlogTag> list = blogTagService.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)){
            ResultGenerator.getResultByHttp(HttpStatusConstants.INTERNAL_SERVER_ERROR);
        }

            return ResultGenerator.getResultByHttp(HttpStatusConstants.OK,list);
    }

    /**
     * 标签分页
     * @param ajaxPutPage
     * @param condition
     * @return
     * @date
     */
    @ResponseBody
    @GetMapping("/v1/tags/paging")
    public AjaxResultPage<BlogTag> getCategoryList(AjaxPutPage<BlogTag> ajaxPutPage, BlogTag condition){
        QueryWrapper<BlogTag> queryWrapper = new QueryWrapper<>(condition);
        queryWrapper.lambda()
                .ne(BlogTag::getTagId,1);
        Page<BlogTag> page = ajaxPutPage.putPageToPage();
        blogTagService.page(page,queryWrapper);
        AjaxResultPage<BlogTag> result = new AjaxResultPage<>();
        result.setData(page.getRecords());
        result.setCount(page.getTotal());
        return result;
    }

    /**
     * 修改标签状态
     * @param blogTag
     * @return
     */

  @ResponseBody
    @PostMapping("/v1/tags/isDel")
  public Result updateCategoryStatus(BlogTag blogTag){
      boolean flag = blogTagService.updateById(blogTag);
      if (flag){
          return ResultGenerator.getResultByHttp(HttpStatusConstants.OK);
      }
      return ResultGenerator.getResultByHttp(HttpStatusConstants.INTERNAL_SERVER_ERROR);
  }

    /**
     * 添加标签
     * @param blogTag
     * @return
     */
    @ResponseBody
    @PostMapping("/v1/tags/add")
    public Result addTag(BlogTag blogTag){
        blogTag.setCreateTime(DateUtils.getLocalCurrentDate());
        boolean flag = blogTagService.save(blogTag);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusConstants.OK);
        }else {
            return ResultGenerator.getResultByHttp(HttpStatusConstants.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 清除标签
     * @param tagId
     * @return
     */

    @ResponseBody
    @PostMapping("/v1/tags/clear")
    public Result clearTag(int tagId){
        QueryWrapper<BlogTagRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BlogTagRelation::getTagId,tagId);
        List<BlogTagRelation> tagRelationList = blogTagRelationService.list(queryWrapper);

        //批量跟新BlogInfo的内容
        List<BlogInfo> infoList = tagRelationList.stream()
                .map(tagRelation -> new BlogInfo()
                        .setBlogId(tagRelation.getBlogId())
                        .setBlogTags(SysConfigConstants.DEFAULT_TAG.getConfigName())
                ).collect(Collectors.toList());
        List<Long> blogIds = infoList.stream().map(BlogInfo::getBlogId).collect(Collectors.toList());

        // 批量更新的tagRelation信息
        List<BlogTagRelation> tagRelations = tagRelationList.stream().map(tagRelation -> new BlogTagRelation()
                .setBlogId(tagRelation.getBlogId())
                .setTagId(Integer.valueOf(SysConfigConstants.DEFAULT_CATEGORY.getConfigField()))
        ).collect(Collectors.toList());

       // 批量更新
        blogInfoService.updateBatchById(infoList);
      blogTagRelationService.remove(new QueryWrapper<BlogTagRelation>()
      .lambda()
       .in(BlogTagRelation::getBlogId,blogIds)
      );

      blogTagRelationService.saveBatch(tagRelations);
      blogTagService.removeById(tagId);
      return ResultGenerator.getResultByHttp(HttpStatusConstants.OK);
    }

}

