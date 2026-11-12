package sn.uadb.filter;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import sn.uadb.error.ErrorResponseWriter;
import sn.uadb.repository.ApiKeyRepository;

public class ApiKeyFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-API-Key";
    public static final String OWNER_ATTRIBUTE = "apiKeyOwner"; // réutilisé en séance 3

    private static final Logger log = LoggerFactory.getLogger(ApiKeyFilter.class);

    private final ApiKeyRepository repository;
    private final Set<String> publicPaths;

    public ApiKeyFilter(ApiKeyRepository repository, Set<String> publicPaths) {
        this.repository = repository;
        this.publicPaths = publicPaths;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // OPTIONS : préflight CORS, envoyé par le navigateur sans clé
        return "OPTIONS".equals(request.getMethod()) || publicPaths.contains(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String key = request.getHeader(HEADER);
        if (key == null || key.isBlank()) {
            reject(response, "Clé d'API manquante (en-tête " + HEADER + ").");
            return;
        }

        Optional<String> owner = repository.findOwnerByKey(key.trim());
        if (owner.isEmpty()) {
            log.warn("Clé d'API invalide depuis {}", request.getRemoteAddr()); // ne jamais loguer la clé
            reject(response, "Clé d'API invalide.");
            return;
        }

        request.setAttribute(OWNER_ATTRIBUTE, owner.get());
        chain.doFilter(request, response);
    }

    private void reject(HttpServletResponse response, String message) throws IOException {
        ErrorResponseWriter.write(response, HttpStatus.UNAUTHORIZED.value(), message);
    }
}