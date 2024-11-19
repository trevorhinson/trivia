package com.trevorhinson.tests.trivia.controller;

import com.trevorhinson.tests.trivia.dto.ReplyRequest;
import com.trevorhinson.tests.trivia.dto.ReplyResponse;
import com.trevorhinson.tests.trivia.dto.TriviaResponse;
import com.trevorhinson.tests.trivia.service.TriviaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.trevorhinson.tests.trivia.dto.Answer.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TriviaControllerTest {

    private TriviaController underTest;

    @Mock
    private TriviaService triviaService;

    @BeforeEach
    void beforeEach() {
        underTest = new TriviaController(triviaService);
    }

    @Test
    void shouldReturnTriviaResponseWhenStartTriviaEndpointIsCalled() {
        // Given
        TriviaResponse expectedResponse = new TriviaResponse();
        when(triviaService.startTrivia()).thenReturn(expectedResponse);

        // When
        ResponseEntity<TriviaResponse> response = underTest.startTrivia();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    void shouldReturnBadRequestWhenResultIsWrong() {
        // Given
        ReplyResponse replyResponse = new ReplyResponse();
        replyResponse.setResult(WRONG.getMessage());

        // When
        ResponseEntity<ReplyResponse> response = underTest.applyResponse(replyResponse);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(replyResponse);
    }

    @Test
    void shouldReturnNotFoundStatusWhenResultIsNotFound() {
        // Given
        ReplyResponse replyResponse = new ReplyResponse();
        replyResponse.setResult(NOT_FOUND.getMessage());

        // When
        ResponseEntity<ReplyResponse> response = underTest.applyResponse(replyResponse);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo(replyResponse);
    }
 
    @Test
    void shouldReturnForbiddenStatusWhenResultIsAttemptsExceeded() {
        // Given
        ReplyResponse replyResponse = new ReplyResponse();
        replyResponse.setResult(ATTEMPTS_EXCEEDED.getMessage());

        // When
        ResponseEntity<ReplyResponse> response = underTest.applyResponse(replyResponse);
    
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isEqualTo(replyResponse);
    }
    
    @Test
    void shouldReturnOkStatusWhenResultIsRight() {
        // Given
        ReplyResponse replyResponse = new ReplyResponse();
        replyResponse.setResult(RIGHT.getMessage());
    
        // When
        ResponseEntity<ReplyResponse> response = underTest.applyResponse(replyResponse);
    
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(replyResponse);
    }
    
    @Test
    void shouldThrowIllegalStateExceptionWhenResultIsUnexpected() {
        // Given
        ReplyResponse replyResponse = new ReplyResponse();
        replyResponse.setResult("UNEXPECTED_RESULT");

        // When // Then
        assertThatThrownBy(() -> underTest.applyResponse(replyResponse))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Unexpected result from trivia service.");
    }

    @Test
    void shouldThrowIllegalStateExceptionWhenResultIsEmptyString() {
        // Given
        ReplyResponse replyResponse = new ReplyResponse();
        replyResponse.setResult("");
    
        // When // Then
        assertThatThrownBy(() -> underTest.applyResponse(replyResponse))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Unexpected result from trivia service.");
    }

    @Test
    void shouldHandleCaseInsensitiveComparisonForReplyResult() {
        // Given
        ReplyResponse replyResponse = new ReplyResponse();
        replyResponse.setResult(WRONG.getMessage().toLowerCase());

        // When
        ResponseEntity<ReplyResponse> response = underTest.applyResponse(replyResponse);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(replyResponse);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenIdIsNull() {
        // Given
        Long nullId = null;

        // When // Then
        assertThatThrownBy(() -> underTest.validateId(nullId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid trivia id.");
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenIdIsLessThanOrEqualToZero() {
        // Given
        Long invalidId = 0L;
    
        // When // Then
        assertThatThrownBy(() -> underTest.validateId(invalidId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid trivia id.");
    }

    @Test
    void shouldNotThrowExceptionWhenIdIsPositiveInteger() {
        // Given
        Long positiveId = 1L;

        // When // Then
        assertThatNoException().isThrownBy(() -> underTest.validateId(positiveId));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenIdIsNegative() {
        // Given
        Long negativeId = -1L;

        // When // Then
        assertThatThrownBy(() -> underTest.validateId(negativeId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid trivia id.");
    }

    @Test
    void validateReplyRequest_shouldThrowIllegalArgumentException_whenReplyRequestIsNull() {
        // Given
        ReplyRequest nullReplyRequest = null;

        // When // Then
        assertThatThrownBy(() -> underTest.validateReplyRequest(nullReplyRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Reply request must include an answer.");
    }

}

