package com.bai.blogadmin.controller.admin;


import com.bai.blogadmin.constants.BlogStatusConstants;
import com.bai.blogadmin.constants.HttpStatusConstants;
import com.bai.blogadmin.constants.SessionConstants;
import com.bai.blogadmin.constants.SysConfigConstants;
import com.bai.blogadmin.dto.Result;
import com.bai.blogadmin.pojo.*;
import com.bai.blogadmin.service.*;
import com.bai.blogadmin.util.MD5Utils;
import com.bai.blogadmin.util.ResultGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.management.Query;
import javax.servlet.http.HttpSession;

/**
 * <p>
 * 后台管理员信息表 前端控制器
 * </p>
 *
 * @author 小白
 * @since 2020-06-16
 */
@Controller
@RequestMapping("/admin")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private BlogConfigService blogConfigService;

    @Autowired
    private BlogInfoService blogInfoService;
    @Autowired
    private BlogTagService blogTagService;
    @Autowired
    private BlogCategoryService blogCategoryService;
    @Autowired
    private BlogCommentService blogCommentService;

    @Autowired
    private BlogLinkService blogLinkService;


    /**
     * 跳转到登录界面
     */
    @GetMapping("/v1/login")
    public String  toLogin(){
        return "adminLayui/login";
    }


    /**
     * 用户登录
     */
    @RequestMapping("/v1/login")
    @ResponseBody
    public Result login(String password , String username, HttpSession session){
         //判断username password  是否为空  如果为空的话就抛出异常
        if (StringUtils.isEmpty(username) && StringUtils.isEmpty(password)){
            return ResultGenerator.getResultByHttp(HttpStatusConstants.BAD_REQUEST);
        }
        //添加构造器
        QueryWrapper<AdminUser> adminUserQueryWrapper = new QueryWrapper<>(new AdminUser().setLoginUserName(username)
                //s使用MD5转换密码
                .setLoginPassword(MD5Utils.MD5Encode(password, "UTF-8")));

        //获取对应的用户信息
        AdminUser adminUser = adminUserService.getOne(adminUserQueryWrapper);

        if (adminUser!=null){
            session.setAttribute(SessionConstants.LOGIN_USER,adminUser.getNickName());
            session.setAttribute(SessionConstants.LOGIN_USER_ID,adminUser.getAdminUserId());
            session.setAttribute(SessionConstants.LOGIN_USER_NAME,adminUser.getLoginUserName());
            session.setAttribute(SessionConstants.AUTHOR_IMG,blogConfigService.getById(
                    //设置用户头像
                    SysConfigConstants.SYS_AUTHOR_IMG.getConfigField()
            ));
            //返回登录信息
            return ResultGenerator.getResultByHttp(HttpStatusConstants.OK,"/admin/v1/index");
        }else {
            //如果为空的话就返回请求错误信息
            return ResultGenerator.getResultByHttp(HttpStatusConstants.UNAUTHORIZED);
        }
    }



    /*
    用户注销登录
     */
    @RequestMapping("/v1/logout")
    public String logout(HttpSession session){
        //清除session
        session.invalidate();
        return "adminLayui/login";
    }

    /**
     * 进入欢迎界面
     */
    @RequestMapping("/v1/welcome")
    public String welcome(){
        return "adminLayui/welcome";
    }


    /**
     * 返回用户信息界面
     *
     */
    @GetMapping("/v1/userInfo")
    public String getUserInfo(){
        return "adminLayui/userInfo-edit";
    }

    /**
     * 更改用户密码
     */

    @ResponseBody
    @RequestMapping("/v1/password")
     public Result validatePassword(String olgPwd,HttpSession session){
         //从session获取原来的密码进行比较
       Integer userId = (Integer)session.getAttribute(SessionConstants.LOGIN_USER_ID);
        boolean flag = adminUserService.vaildPasseord(userId, olgPwd);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusConstants.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusConstants.BAD_REQUEST);
    }


    /**
     * 返回首页相关数据
     *
     */

    @GetMapping("/v1/index")
    public String index(HttpSession session){
        session.setAttribute("categoryCount", blogCategoryService.count(
                new QueryWrapper<BlogCategory>().lambda().eq(BlogCategory::getIsDeleted,
                        BlogStatusConstants.ZERO)
        ));
        session.setAttribute("blogCount", blogInfoService.count(
                new QueryWrapper<BlogInfo>().lambda().eq(BlogInfo::getIsDeleted,
                        BlogStatusConstants.ZERO)
        ));
        session.setAttribute("linkCount", blogLinkService.count(
                new QueryWrapper<BlogLink>().lambda().eq(BlogLink::getIsDeleted,
                        BlogStatusConstants.ZERO)
        ));
        session.setAttribute("tagCount", blogTagService.count(
                new QueryWrapper<BlogTag>().lambda().eq(BlogTag::getIsDeleted,
                        BlogStatusConstants.ZERO)
        ));
        session.setAttribute("commentCount", blogCommentService.count(
                new QueryWrapper<BlogComment>().lambda().eq(BlogComment::getIsDeleted,
                        BlogStatusConstants.ZERO)
        ));
        session.setAttribute("sysList",blogConfigService.list());
        return "adminLayui/index";
    }

    /**
     * 修改用户信息，成功以后清空sessionb并跳转登录
     *
     */
    @ResponseBody
    @PostMapping("/v1/userInfo")
    public Result userInfoUpdate(HttpSession session,String userName, String newPwd,
                                 String nickName) {
        if (StringUtils.isEmpty(newPwd) || StringUtils.isEmpty(nickName)) {
            return ResultGenerator.getResultByHttp(HttpStatusConstants.BAD_REQUEST);
        }
        Integer loginUserId = (int) session.getAttribute(SessionConstants.LOGIN_USER_ID);
        AdminUser adminUser = new AdminUser()
                .setAdminUserId(loginUserId)
                .setLoginUserName(userName)
                .setNickName(nickName)
                .setLoginPassword(MD5Utils.MD5Encode(newPwd, "UTF-8"));
        if (adminUserService.updateUserInfo(adminUser)) {
            //修改成功后清空session中的数据，前端控制跳转至登录页
            return ResultGenerator.getResultByHttp(HttpStatusConstants.OK,"/admin/v1/logout");
        } else {
            return ResultGenerator.getResultByHttp(HttpStatusConstants.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 检查是否已经登录
     */
    @ResponseBody
    @GetMapping("/v1/reload")
    public boolean reload(HttpSession session){
        Integer userId = (Integer) session.getAttribute(SessionConstants.LOGIN_USER_ID);
        return userId != null && userId != 0;
    }


}

