package com.davengeo.etag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@SpringBootApplication
public class EtagDemoApplication {

    @Bean
    public FilterRegistrationBean someFilterRegistration(ShallowEtagHeaderFilter shallowEtagHeaderFilter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(shallowEtagHeaderFilter);
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
        return new ShallowEtagHeaderFilter();
    }

    public static void main(String[] args) {
        SpringApplication.run(EtagDemoApplication.class, args);
    }
}
