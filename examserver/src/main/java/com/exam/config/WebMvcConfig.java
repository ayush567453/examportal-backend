package com.exam.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${tenant.logo.path:/home/ayush/uploads/logos}")
    private String logoPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve logo files from the external upload directory at /logos/**
        registry.addResourceHandler("/logos/**")
                .addResourceLocations("file:" + logoPath + "/");
    }
}
