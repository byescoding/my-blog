package com.bai.blogadmin.dao;

import com.bai.blogadmin.pojo.AdminUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 后台管理员信息表 Mapper 接口
 * </p>
 *
 * @author 小白
 * @since 2020-06-16
 */
@Mapper
@Repository
public interface AdminUserMapper extends BaseMapper<AdminUser> {

}
