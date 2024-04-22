package com.mintyfinance.config;

import com.mintyfinance.config.interceptor.MyModelInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final MyModelInterceptor myModelInterceptor;

    public WebConfig(MyModelInterceptor myModelInterceptor) {
        this.myModelInterceptor = myModelInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(myModelInterceptor);
    }
}

