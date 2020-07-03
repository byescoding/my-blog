package com.bai.blogadmin.controller.admin;


import com.bai.blogadmin.constants.HttpStatusConstants;
import com.bai.blogadmin.dto.AjaxResultPage;
import com.bai.blogadmin.dto.Result;
import com.bai.blogadmin.pojo.BlogConfig;
import com.bai.blogadmin.service.BlogConfigService;
import com.bai.blogadmin.util.DateUtils;
import com.bai.blogadmin.util.ResultGenerator;
//import org.apache.coyote.http11.HttpOutputBuffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 小白
 * @since 2020-06-16
 */
@Controller
@RequestMapping("/admin")
public class BlogConfigController {
    @Autowired
    private BlogConfigService blogConfigService;

    /**
     * 跳转至系统配置画面
     * @return
     */
    @GetMapping("/v1/blogConfig")
    public String gotoBlogConfig(){
        return "adminLayui/sys-edit";
    }

    /**
     * 返回配置信息
     * @return
     */
    @ResponseBody
    @GetMapping("/v1/blogConfig/list")
    public AjaxResultPage<BlogConfig> getBlogConfig(){
        AjaxResultPage<BlogConfig> page = new AjaxResultPage<>();
        List<BlogConfig> list = blogConfigService.list();
        if (CollectionUtils.isEmpty(list)){
            ResultGenerator.getResultByHttp(HttpStatusConstants.INTERNAL_SERVER_ERROR);
            return page;
        }
        page.setData(blogConfigService.list());
        return page;
    }

    /**
     * 修改系统配置
     * @param blogConfig
     * @return
     */
    @ResponseBody
    @PostMapping("/v1/blogConfig/edit")
    public Result  updateBlogConfig(BlogConfig blogConfig){
        boolean flag = blogConfigService.updateById(blogConfig);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusConstants.OK);
        }else{
            return ResultGenerator.getResultByHttp(HttpStatusConstants.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 跳转至添加系统信息的界面
     * @return
     */

    @GetMapping("/v1/blogConfig/add")
    public String addBlogConfig(){
        return "adminLayui/sys-add";
    }

    /**
     * 添加系统信息
     * @param blogConfig
     * @return
     */
    @ResponseBody
    @PostMapping("/v1/blogConfig/add")
    public Result addBlogConfig(BlogConfig blogConfig){
        blogConfig.setCreateTime(DateUtils.getLocalCurrentDate());
        blogConfig.setUpdateTime(DateUtils.getLocalCurrentDate());
        boolean flag = blogConfigService.save(blogConfig);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusConstants.OK);
        }else{
            return ResultGenerator.getResultByHttp(HttpStatusConstants.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * s删除系统配置
     * @param configField
     * @return
     */
    @ResponseBody
    @PostMapping("/v1/blogConfig/del")
    public Result delConfig(@RequestParam String configField){
        boolean flag= blogConfigService.removeById(configField);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusConstants.OK);
        }else{
            return ResultGenerator.getResultByHttp(HttpStatusConstants.INTERNAL_SERVER_ERROR);
        }
    }

}

