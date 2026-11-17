package sn.uadb.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import sn.uadb.error.ErrorResponseWriter;

public class MaintenanceFilter extends OncePerRequestFilter {

    private final boolean enabled;

    public MaintenanceFilter(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (enabled) {
            response.setHeader("Retry-After", "300");
            ErrorResponseWriter.write(response, HttpStatus.SERVICE_UNAVAILABLE.value(),
                    "Service en maintenance. Veuillez réessayer plus tard.");
            return;
        }
        chain.doFilter(request, response);
    }
}