package com.bai.blogadmin.controller.admin;


import com.bai.blogadmin.constants.BlogStatusConstants;
import com.bai.blogadmin.constants.HttpStatusConstants;
import com.bai.blogadmin.constants.SysConfigConstants;
import com.bai.blogadmin.dto.AjaxPutPage;
import com.bai.blogadmin.dto.AjaxResultPage;
import com.bai.blogadmin.dto.Result;
import com.bai.blogadmin.pojo.BlogCategory;
import com.bai.blogadmin.pojo.BlogInfo;
import com.bai.blogadmin.service.BlogCategoryService;
import com.bai.blogadmin.service.BlogInfoService;
import com.bai.blogadmin.util.DateUtils;
import com.bai.blogadmin.util.ResultGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * <p>
 * 博客分类 前端控制器
 * </p>
 *
 * @author 小白
 * @since 2020-06-16
 */
@Controller
@RequestMapping("/admin")
public class BlogCategoryController {
    @Autowired
    private BlogCategoryService blogCategoryService;

    @Autowired
    private BlogInfoService blogInfoService;


    /**
     * 跳转至分类页面
     * @return
     */
    @GetMapping("/v1/category")
    public String gotoBlogCategory(){
        return "adminLayui/category-list";
    }


    /**
     * 获取分类的数据
     */

    @ResponseBody
    @GetMapping("/v1/category/list")
    public Result<BlogCategory> categoryList() {
        QueryWrapper<BlogCategory> blogCategoryQueryWrapper = new QueryWrapper<>();
        blogCategoryQueryWrapper.lambda().eq(BlogCategory::getIsDeleted, BlogStatusConstants.ZERO);
        List<BlogCategory> list = blogCategoryService.list(blogCategoryQueryWrapper);

        if (CollectionUtils.isEmpty(list)) {
            ResultGenerator.getResultByHttp(HttpStatusConstants.INTERNAL_SERVER_ERROR);
        }
            return ResultGenerator.getResultByHttp(HttpStatusConstants.OK, list);
    }

    /**
     * 分类的分页
     * @param ajaxPutPage
     * @param condition
     * @return
     */

    @ResponseBody
    @GetMapping("/v1/category/paging")
    public AjaxResultPage<BlogCategory> getCategoryList(AjaxPutPage<BlogCategory> ajaxPutPage
    ,BlogCategory condition){
        QueryWrapper<BlogCategory> blogCategoryQueryWrapper = new QueryWrapper<>(condition);

        blogCategoryQueryWrapper.lambda().orderByAsc(BlogCategory::getCategoryRank)
                .ne(BlogCategory::getCategoryId,1);
        Page<BlogCategory> blogCategoryPage = ajaxPutPage.putPageToPage();

        blogCategoryService.page(blogCategoryPage,blogCategoryQueryWrapper);
        AjaxResultPage<BlogCategory> result = new AjaxResultPage<>();
        result.setData(blogCategoryPage.getRecords());
        result.setCount(blogCategoryPage.getTotal());
        return result;
    }

    /**
     * 修改分类信息
     * @param blogCategory
     * @return
     */
    @ResponseBody
    @PostMapping("/v1/category/update")
    public Result updateCategory(BlogCategory blogCategory){
        //根据查询对应的id的分类
        BlogCategory sqlCategory = blogCategoryService.getById(blogCategory.getCategoryId());
        boolean flag = sqlCategory.getCategoryName().equals(blogCategory.getCategoryName());
        if (flag){
            blogCategoryService.updateById(blogCategory);
        }else {
            BlogInfo blogInfo = new BlogInfo().setBlogCategoryId(blogCategory.getCategoryId())
                    .setBlogCategoryName(blogCategory.getCategoryName());
            UpdateWrapper<BlogInfo> blogInfoUpdateWrapper = new UpdateWrapper<>();
            blogInfoUpdateWrapper.lambda().eq(BlogInfo::getBlogCategoryId,
                    blogCategory.getCategoryId());
            blogInfoService.update(blogInfo,blogInfoUpdateWrapper);
            blogCategoryService.updateById(blogCategory);

        }
     return ResultGenerator.getResultByHttp(HttpStatusConstants.OK);
    }

    /**
     * 修改分类状态
     * @param blogCategory
     * @return
     * @date
     */
    @ResponseBody
    @PostMapping("/v1/category/isDel")
    public Result updateCategoryStatus(BlogCategory blogCategory){
        boolean flag = blogCategoryService.updateById(blogCategory);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusConstants.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusConstants.INTERNAL_SERVER_ERROR);
    }


    @ResponseBody
    @PostMapping("/v1/category/clear")
    public Result   clearCategory(BlogCategory blogCategory){
        UpdateWrapper<BlogInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .eq(BlogInfo::getBlogCategoryId,blogCategory.getCategoryId())
                .set(BlogInfo::getBlogCategoryId, SysConfigConstants.DEFAULT_CATEGORY
                .getConfigField())
                .set(BlogInfo::getBlogCategoryName,SysConfigConstants.DEFAULT_CATEGORY.getConfigName());

             boolean flag = blogInfoService.update(updateWrapper);
              flag = blogCategoryService.removeById(blogCategory.getCategoryId());
              if (flag){
                  return ResultGenerator.getResultByHttp(HttpStatusConstants.OK);
              }else {
                  return ResultGenerator.getResultByHttp(HttpStatusConstants.INTERNAL_SERVER_ERROR);
              }

    }

    /**
     * 跳转至添加分类信息界面
     * @return
     */
    @GetMapping("/v1/category/add")
    public String addBlogConfig(){
        return "adminLayui/category-add";
    }

      @ResponseBody
    @PostMapping("/v1/category/add")
     public Result addCategory(BlogCategory blogCategory){
    blogCategory.setCreateTime(DateUtils.getLocalCurrentDate());
          boolean flag = blogCategoryService.save(blogCategory);

   if (flag){
       return ResultGenerator.getResultByHttp(HttpStatusConstants.OK);
   }
   return ResultGenerator.getResultByHttp(HttpStatusConstants.INTERNAL_SERVER_ERROR);
      }
}

