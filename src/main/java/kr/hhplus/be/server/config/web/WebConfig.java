package kr.hhplus.be.server.config.web;

import kr.hhplus.be.server.support.interceptor.ExceptionLoggingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ExceptionLoggingInterceptor())
                .addPathPatterns("/**");
    }
}
