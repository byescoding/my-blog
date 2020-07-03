package com.bai.blogadmin.service.impl;

import com.bai.blogadmin.pojo.AdminUser;
import com.bai.blogadmin.dao.AdminUserMapper;
import com.bai.blogadmin.service.AdminUserService;
import com.bai.blogadmin.util.MD5Utils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 后台管理员信息表 服务实现类
 * </p>
 *
 * @author 小白
 * @since 2020-06-16
 */
@Service
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements AdminUserService {

    @Autowired
    private AdminUserMapper adminUserMapper;


    //密码验证


    @Override
    public boolean vaildPasseord(Integer userId, String oldPwd) {
        QueryWrapper<AdminUser> queryWrapper = new QueryWrapper<AdminUser>(
                new AdminUser().setAdminUserId(userId)
                        .setLoginPassword(MD5Utils.MD5Encode(oldPwd,"UTF-8"))
        );
        AdminUser adminUser = adminUserMapper.selectOne(queryWrapper);
        return !StringUtils.isEmpty(adminUser);

    }

    //更新用户信息

    @Transactional
    @Override
    public boolean updateUserInfo(AdminUser adminUser) {
        return SqlHelper.retBool(adminUserMapper.updateById(adminUser));
    }
}
