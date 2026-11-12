package sn.uadb.filter;

import java.io.IOException;
import java.time.Instant;
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

public class RateLimitingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);

    private static final int MAX_REQUESTS_PER_MINUTE = 2; // valeur de test
    private static final long WINDOW_MS = 60_000;
    private static final int CLEANUP_THRESHOLD = 10_000;

    private static final class Window {
        final long start;
        final AtomicInteger count = new AtomicInteger();
        Window(long start) { this.start = start; }
    }

    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIp = httpRequest.getRemoteAddr();
        long now = System.currentTimeMillis();

        // Nouvelle fenêtre si aucune n'existe ou si l'ancienne a expiré (opération atomique)
        Window window = windows.compute(clientIp,
                (k, old) -> (old == null || now - old.start >= WINDOW_MS) ? new Window(now) : old);

        if (window.count.incrementAndGet() > MAX_REQUESTS_PER_MINUTE) {
            long retryAfter = Math.max(1, (window.start + WINDOW_MS - now + 999) / 1000);
            logger.warn("Rate limit exceeded for IP: {}", clientIp);

            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.setHeader("Retry-After", String.valueOf(retryAfter));
            httpResponse.setContentType("application/json");
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.getWriter().write(String.format(
                    "{\"error\":%d,\"message\":\"Rate limit exceeded. Please try again later.\",\"timestamp\":\"%s\"}",
                    HttpStatus.TOO_MANY_REQUESTS.value(), Instant.now()));
            return;
        }

        cleanupIfNeeded(now);
        chain.doFilter(request, response);
    }

    // Évite que la map grossisse indéfiniment
    private void cleanupIfNeeded(long now) {
        if (windows.size() > CLEANUP_THRESHOLD) {
            windows.values().removeIf(w -> now - w.start >= WINDOW_MS);
        }
    }
}