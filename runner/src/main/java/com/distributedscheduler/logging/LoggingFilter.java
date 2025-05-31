package com.distributedscheduler.logging;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class LoggingFilter implements Filter {

    /***
     * 🔍 Purpose:
     * Assign a unique traceId (UUID) to every request so all logs related to that
     * request can be traced using this ID.
     *
     ***/

    private static final String CORRELATION_ID = "traceId";

    @Override
    public void doFilter(jakarta.servlet.ServletRequest request,
            jakarta.servlet.ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        String traceId = UUID.randomUUID().toString();
        MDC.put(CORRELATION_ID, traceId);
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        MDC.put("method", httpRequest.getMethod());
        MDC.put("path", httpRequest.getRequestURI());

        try {
            chain.doFilter(request, response);
        } finally {
            // MDC.remove(CORRELATION_ID);
            MDC.clear(); // Clean up to avoid leaking context
        }
    }
}
