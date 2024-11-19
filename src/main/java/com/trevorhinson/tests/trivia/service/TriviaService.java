package com.trevorhinson.tests.trivia.service;

import com.trevorhinson.tests.trivia.client.OpenDBClient;
import com.trevorhinson.tests.trivia.client.dto.ApiResponse;
import com.trevorhinson.tests.trivia.client.dto.Result;
import com.trevorhinson.tests.trivia.domain.Trivia;
import com.trevorhinson.tests.trivia.dto.*;
import com.trevorhinson.tests.trivia.repository.TriviaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.trevorhinson.tests.trivia.dto.Answer.*;

@Service
@AllArgsConstructor
@Slf4j
public class TriviaService {

    public static final int ANSWER_ATTEMPTS = 3;  // Typically this would be configurable instead of hardcoding.

    private TriviaRepository triviaRepository;
    private OpenDBClient openDBClient;

    public TriviaResponse startTrivia() {
        TriviaResponse triviaResponse = new TriviaResponse();
        log.info("Fetching trivia from OpenDB... restricted to 1 as per requirement - though not really clear!");
        final Optional<ApiResponse> optionalResponse = openDBClient.getTrivia(1);
        if (optionalResponse.isPresent()) {
            final List<TriviaItem> triviaItems = createTriviaItemsFromApiResponse(optionalResponse);
            if (!triviaItems.isEmpty()) {
                final TriviaItem triviaItem = triviaItems.get(0);
                triviaResponse.setTriviaId(triviaItem.getTriviaId());
                triviaResponse.setQuestion(triviaItem.getQuestion());
                triviaResponse.setPossibleAnswers(triviaItem.getPossibleAnswers());
            }
        }
        return triviaResponse;
    }

    public ReplyResponse reply(Long id, ReplyRequest replyRequest) {
        ReplyResponse replyResponse = new ReplyResponse();
        log.info("Finding trivia by id: {}", id);
        final Optional<Trivia> byId = triviaRepository.findById(id);
        if (byId.isPresent()) {
            final Trivia trivia = byId.get();
            final String correctAnswer = trivia.getCorrectAnswer();
            if (correctAnswer.equalsIgnoreCase(replyRequest.getAnswer())) {
                replyResponse.setResult(RIGHT.getMessage());
                deleteById(trivia);
            } else {
                if (trivia.getAnswerAttempts() >= ANSWER_ATTEMPTS) {
                    log.info("Max attempts reached for trivia: {}", trivia);
                    replyResponse.setResult(ATTEMPTS_EXCEEDED.getMessage());
                } else {
                    replyResponse.setResult(WRONG.getMessage());
                    recordFailedAttempt(trivia);
                }
            }
        } else {
            log.error("Trivia not found for id: {}", id);
            replyResponse.setResult(NOT_FOUND.getMessage());
        }
        return replyResponse;
    }

    void recordFailedAttempt(Trivia trivia) {
        trivia.setAnswerAttempts(trivia.getAnswerAttempts() + 1);
        log.info("Recording failed attempt for trivia: {}", trivia);
        save(trivia);
    }

    public Trivia save(Trivia trivia) {
        log.info("Saving trivia: {}", trivia);
        return triviaRepository.save(trivia);
    }

    public void deleteById(Trivia trivia) {
        log.info("Deleting trivia with id: {}", trivia.getTriviaId());
        triviaRepository.delete(trivia);
    }

    List<TriviaItem> createTriviaItemsFromApiResponse(Optional<ApiResponse> optionalApiResponse) {
        List<TriviaItem> triviaList = new ArrayList<>();
        if (optionalApiResponse.isEmpty()) {
            return triviaList;
        }
        optionalApiResponse.get().getResults().forEach(result -> {
            if (result != null) {
                Trivia trivia = createTrivaFromResult(result);
                final TriviaItem triviaItem = createTriviaItemFromTrivia(trivia, result);
                log.info("Adding TriviaItem {} to response.", triviaItem);
                triviaList.add(triviaItem);
            } else {
                log.warn("Skipping trivia item with null question or answer.");
            }
        });
        return triviaList;
    }

    Trivia createTrivaFromResult(Result result) {
        Trivia trivia = new Trivia();
        trivia.setQuestion(result.getQuestion());
        trivia.setCorrectAnswer(result.getCorrect_answer());
        trivia.setAnswerAttempts(0);
        final Trivia saved = save(trivia);
        log.info("Created Trivia {}", saved);
        return saved;
    }

    private TriviaItem createTriviaItemFromTrivia(Trivia trivia, Result result) {
        return TriviaItem.builder().triviaId(trivia.getTriviaId()).question(trivia.getQuestion())
                .possibleAnswers(createPossibleAnswers(result))
                .build();
    }

    List<String> createPossibleAnswers(Result result) {
        List<String> possibleAnswers = result.getIncorrect_answers();
        possibleAnswers.add(result.getCorrect_answer());
        Collections.shuffle(possibleAnswers);
        return possibleAnswers;
    }

}
