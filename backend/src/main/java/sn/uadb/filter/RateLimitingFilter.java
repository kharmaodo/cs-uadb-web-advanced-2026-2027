package sn.uadb.filter;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import sn.uadb.error.ErrorResponseWriter;

public class RateLimitingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);

    private static final long WINDOW_MS = 60_000;
    private static final int CLEANUP_THRESHOLD = 10_000;

    private static final class Window {
        final long start;
        final AtomicInteger count = new AtomicInteger();
        Window(long start) { this.start = start; }
    }

    private final int maxRequestsPerMinute;
    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    public RateLimitingFilter(int maxRequestsPerMinute) {
        this.maxRequestsPerMinute = maxRequestsPerMinute;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIp = httpRequest.getRemoteAddr();
        long now = System.currentTimeMillis();

        Window window = windows.compute(clientIp,
                (k, old) -> (old == null || now - old.start >= WINDOW_MS) ? new Window(now) : old);

        if (window.count.incrementAndGet() > maxRequestsPerMinute) {
            long retryAfter = Math.max(1, (window.start + WINDOW_MS - now + 999) / 1000);
            logger.warn("Rate limit exceeded for IP: {}", clientIp);

            httpResponse.setHeader("Retry-After", String.valueOf(retryAfter));
            ErrorResponseWriter.write(httpResponse, HttpStatus.TOO_MANY_REQUESTS.value(),
                    "Rate limit exceeded. Please try again later.");
            return;
        }

        cleanupIfNeeded(now);
        chain.doFilter(request, response);
    }

    private void cleanupIfNeeded(long now) {
        if (windows.size() > CLEANUP_THRESHOLD) {
            windows.values().removeIf(w -> now - w.start >= WINDOW_MS);
        }
    }
}