package com.trevorhinson.tests.trivia.config;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.retry.RetryOperations;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class RESTConfig {

    @Bean(name = "restOperations")
    public RestTemplate restTemplate(@Qualifier("httpRequestFactory") ClientHttpRequestFactory clientHttpRequestFactory) {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        messageConverters.add(new FormHttpMessageConverter());
        return restTemplate;
    }

    @Bean(name = "httpRequestFactory")
    public ClientHttpRequestFactory clientHttpRequestFactory(@Value("#{new Integer('${retry.connectionTimeout:10000}')}") Integer connectionTimeout,
                                                             @Value("#{new Integer('${retry.readTimeout:10000}')}") Integer readTimeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectionTimeout);
        factory.setReadTimeout(readTimeout);
        return factory;
    }

    @Bean(name = "retryPolicy")
    public RetryPolicy updateRetryPolicy(@Value("${retry.max-attempts:1}") Integer maxAttempts) {
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(maxAttempts);
        return retryPolicy;
    }

    @Bean(name = "retryOperations")
    public RetryOperations retryOperations(@Qualifier("retryPolicy") RetryPolicy retryPolicy,
                                           @Qualifier("retryBackOffPolicy") BackOffPolicy backOffPolicy) {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        return retryTemplate;
    }

    @Bean(name = "retryBackOffPolicy")
    public BackOffPolicy updateBackOffPolicy(@Value("${retry.back-off-period-ms:2000}") Long retryBackOffPeriodMs) {
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(retryBackOffPeriodMs);
        return backOffPolicy;
    }

    @Bean(name = "updatePropertySourcesPlaceholderConfigurer")
    public static PropertySourcesPlaceholderConfigurer updatePropertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @SneakyThrows
    @Bean(name = "updateClientHttpRequestFactory")
    public ClientHttpRequestFactory updateClientHttpRequestFactory(@Value("#{new Integer('${update.connection-timeout-ms:10000}')}") Integer connectionTimeoutMs,
                                                                   @Value("#{new Integer('${update.read-timeout-ms:10000}')}") Integer readTimeoutMs) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectionRequestTimeout(connectionTimeoutMs);
        factory.setConnectTimeout(readTimeoutMs);
        return factory;
    }

}
