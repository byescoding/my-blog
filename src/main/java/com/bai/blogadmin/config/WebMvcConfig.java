package com.bai.blogadmin.config;

import com.bai.blogadmin.constants.UploadConstants;
import com.bai.blogadmin.intercept.AdminLoginInterceptor;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private AdminLoginInterceptor adminLoginInterceptor;

    /**
     * 配置拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
      registry.addInterceptor(adminLoginInterceptor)
              .addPathPatterns("/admin/**")
              .excludePathPatterns("/admin/v1/login")
              .excludePathPatterns("/admin/v1/reload")
              .excludePathPatterns("/admin/dist/**")
              .excludePathPatterns("/admin/plugins/**")
              .excludePathPatterns("/X-admin/**");



    }


    /**
     * 添加文件路径映射
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/authorImg/**").addResourceLocations("file:" + UploadConstants.UPLOAD_AUTHOR_IMG);
        registry.addResourceHandler("/upload/**").addResourceLocations("file:" + UploadConstants.FILE_UPLOAD_DIC);
    }
}
