package com.akman.camel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

/**
 * Enable AOP specific annotations.
 */
@Configuration
/**
 * Required for the user of {@link org.springframework.retry.annotation.Retryable}.
 */
@EnableRetry
public class AopConfig {
}
