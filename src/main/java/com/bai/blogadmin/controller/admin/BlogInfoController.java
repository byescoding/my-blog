package com.bai.blogadmin.controller.admin;


import com.bai.blogadmin.constants.BlogStatusConstants;
import com.bai.blogadmin.constants.HttpStatusConstants;
import com.bai.blogadmin.constants.UploadConstants;
import com.bai.blogadmin.dto.AjaxPutPage;
import com.bai.blogadmin.dto.AjaxResultPage;
import com.bai.blogadmin.dto.Result;
import com.bai.blogadmin.pojo.BlogInfo;
import com.bai.blogadmin.pojo.BlogTagRelation;
import com.bai.blogadmin.service.BlogCommentService;
import com.bai.blogadmin.service.BlogInfoService;
import com.bai.blogadmin.service.BlogTagRelationService;
import com.bai.blogadmin.util.DateUtils;
import com.bai.blogadmin.util.MyBlogUtils;
import com.bai.blogadmin.util.ResultGenerator;
import com.bai.blogadmin.util.UploadFileUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 博客信息表 前端控制器
 * </p>
 *
 * @author 小白
 * @since 2020-06-16
 */
@Controller
@RequestMapping("/admin")
public class BlogInfoController {

    @Autowired
    private BlogInfoService blogInfoService;
    @Autowired
    private BlogTagRelationService blogTagRelationService;


    @GetMapping("/v1/blog/edit")
    public String gotoBlogEdit(@RequestParam(required = false) Long blogId, Model model){
        if (blogId != null){
            BlogInfo blogInfo = blogInfoService.getById(blogId);
//            QueryWrapper<BlogTagRelation> queryWrapper = new QueryWrapper<>();
            List<BlogTagRelation> list = blogTagRelationService.list(
                    new QueryWrapper<BlogTagRelation>()
                            .lambda()
                            .eq(BlogTagRelation::getBlogId,blogId)
            );
            List<Integer> tags = null;
            if (!CollectionUtils.isEmpty(list)){
                tags = list.stream().map(
                        blogTagRelation -> blogTagRelation.getTagId())
                        .collect(Collectors.toList());
            }
            model.addAttribute("blogTags",tags);
            model.addAttribute("blogInfo",blogInfo);
        }
        return "adminLayui/blog-edit";
    }


    /**
     *
     * @return
     * @date
     */
    @GetMapping("/v1/blog")
    public String gotoBlogList(){
        return "adminLayui/blog-list";
    }

    /**
     * 保存文章的图片
     * @param request
     * @param file
     * @return
     */

    @ResponseBody
    @PostMapping("/v1/blog/uploadFile")
    public Map<String,Object> uploadFileByEditormd(HttpServletRequest request,
       @RequestParam(name = "editormd-image-file", required = true)
        MultipartFile file){
        String suffixName = UploadFileUtils.getSuffixName(file);

        //生成文件名称通用方法
        String newFileName = UploadFileUtils.getNewFileName(suffixName);
        File fileDirectory = new File(UploadConstants.FILE_UPLOAD_DIC);
        //创建文件
        File destFile = new File(UploadConstants.FILE_UPLOAD_DIC + newFileName);

        HashMap<String, Object> result = new HashMap<>();

        try {
            if (!fileDirectory.exists()){
                if (!fileDirectory.mkdirs()){
                    throw new IOException("文件夹创建失败路径为:"+fileDirectory);
                }
            }
            file.transferTo(destFile);
         String fileUrl = MyBlogUtils.getHost(new URI(request.getRequestURI()+""))+
                    UploadConstants.FILE_SQL_DIC+newFileName;

            result.put("success", 1);
            result.put("message","上传成功");
            result.put("url",fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
            result.put("success", 0);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            result.put("success", 0);
        }

        return result;
    }

    /**
     * 保存文章内容
     * @param blogTagIds
     * @param blogInfo
     * @return
     */
    @ResponseBody
    @PostMapping("/v1/blog/edit")
    public Result saveBlog(@RequestParam("blogTagIds[]") List<Integer> blogTagIds, BlogInfo blogInfo){
        if (CollectionUtils.isEmpty(blogTagIds) || ObjectUtils.isEmpty(blogInfo)){
             return ResultGenerator.getResultByHttp(HttpStatusConstants.BAD_REQUEST);
        }
        blogInfo.setCreateTime(DateUtils.getLocalCurrentDate());
        blogInfo.setUpdateTime(DateUtils.getLocalCurrentDate());
        if (blogInfoService.saveOrUpdate(blogInfo)){
            blogTagRelationService.removeAndsaveBatch(blogTagIds,blogInfo);
            return ResultGenerator.getResultByHttp(HttpStatusConstants.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusConstants.INTERNAL_SERVER_ERROR);
    }

    /**
     * 文章分页列表
     * @param ajaxPutPage 分页参数
     * @param condition
     * @return
     * @date
     */
    @ResponseBody
    @GetMapping("/v1/blog/list")
    public AjaxResultPage<BlogInfo> getContractList(AjaxPutPage<BlogInfo> ajaxPutPage, BlogInfo condition){
        QueryWrapper<BlogInfo> queryWrapper = new QueryWrapper<>(condition);
        queryWrapper.lambda().orderByDesc(BlogInfo::getUpdateTime);
        Page<BlogInfo> page = ajaxPutPage.putPageToPage();
        blogInfoService.page(page,queryWrapper);
        AjaxResultPage<BlogInfo> result = new AjaxResultPage<>();
        result.setData(page.getRecords());
        result.setCount(page.getTotal());
        return result;
    }

    /**
     * 修改博客的部分状态相关信息
     * @param blogInfo
     * @return
     */
  @ResponseBody
    @PostMapping("/v1/blog/blogStatus")
    public Result updateBlogStatus(BlogInfo blogInfo){
        blogInfo.setUpdateTime(DateUtils.getLocalCurrentDate());
      boolean flag = blogInfoService.updateById(blogInfo);
      if (flag){
          return ResultGenerator.getResultByHttp(HttpStatusConstants.OK);
      }
      return  ResultGenerator.getResultByHttp(HttpStatusConstants.INTERNAL_SERVER_ERROR);
  }

    /**
     * 修改文章的删除状态为已删除
     * @param blogId
     * @return c
     * @date
     */
    @ResponseBody
    @PostMapping("/v1/blog/delete")
    public Result deleteBlog(@RequestParam Long blogId){
        BlogInfo blogInfo = new BlogInfo()
                .setBlogId(blogId)
                .setIsDeleted(BlogStatusConstants.ONE)
                .setUpdateTime(DateUtils.getLocalCurrentDate());
        boolean flag = blogInfoService.updateById(blogInfo);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusConstants.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusConstants.INTERNAL_SERVER_ERROR);
    }


    /**
     * 清除文章
     * @param blogId
     * @return
     */
    @ResponseBody
    @PostMapping("/v1/blog/clear")
    public Result clearBlog(@RequestParam Long blogId){
        if (blogInfoService.clearBlogInfo(blogId)){
            return ResultGenerator.getResultByHttp(HttpStatusConstants.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusConstants.INTERNAL_SERVER_ERROR);
    }

    /**
     * 还原文章
     * @param blogId
     * @return
     */
    @ResponseBody
    @PostMapping("/v1/blog/restore")
    public Result restoreBlog(@RequestParam Long blogId){
        BlogInfo blogInfo = new BlogInfo()
                .setBlogId(blogId)
                .setIsDeleted(BlogStatusConstants.ZERO)
                .setUpdateTime(DateUtils.getLocalCurrentDate());
        boolean flag = blogInfoService.updateById(blogInfo);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusConstants.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusConstants.INTERNAL_SERVER_ERROR);
    }

    /**
     * 返回博客信息列表
     * @return
     */

    @ResponseBody
    @GetMapping("v1/blog/select")
    public List<BlogInfo> getBlogInfoSelect(){
        return blogInfoService.list();
    }



}

