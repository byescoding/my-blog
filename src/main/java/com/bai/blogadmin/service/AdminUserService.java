package com.bai.blogadmin.service;

import com.bai.blogadmin.pojo.AdminUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 后台管理员信息表 服务类
 * </p>
 *
 * @author 小白
 * @since 2020-06-16
 */
public interface AdminUserService extends IService<AdminUser> {
    boolean vaildPasseord(Integer userId,String oldPwd);
    boolean updateUserInfo(AdminUser adminUser);
}
