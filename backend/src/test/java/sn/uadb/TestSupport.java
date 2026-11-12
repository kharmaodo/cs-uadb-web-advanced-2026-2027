package sn.uadb;

import org.springframework.test.web.servlet.request.RequestPostProcessor;

public final class TestSupport {

    public static final String API_KEY = "test-key";
    public static final String API_KEY_HEADER = "X-API-Key";

    private TestSupport() {}

    /** Simule un appel venant d'une adresse IP donnée (le rate limiter compte par IP). */
    public static RequestPostProcessor fromIp(String ip) {
        return request -> {
            request.setRemoteAddr(ip);
            return request;
        };
    }
}
