package sn.uadb.error;

import java.io.IOException;
import java.time.Instant;

import jakarta.servlet.http.HttpServletResponse;

public final class ErrorResponseWriter {

    private ErrorResponseWriter() {}

    public static void write(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format(
                "{\"error\":%d,\"message\":\"%s\",\"timestamp\":\"%s\"}",
                status, escape(message), Instant.now()));
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}