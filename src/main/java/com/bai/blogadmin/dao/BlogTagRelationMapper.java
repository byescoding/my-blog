package com.bai.blogadmin.dao;

import com.bai.blogadmin.pojo.BlogTagRelation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 博客跟标签的关系表 Mapper 接口
 * </p>
 *
 * @author 小白
 * @since 2020-06-16
 */
@Repository
public interface BlogTagRelationMapper extends BaseMapper<BlogTagRelation> {

}
