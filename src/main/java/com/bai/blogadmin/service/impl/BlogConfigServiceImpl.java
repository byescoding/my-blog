package com.bai.blogadmin.service.impl;

import com.bai.blogadmin.pojo.BlogConfig;
import com.bai.blogadmin.dao.BlogConfigMapper;
import com.bai.blogadmin.service.BlogConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 小白
 * @since 2020-06-16
 */
@Service
public class BlogConfigServiceImpl extends ServiceImpl<BlogConfigMapper, BlogConfig> implements BlogConfigService {


    @Autowired
    private BlogConfigMapper blogConfigMapper;
    @Override
    public Map<String, String> getAllConfigs() {
        List<BlogConfig> blogConfigs = blogConfigMapper.selectList(null);

        return blogConfigs.stream().collect(Collectors.toMap(
                BlogConfig::getConfigField,BlogConfig::getConfigValue
        ));
    }
}
