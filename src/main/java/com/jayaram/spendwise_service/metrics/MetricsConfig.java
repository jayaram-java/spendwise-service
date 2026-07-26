package com.jayaram.spendwise_service.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class MetricsConfig {

    @Bean
    public FilterRegistrationBean<ApiRequestMetricsFilter> apiRequestMetricsFilter(MeterRegistry meterRegistry) {
        FilterRegistrationBean<ApiRequestMetricsFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ApiRequestMetricsFilter(meterRegistry));
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}

