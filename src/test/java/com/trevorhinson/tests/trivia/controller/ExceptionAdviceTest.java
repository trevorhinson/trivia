package com.trevorhinson.tests.trivia.controller;

import com.trevorhinson.tests.trivia.dto.ErrorMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExceptionAdviceTest {

    @Mock
    private WebRequest webRequest;

    @InjectMocks
    private ExceptionAdvice underTest;

    @BeforeEach
    void beforeEach() {
        webRequest = mock(WebRequest.class);
    }

    @Test
    void shouldHandleIllegalArgumentExceptionWhenRequiredInputIsMissing() {
        // Given
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Required input is missing");

        // When
        ResponseEntity<ErrorMessage> response = underTest.illegalArgumentException(illegalArgumentException, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getResult()).isEqualTo("Required input is missing");
    }

    @Test
    void shouldReturn400BadRequestForIllegalArgumentException() {
        // Given
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Required input is missing");

        // When
        ResponseEntity<ErrorMessage> response = underTest.illegalArgumentException(illegalArgumentException, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    
    @Test
    void shouldHandleExhaustedRetryExceptionWhenRetryAttemptsAreExhausted() {
        // Given
        ExhaustedRetryException exhaustedRetryException = new ExhaustedRetryException("Retry attempts exhausted");

        // When
        ResponseEntity<ErrorMessage> response = underTest.exhaustedRetryException(exhaustedRetryException, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(response.getBody().getResult()).isEqualTo("Retry attempts exhausted. Please try again later.");
    }

    @Test
    void shouldHandleIllegalStateExceptionWhenAnIllegalStateOccurs() {
        // Given
        IllegalStateException illegalStateException = new IllegalStateException("Illegal state encountered");

        // When
        ResponseEntity<ErrorMessage> response = underTest.illegalStateException(illegalStateException, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getResult()).isEqualTo("Illegal state encountered");
    }

    @Test
    void shouldReturn500InternalServerErrorForIllegalStateException() {
        // Given
        IllegalStateException illegalStateException = new IllegalStateException("Illegal state encountered");

        // When
        ResponseEntity<ErrorMessage> response = underTest.illegalStateException(illegalStateException, webRequest);
    
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getResult()).isEqualTo("Illegal state encountered");
    }

    @Test
    void shouldHandleUnexpectedExceptionsThatAreNotExplicitlyHandled() {
        // Given
        Exception unexpectedException = new Exception("An unexpected error occurred");

        // When
        ResponseEntity<ErrorMessage> response = underTest.unexpectedException(unexpectedException, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getResult()).isEqualTo("An unexpected error occurred. Please try again later.");
    }

}
