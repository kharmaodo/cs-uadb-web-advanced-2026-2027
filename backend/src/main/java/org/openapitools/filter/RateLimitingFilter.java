package org.openapitools.filter;
/**
 * 
 * RateLimitingFilter is a servlet filter that implements rate limiting for incoming requests.
 */
@Component
public class RateLimitingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);

    private static final int MAX_REQUESTS_PER_MINUTE = 5; // Maximum requests allowed per minute

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
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
            return;
        }

        // Proceed with the filter chain
        chain.doFilter(request, response);

        // Reset the request count after a minute
        resetRequestCountAfterDelay(clientIp);
    }
}