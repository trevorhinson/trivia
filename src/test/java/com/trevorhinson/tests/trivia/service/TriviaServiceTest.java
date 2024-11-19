package com.trevorhinson.tests.trivia.service;

import com.trevorhinson.tests.trivia.TestUtils;
import com.trevorhinson.tests.trivia.client.OpenDBClient;
import com.trevorhinson.tests.trivia.client.dto.ApiResponse;
import com.trevorhinson.tests.trivia.client.dto.Result;
import com.trevorhinson.tests.trivia.domain.Trivia;
import com.trevorhinson.tests.trivia.dto.*;
import com.trevorhinson.tests.trivia.repository.TriviaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.trevorhinson.tests.trivia.dto.Answer.RIGHT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TriviaServiceTest {

    @Mock
    private TriviaRepository triviaRepository;
    @Mock
    private OpenDBClient openDBClient;

    private TriviaService underTest;

    @Captor
    private ArgumentCaptor<Trivia> triviaCaptor;

    @BeforeEach
    void setUp() {
        underTest = new TriviaService(triviaRepository, openDBClient);
    }

    @Test
    void shouldStartTriviaWithCustomAmountOfQuestions() {
        // Given
        ApiResponse expectedResponse = TestUtils.createApiResponse();
        when(openDBClient.getTrivia(anyInt())).thenReturn(Optional.of(expectedResponse));
        when(triviaRepository.save(triviaCaptor.capture())).thenAnswer(i -> {
            Trivia savedTrivia = i.getArgument(0);
            savedTrivia.setTriviaId(1L);
            return savedTrivia;
        });
        
        // When
        TriviaResponse triviaResponse = underTest.startTrivia();

        // Then
        verify(openDBClient).getTrivia(1);
        assertThat(triviaResponse).isNotNull();
        final Result result = expectedResponse.getResults().get(0);
        assertThat(triviaResponse.getTriviaId()).isNotNull();
        assertThat(triviaResponse.getTriviaId()).isGreaterThan(0);
        assertThat(triviaResponse.getQuestion()).isEqualTo(result.getQuestion());
        List<String> possibleAnswers = new ArrayList<>();
        possibleAnswers.addAll(result.getIncorrect_answers());
        possibleAnswers.add(result.getCorrect_answer());
        assertThat(triviaResponse.getPossibleAnswers()).containsAll(possibleAnswers);
    }

    @Test
    void shouldHandleOpenDBApiFailureGracefully() {
        // Given
        when(openDBClient.getTrivia(anyInt())).thenReturn(Optional.empty());

        // Given
        TriviaResponse triviaResponse = underTest.startTrivia();

        // Then
        assertThat(triviaResponse).isNotNull();
        assertThat(triviaResponse.getTriviaId()).isNull();
        assertThat(triviaResponse.getQuestion()).isNull();
        assertThat(triviaResponse.getPossibleAnswers()).isNull();
    }

    @Test
    void shouldCreateTriviaItemsCorrectlyWhenApiResponseIsEmpty() {
        // Given
        ApiResponse emptyApiResponse = new ApiResponse();
        emptyApiResponse.setResponseCode(200);
        emptyApiResponse.setResults(Collections.emptyList());

        // When
        List<TriviaItem> triviaItems = underTest.createTriviaItemsFromApiResponse(Optional.of(emptyApiResponse));

        // Then
        assertThat(triviaItems).isEmpty();
    }

    @Test
    void shouldHandleNullTriviaItemInApiResponse() {
        // Given
        Result nullResult = null;
        ApiResponse apiResponse = TestUtils.createApiResponse();
        List<Result> results = new ArrayList<>();
        results.add(nullResult);
        apiResponse.setResults(results);

        // When
        List<TriviaItem> triviaItems = underTest.createTriviaItemsFromApiResponse(Optional.of(apiResponse));

        // Then
        assertThat(triviaItems).isEmpty();
        verify(triviaRepository, never()).save(any());
    }
    
    @Test
    void shouldShufflePossibleAnswersCorrectly() {
        // Given
        Result result = TestUtils.createResult();
        List<String> expectedPossibleAnswers = new ArrayList<>(result.getIncorrect_answers());
        expectedPossibleAnswers.add(result.getCorrect_answer());
    
        // When
        List<String> actualPossibleAnswers = underTest.createPossibleAnswers(result);

        // Then
        assertThat(actualPossibleAnswers).hasSize(expectedPossibleAnswers.size())
                .containsAll(expectedPossibleAnswers)
                .isNotEqualTo(expectedPossibleAnswers);
    }
    
    @Test
    void shouldFindTriviaByIdAndReplyRequestRightAnswer() {
        // Given
        String answer = "Correct Answer";
        Long triviaId = 1L;
        Trivia expectedTrivia = new Trivia();
        expectedTrivia.setTriviaId(triviaId);
        expectedTrivia.setQuestion("Question");
        expectedTrivia.setCorrectAnswer(answer);
        ReplyRequest replyRequest = new ReplyRequest();
        replyRequest.setAnswer(answer);
        when(triviaRepository.findById(triviaId)).thenReturn(Optional.of(expectedTrivia));

        // When
        final ReplyResponse replyResponse = underTest.reply(triviaId, replyRequest);

        // Then
        assertThat(replyResponse).isNotNull();
        assertThat(replyResponse.getResult()).isEqualTo(RIGHT.getMessage());
        verify(triviaRepository).delete(any());
    }

    @Test
    void shouldFindTriviaByIdAndReplyRequestWrongAnswer() {
        // Given
        String answer = "Correct Answer";
        Long triviaId = 1L;
        Trivia expectedTrivia = new Trivia();
        expectedTrivia.setTriviaId(triviaId);
        expectedTrivia.setQuestion("Question");
        expectedTrivia.setCorrectAnswer(answer);
        ReplyRequest replyRequest = new ReplyRequest();
        replyRequest.setAnswer("Not this.");
        when(triviaRepository.findById(triviaId)).thenReturn(Optional.of(expectedTrivia));

        // When
        final ReplyResponse replyResponse = underTest.reply(triviaId, replyRequest);

        // Then
        assertThat(replyResponse).isNotNull();
        assertThat(replyResponse.getResult()).isEqualTo(Answer.WRONG.getMessage());
        verify(triviaRepository).save(any());
    }

    @Test
    void shouldFindTriviaByIdAndReplyRequestNotFound() {
        // Given
        Long triviaId = 1L;
        Trivia expectedTrivia = new Trivia();
        expectedTrivia.setTriviaId(triviaId);
        expectedTrivia.setQuestion("Question");
        expectedTrivia.setAnswerAttempts(3);
        expectedTrivia.setCorrectAnswer("Doesnt matter");
        ReplyRequest replyRequest = new ReplyRequest();
        replyRequest.setAnswer("Not this.");
        when(triviaRepository.findById(triviaId)).thenReturn(Optional.of(expectedTrivia));

        // When
        final ReplyResponse replyResponse = underTest.reply(triviaId, replyRequest);

        // Then
        assertThat(replyResponse).isNotNull();
        assertThat(replyResponse.getResult()).isEqualTo(Answer.ATTEMPTS_EXCEEDED.getMessage());
    }

    @Test
    void shouldFindTriviaByIdAndReplyRequestMaxAttempts() {
        // Given
        Long triviaId = 1L;
        ReplyRequest replyRequest = new ReplyRequest();
        replyRequest.setAnswer("Not this.");
        when(triviaRepository.findById(triviaId)).thenReturn(Optional.empty());

        // When
        final ReplyResponse replyResponse = underTest.reply(triviaId, replyRequest);

        // Then
        assertThat(replyResponse).isNotNull();
        assertThat(replyResponse.getResult()).isEqualTo(Answer.NOT_FOUND.getMessage());
    }

    @Test
    void shouldSaveTriviaCorrectly() {
        // Given
        Result result = TestUtils.createResult();
        when(triviaRepository.save(any(Trivia.class))).thenAnswer(i -> {
            Trivia savedTrivia = i.getArgument(0);
            savedTrivia.setTriviaId(1L);
            return savedTrivia;
        });

        // When
        Trivia actualTrivia = underTest.createTrivaFromResult(result);

        // Then
        assertThat(actualTrivia).isNotNull();
        assertThat(actualTrivia.getTriviaId()).isEqualTo(1L);
    }

    @Test
    void shouldDeleteTriviaSuccessfully() {
        // Given
        Trivia trivia = new Trivia();
        trivia.setQuestion("Question");
        trivia.setAnswerAttempts(3);
        trivia.setCorrectAnswer("Doesnt matter");

        // When
        underTest.deleteById(trivia);

        // Then
        verify(triviaRepository).delete(trivia);
    }

}
