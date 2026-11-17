package sn.uadb.config;

import java.util.Set;
import sn.uadb.filter.ApiKeyFilter;
import sn.uadb.repository.ApiKeyRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import sn.uadb.filter.MaintenanceFilter;
import sn.uadb.filter.RateLimitingFilter;
import sn.uadb.filter.RequestIdFilter;
import sn.uadb.filter.RequestLoggingFilter;
import sn.uadb.filter.SecurityHeadersFilter;

@Configuration
public class FilterConfig {

    private static final String[] API = {"/api/*"};

    @Bean
    public FilterRegistrationBean<RequestIdFilter> requestIdFilterRegistration() {
        return register(new RequestIdFilter(), 1);
    }

    @Bean
    public FilterRegistrationBean<RequestLoggingFilter> requestLoggingFilterRegistration() {
        return register(new RequestLoggingFilter(), 2);
    }

    @Bean
    public FilterRegistrationBean<SecurityHeadersFilter> securityHeadersFilterRegistration() {
        return register(new SecurityHeadersFilter(), 3);
    }

    @Bean
    public FilterRegistrationBean<MaintenanceFilter> maintenanceFilterRegistration(
            @Value("${app.maintenance.enabled:false}") boolean enabled) {
        return register(new MaintenanceFilter(enabled), 4);
    }

    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilterRegistration(
            @Value("${app.rate-limit.max-requests-per-minute:100}") int max) {
        return register(new RateLimitingFilter(max), 5);
    }




 

        @Bean
        public FilterRegistrationBean<ApiKeyFilter> apiKeyFilterRegistration(ApiKeyRepository repository) {
            return register(new ApiKeyFilter(repository, Set.of("/api/hello")), 6);
        }

    private static <T extends jakarta.servlet.Filter> FilterRegistrationBean<T> register(T filter, int order) {
        FilterRegistrationBean<T> bean = new FilterRegistrationBean<>(filter);
        bean.addUrlPatterns(API);
        bean.setOrder(order);
        return bean;
    }
}