package com.app.chatserver.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class MultipartConfig {
    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.parse("4096KB"));
        factory.setMaxRequestSize(DataSize.parse("4096KB"));
        return factory.createMultipartConfig();
    }
}
