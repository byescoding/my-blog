package com.bai.blogadmin.service;

import com.bai.blogadmin.pojo.BlogConfig;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 小白
 * @since 2020-06-16
 */
public interface BlogConfigService extends IService<BlogConfig> {
    Map<String, String> getAllConfigs();
}
