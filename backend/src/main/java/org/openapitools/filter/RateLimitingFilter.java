package org.openapitools.filter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;     
import org.springframework.http.HttpStatus;
/**
 * 
 * RateLimitingFilter is a servlet filter that implements rate limiting for incoming requests.
 */
public class RateLimitingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);

    private static final int MAX_REQUESTS_PER_MINUTE = 2; // Maximum requests allowed per minute

    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();

    @Override 
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String clientIp = httpRequest.getRemoteAddr();

        // Increment the request count for the client IP
        AtomicInteger requestCount = requestCounts.computeIfAbsent(clientIp, k -> new AtomicInteger(0));
        int currentCount = requestCount.incrementAndGet();

        if (currentCount > MAX_REQUESTS_PER_MINUTE) {
            logger.warn("Rate limit exceeded for IP: {}", clientIp);
            ((HttpServletResponse) response).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
           ((HttpServletResponse) response).getWriter().write("Rate limit exceeded. Please try again later.");
            return;
        }

        // Proceed with the filter chain
        chain.doFilter(request, response);

        // Reset the request count after a minute
        resetRequestCountAfterDelay(clientIp);
    }
    public void resetRequestCountAfterDelay(String clientIp) {
        new Thread(() -> {
            try {
                Thread.sleep(60000); // Wait for 1 minute
                requestCounts.remove(clientIp);
            } catch (InterruptedException e) {
                logger.error("Error resetting request count for IP: {}", clientIp, e);
            }
        }).start();
    }
}