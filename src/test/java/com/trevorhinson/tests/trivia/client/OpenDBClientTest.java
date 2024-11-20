package com.trevorhinson.tests.trivia.client;

import com.trevorhinson.tests.trivia.client.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.retry.RetryOperations;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenDBClientTest {

    @Mock
    private WebClient.Builder webClientBuilder;
    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private ResponseSpec responseSpec;
    @Mock
    private RetryOperations retryOperations;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    private OpenDBClient underTest;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.build()).thenReturn(webClient);
        underTest = new OpenDBClient(webClientBuilder, retryOperations);
    }

    @Test
    void testSendRequest() {
        // Given
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        int amount = 10;
        ApiResponse mockResponse = new ApiResponse();
        when(responseSpec.bodyToMono(ApiResponse.class)).thenReturn(Mono.just(mockResponse));

        // When
        ApiResponse response = underTest.sendRequest(amount);

        // Then
        assertEquals(mockResponse, response);
        verify(webClient).get();
    }

    @Test
    void shouldGetTriviaExhaustedRetryException() {
        // Given
        int amount = 1;
        when(retryOperations.execute(any()))
                .thenThrow(new ExhaustedRetryException("Retry attempts exhausted"));

        // Given ... Then
        assertThatThrownBy(() -> underTest.getTrivia(amount))
                .isInstanceOf(ExhaustedRetryException.class)
                .hasMessageContaining("Retry attempts exhausted");
    }

}
