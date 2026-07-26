package com.jayaram.spendwise_service.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class ApiRequestMetricsFilter extends OncePerRequestFilter {

    private static final Pattern UUID_PATTERN =
            Pattern.compile("(?i)^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

    private final MeterRegistry meterRegistry;

    public ApiRequestMetricsFilter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String contextPath = request.getContextPath() == null ? "" : request.getContextPath();
        String uri = request.getRequestURI();
        if (uri == null) {
            return true;
        }
        if (uri.startsWith(contextPath + "/actuator")) {
            return true;
        }
        return uri.startsWith(contextPath + "/error");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long startNanos = System.nanoTime();
        int status = 200;
        boolean success = false;
        try {
            filterChain.doFilter(request, response);
            status = response.getStatus();
            success = true;
        } catch (Exception ex) {
            status = 500;
            recordMetrics(request, status, System.nanoTime() - startNanos);
            throw ex;
        } finally {
            if (success) {
                recordMetrics(request, status, System.nanoTime() - startNanos);
            }
        }
    }

    private void recordMetrics(HttpServletRequest request, int status, long durationNanos) {
        String method = request.getMethod() == null ? "UNKNOWN" : request.getMethod();
        String uri = sanitizeUri(request);
        String statusTag = String.valueOf(status);

        Counter.builder("app_api_requests_total")
                .description("Total number of API requests handled by this service (excluding actuator).")
                .tags("method", method, "uri", uri, "status", statusTag)
                .register(meterRegistry)
                .increment();

        Timer.builder("app_api_request_duration")
                .description("API request duration in seconds (excluding actuator).")
                .tags("method", method, "uri", uri, "status", statusTag)
                .publishPercentileHistogram()
                .register(meterRegistry)
                .record(durationNanos, TimeUnit.NANOSECONDS);
    }

    private String sanitizeUri(HttpServletRequest request) {
        String contextPath = request.getContextPath() == null ? "" : request.getContextPath();
        String raw = request.getRequestURI() == null ? "" : request.getRequestURI();
        String path = raw.startsWith(contextPath) ? raw.substring(contextPath.length()) : raw;
        if (path.isBlank()) {
            return "/";
        }

        String[] parts = path.split("/");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            sb.append('/');
            if (part.chars().allMatch(Character::isDigit)) {
                sb.append("{id}");
            } else if (UUID_PATTERN.matcher(part).matches()) {
                sb.append("{uuid}");
            } else {
                sb.append(part);
            }
        }
        return sb.length() == 0 ? "/" : sb.toString();
    }
}

