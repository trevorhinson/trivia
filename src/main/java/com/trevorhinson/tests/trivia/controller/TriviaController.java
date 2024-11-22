package com.trevorhinson.tests.trivia.controller;

import com.trevorhinson.tests.trivia.dto.Answer;
import com.trevorhinson.tests.trivia.dto.ReplyRequest;
import com.trevorhinson.tests.trivia.dto.ReplyResponse;
import com.trevorhinson.tests.trivia.dto.TriviaResponse;
import com.trevorhinson.tests.trivia.service.TriviaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.trevorhinson.tests.trivia.dto.Answer.*;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/trivia")
@Slf4j
public class TriviaController {

    private final TriviaService triviaService;
    private final Map<String, HttpStatus> responseStatusMapping = new HashMap<>();

    public TriviaController(TriviaService triviaService) {
        this.triviaService = triviaService;
        constructResponseStatusMapping();
    }

    @GetMapping("/gettrivia")
    public ResponseEntity<TriviaResponse> startTrivia() {
        return ResponseEntity.ok(triviaService.startTrivia());
    }

    @PutMapping(path = "/reply/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ReplyResponse> replyTrivia(@PathVariable Long id, @RequestBody ReplyRequest answerRequest) {
        validateId(id);
        validateReplyRequest(answerRequest);
        final ReplyResponse replyResponse = triviaService.reply(id, answerRequest);
        return applyResponse(replyResponse);
    }

    ResponseEntity<ReplyResponse> applyResponse(ReplyResponse replyResponse) {
        log.info("Applying response: {}", replyResponse);
        final HttpStatus status = responseStatusMapping.get(replyResponse.getResult());
        if (status == null) {
            log.error("Unexpected result from trivia service: {}", replyResponse.getResult());
            throw new IllegalStateException("Unexpected result from trivia service.");
        }
        return ResponseEntity.status(status).body(replyResponse);
    }

    void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid trivia id.");
        }
    }

    void validateReplyRequest(ReplyRequest replyRequest) {
        if (replyRequest == null) {
            throw new IllegalArgumentException("Reply request must include an answer.");
        }
        replyRequest.validate();
    }

    private void constructResponseStatusMapping() {
        responseStatusMapping.put(ATTEMPTS_EXCEEDED.getMessage(), FORBIDDEN);
        responseStatusMapping.put(WRONG.getMessage(), BAD_REQUEST);
        responseStatusMapping.put(Answer.NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
        responseStatusMapping.put(RIGHT.getMessage(), OK);
    }

}
