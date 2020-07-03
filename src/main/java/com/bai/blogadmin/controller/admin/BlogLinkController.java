package com.bai.blogadmin.controller.admin;


import com.bai.blogadmin.constants.HttpStatusConstants;
import com.bai.blogadmin.constants.LinkConstants;
import com.bai.blogadmin.dto.AjaxPutPage;
import com.bai.blogadmin.dto.AjaxResultPage;
import com.bai.blogadmin.dto.Result;
import com.bai.blogadmin.pojo.BlogLink;
import com.bai.blogadmin.service.BlogLinkService;
import com.bai.blogadmin.util.DateUtils;
import com.bai.blogadmin.util.ResultGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.swing.text.StyledEditorKit;
import java.util.ArrayList;

/**
 * <p>
 * 友情链接表 前端控制器
 * </p>
 *
 * @author 小白
 * @since 2020-06-16
 */
@Controller
@RequestMapping("/admin")
public class BlogLinkController {

    @Autowired
    private BlogLinkService blogLinkService;

    /**
     *跳转至连接页面
     */
    @GetMapping("/v1/linkType")
    public String gotoLink(){
        return "adminLayui/link-list";
    }

    /**
     * link 的类型
     * @return
     */
    @ResponseBody
    @GetMapping("/v1/linkType/list")
    public Result linkTypeList(){
        ArrayList<BlogLink> blogLinks = new ArrayList<>();
        blogLinks.add(new BlogLink().setLinkType(LinkConstants.LINK_TYPE_FRIENDSHIP.getLinkTypeId())
                .setLinkName(LinkConstants.LINK_TYPE_FRIENDSHIP.getLinkTypeName()));
        blogLinks.add(new BlogLink().setLinkType(LinkConstants.LINK_TYPE_RECOMMEND.getLinkTypeId())
                .setLinkName(LinkConstants.LINK_TYPE_RECOMMEND.getLinkTypeName()));
        blogLinks.add(new BlogLink().setLinkType(LinkConstants.LINK_TYPE_PRIVATE.getLinkTypeId())
                .setLinkName(LinkConstants.LINK_TYPE_PRIVATE.getLinkTypeName()));
        return ResultGenerator.getResultByHttp(HttpStatusConstants.OK,blogLinks);
    }

    /**
     * link  分页功能
     * @param ajaxPutPage
     * @param condition
     * @return
     */
    @ResponseBody
    @GetMapping("/v1/link/paging")
    public AjaxResultPage<BlogLink> getLinkList(AjaxPutPage<BlogLink> ajaxPutPage,BlogLink condition){
        QueryWrapper<BlogLink> queryWrapper = new QueryWrapper<>(condition);
        queryWrapper.lambda()
                .orderByAsc(BlogLink::getLinkRank);
        Page<BlogLink> page = ajaxPutPage.putPageToPage();
        blogLinkService.page(page,queryWrapper);
        AjaxResultPage<BlogLink> result = new AjaxResultPage<>();
        result.setData(page.getRecords());
        result.setCount(page.getTotal());
       return result;
    }

    /**
     * 改变标签的状态
     * @param blogLink
     * @return
     */
    @ResponseBody
    @PostMapping("/v1/link/isDel")
    public Result updateLinkStatus(BlogLink blogLink){
        boolean flag = blogLinkService.updateById(blogLink);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusConstants.OK);
        }else {
            return ResultGenerator.getResultByHttp(HttpStatusConstants.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 删除link
     * @param linkId
     * @return
     */
    @ResponseBody
    @PostMapping("/v1/link/clear")
   public Result clearLink(Integer linkId){
        boolean flag = blogLinkService.removeById(linkId);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusConstants.OK);
        }else{
            return ResultGenerator.getResultByHttp(HttpStatusConstants.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 跳转到link的编辑界面
     * @param linkId
     * @param model
     * @return
     */
    @GetMapping("/v1/link/edit")
    public String editLink(Integer linkId, Model model){
        if (linkId != null){
            BlogLink blogLink = blogLinkService.getById(linkId);
            model.addAttribute("blogLink",blogLink);
        }
        return "adminLayui/link-edit";
    }

    /**
     * 编辑link
     * @param blogLink
     * @return
     */
    @ResponseBody
    @PostMapping("/v1/link/edit")
    public Result updateAndSaveLink(BlogLink blogLink){
        blogLink.setCreateTime(DateUtils.getLocalCurrentDate());
        boolean flag = blogLinkService.save(blogLink);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusConstants.OK);
        }else {
            return ResultGenerator.getResultByHttp(HttpStatusConstants.INTERNAL_SERVER_ERROR);
        }
    }

}

