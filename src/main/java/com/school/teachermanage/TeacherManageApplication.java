package com.school.teachermanage;

import com.school.teachermanage.filter.JwtFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.List;

/**
 * 启动类
 *
 */
@EnableScheduling
@SpringBootApplication
public class TeacherManageApplication {

    @Bean
    public FilterRegistrationBean jwtFilter() {
        //注意前后端拦截路径不能重复
        List<String> adminUrls = new ArrayList<>();
        adminUrls.add("/back/*");
        adminUrls.add("/config/*");

        List<String> userUrls = new ArrayList<>();
        userUrls.add("/front/*");
        userUrls.add("/userApi/*");
        userUrls.add("/scoreRecordApi/*");
        userUrls.add("/bankAccountApi/*");
        userUrls.add("/balanceRecordApi/*");
        userUrls.add("/aliPayAccountApi/*");

        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new JwtFilter());
        for (String adminUrl : adminUrls) {
            registrationBean.addUrlPatterns(adminUrl);
        }
        for (String userUrl : userUrls) {
            registrationBean.addUrlPatterns(userUrl);
        }
        return registrationBean;
    }

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {

        return (container -> {
            ErrorPage page404 = new ErrorPage(HttpStatus.NOT_FOUND, "/404.html");
            ErrorPage page500 = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html");
            container.addErrorPages(page404, page500);
        });
    }

    public static void main(String[] args) {
        SpringApplication.run(TeacherManageApplication.class, args);
    }
}
