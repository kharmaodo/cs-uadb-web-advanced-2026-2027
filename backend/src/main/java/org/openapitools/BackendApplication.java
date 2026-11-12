package org.openapitools ; 

import sn.uadb.filter.RateLimitingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean; 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"org.openapitools", "sn.uadb"})
public  class BackendApplication {

    public static void main(String[] args ){
       SpringApplication.run(BackendApplication.class,args) ;

    }

    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilter() {
        FilterRegistrationBean<RateLimitingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RateLimitingFilter());
        registrationBean.addUrlPatterns("/api/*"); // Apply the filter to all URLs
        return registrationBean;
    }
}



