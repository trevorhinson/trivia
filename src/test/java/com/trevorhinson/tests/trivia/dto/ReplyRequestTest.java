package com.trevorhinson.tests.trivia.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class ReplyRequestTest {

    @Test
    void shouldThrowIllegalArgumentExceptionWhenAnswerIsNull() {
        // Given
        ReplyRequest underTest = new ReplyRequest(null);

        // When
        Throwable exception = catchThrowable(() -> underTest.validate());

        // Then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Reply request must include an answer.");
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenAnswerIsEmptyString() {
        // Given
        ReplyRequest underTest = new ReplyRequest("");

        // When
        Throwable exception = catchThrowable(() -> underTest.validate());

        // Then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Reply request answer cannot be empty.");
    }
    
    @Test
    void shouldThrowIllegalArgumentExceptionWhenAnswerExceeds50Characters() {
        // Given
        String longAnswer = "This is a very long answer that exceeds the maximum allowed length of 50 characters.";
        ReplyRequest underTest = new ReplyRequest(longAnswer);

        // When
        Throwable exception = catchThrowable(() -> underTest.validate());

        // Then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Reply request answer cannot exceed 50 characters.");
    }
    
    @Test
    void shouldThrowIllegalArgumentExceptionWhenAnswerContainsProhibitedCharacters() {
        // Given
        String prohibitedAnswer = "<script>alert('XSS Attack')</script>";
        ReplyRequest underTest = new ReplyRequest(prohibitedAnswer);

        // When
        Throwable exception = catchThrowable(() -> underTest.validate());

        // Then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Reply request answer contains prohibited characters.");
    }
    
    @Test
    void shouldThrowIllegalArgumentExceptionWhenAnswerContainsProhibitedCharactersLikeCurlyBraces() {
        // Given
        String prohibitedAnswer = "{This is a prohibited answer}";
        ReplyRequest underTest = new ReplyRequest(prohibitedAnswer);
    
        // When
        Throwable exception = catchThrowable(() -> underTest.validate());
    
        // Then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Reply request answer contains prohibited characters.");
    }
    
    @Test
    void shouldThrowIllegalArgumentExceptionWhenAnswerContainsProhibitedCharactersLikeSlash() {
        // Given
        String prohibitedAnswer = "/This is a prohibited answer";
        ReplyRequest underTest = new ReplyRequest(prohibitedAnswer);

        // When
        Throwable exception = catchThrowable(() -> underTest.validate());

        // Then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Reply request answer contains prohibited characters.");
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenAnswerContainsProhibitedCharactersLikeBackslash() {
        // Given
        String prohibitedAnswer = "\\This is a prohibited answer";
        ReplyRequest underTest = new ReplyRequest(prohibitedAnswer);

        // When
        Throwable exception = catchThrowable(() -> underTest.validate());

        // Then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Reply request answer contains prohibited characters.");
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenAnswerContainsProhibitedCharactersLikeSemicolon() {
        // Given
        String prohibitedAnswer = "This is a prohibited answer; with a semicolon";
        ReplyRequest underTest = new ReplyRequest(prohibitedAnswer);

        // When
        Throwable exception = catchThrowable(() -> underTest.validate());

        // Then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Reply request answer contains prohibited characters.");
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenAnswerContainsProhibitedCharactersLikeParentheses() {
        // Given
        String prohibitedAnswer = "This is a prohibited (answer)";
        ReplyRequest underTest = new ReplyRequest(prohibitedAnswer);
    
        // When
        Throwable exception = catchThrowable(() -> underTest.validate());
    
        // Then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Reply request answer contains prohibited characters.");
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenAnswerContainsProhibitedCharactersLikeClosingParenthesis() {
        // Given
        String prohibitedAnswer = "This is a prohibited ) answer";
        ReplyRequest underTest = new ReplyRequest(prohibitedAnswer);

        // When
        Throwable exception = catchThrowable(() -> underTest.validate());

        // Then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Reply request answer contains prohibited characters.");
    }

    @Test
    void shouldValidateSuccessfullyWhenAllCriteriaAreMet() {
        // Given
        ReplyRequest underTest = new ReplyRequest("This is a valid answer with 49 characters.");

        // When // Then
        underTest.validate();
    }

}


