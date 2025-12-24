package com.li.lostbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class EruptWebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. 映射 Erupt 后台静态资源
        registry.addResourceHandler("/erupt-web/**")
                .addResourceLocations("classpath:/META-INF/resources/erupt-web/");

        // 2. 映射 Knife4j/Swagger 接口文档 (如果你用了的话)
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}