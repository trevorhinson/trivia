package com.trevorhinson.tests.trivia.client;

import com.trevorhinson.tests.trivia.client.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.RetryOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
@Slf4j
public class OpenDBClient {

    @Value("${openDBClient.url}")
    private String url;

    private final RestTemplate restTemplate;
    private final RetryOperations retryOperations;

    public OpenDBClient(RestTemplate restTemplate, RetryOperations retryOperations) {
        this.restTemplate = restTemplate;
        this.retryOperations = retryOperations;
    }

    public Optional<ApiResponse> getTrivia(int amount) {
        return Optional.ofNullable(retryOperations.execute(context -> sendRequest(amount)));
    }

    ApiResponse sendRequest(int amount) {
        String fullUrl = url + amount;
        try {
            return restTemplate.getForObject(fullUrl, ApiResponse.class);
        } catch (HttpClientErrorException.Unauthorized ex) {
            log.error("Unauthorized access to OpenDB API", ex);
            throw ex;
        } catch (HttpClientErrorException.NotFound ex) {
            log.error("Trivia not found on OpenDB API", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to retrieve trivia from OpenDB API", ex);
            throw ex;
        }
    }

}
