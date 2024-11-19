package com.trevorhinson.tests.trivia.client;

import com.trevorhinson.tests.trivia.TestUtils;
import com.trevorhinson.tests.trivia.client.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.retry.RetryOperations;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OpenDBClientTest {

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private RetryOperations retryOperations;

    private String testUrl = "https://opentdb.com/api.php?amount=";

    private OpenDBClient underTest;

    @BeforeEach
    void setUp() throws Exception {
        underTest = new OpenDBClient(restTemplate, retryOperations);
        TestUtils.setFieldValue(underTest, "url", testUrl);
    }

    @Test
    public void shouldSendRequest() {
        // Given
        int amount = 1;
        ApiResponse expectedResponse = TestUtils.createApiResponse();
        when(restTemplate.getForObject(testUrl + amount, ApiResponse.class)).thenReturn(expectedResponse);

        // Given
        ApiResponse response = underTest.sendRequest(amount);

        // Then
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    public void shouldGetTriviaExhaustedRetryException() {
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
